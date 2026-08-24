package com.easyui.companion.network

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.easyui.companion.BuildConfig
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object CompanionBackendClient {
    private val DEFAULT_BASE_URL = BuildConfig.BACKEND_BASE_URL
    private const val TIMEOUT_MS = 25_000

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    var baseUrl: String = DEFAULT_BASE_URL
    var deviceToken: String? = null

    suspend fun pairWithSenior(code: String, caregiverDeviceId: String): PairResponseDto? {
        val payload = PairRequestDto(code = code, caregiverDeviceId = caregiverDeviceId)
        val body = json.encodeToString(payload)
        val response = postJson("/pair", body, null, PairResponseDto::class.java)
        if (response != null) {
            deviceToken = response.deviceToken
        }
        return response
    }

    suspend fun fetchStatus(seniorDeviceId: String): StatusResponseDto? {
        return getJson("/status/$seniorDeviceId", deviceToken, StatusResponseDto::class.java)
    }

    /**
     * Like [fetchStatus], but preserves *why* the fetch failed — 403 (permission not granted
     * or revoked) and 404 (senior hasn't reported status yet) are otherwise indistinguishable
     * from a plain network failure to the caller.
     */
    suspend fun fetchStatusResult(seniorDeviceId: String): SeniorStatusResult {
        val (code, data) = getJsonWithStatus("/status/$seniorDeviceId", deviceToken, StatusResponseDto::class.java)
        return when {
            data != null -> SeniorStatusResult.Success(data)
            code == 403 -> SeniorStatusResult.NotAuthorized
            code == 404 -> SeniorStatusResult.NoStatusYet
            else -> SeniorStatusResult.NetworkError
        }
    }

    suspend fun fetchCheckIn(seniorDeviceId: String): CheckInPayloadDto? {
        return getJson("/checkin/$seniorDeviceId", deviceToken, CheckInPayloadDto::class.java)
    }

    suspend fun fetchAlerts(seniorDeviceId: String): AlertListResponseDto? {
        return getJson("/alerts/$seniorDeviceId", deviceToken, AlertListResponseDto::class.java)
    }

    suspend fun sendConfig(seniorDeviceId: String, reminders: List<RemoteReminderDto>): Boolean {
        val payload = ConfigPayloadDto(reminders)
        val body = json.encodeToString(payload)
        return postRaw("/config/$seniorDeviceId", body, deviceToken) == true
    }

    suspend fun deleteAccount(): Boolean {
        val token = deviceToken ?: return false
        return postRaw("/delete-account", "", token) == true
    }

    private suspend fun <T> postJson(path: String, body: String, token: String?, clazz: Class<T>): T? = withContext(Dispatchers.IO) {
        try {
            val conn = openConnection(path, "POST", token)
            conn.outputStream.use { os ->
                OutputStreamWriter(os).use { w -> w.write(body) }
            }
            val code = conn.responseCode
            if (code in 200..299) {
                val response = conn.inputStream.bufferedReader().readText()
                val result = when (clazz) {
                    PairResponseDto::class.java -> json.decodeFromString<PairResponseDto>(response)
                    else -> throw IllegalArgumentException("Unknown DTO class: ${clazz.name}")
                }
                @Suppress("UNCHECKED_CAST")
                result as T
            } else {
                Log.w("CompanionBackend", "POST $path returned $code")
                null
            }
        } catch (e: Exception) {
            Log.e("CompanionBackend", "POST $path failed: ${e.message}")
            null
        }
    }

    private suspend fun postRaw(path: String, body: String, token: String?): Boolean? = withContext(Dispatchers.IO) {
        try {
            val conn = openConnection(path, "POST", token)
            conn.outputStream.use { os ->
                OutputStreamWriter(os).use { w -> w.write(body) }
            }
            val code = conn.responseCode
            if (code in 200..299) true else {
                Log.w("CompanionBackend", "POST $path returned $code")
                false
            }
        } catch (e: Exception) {
            Log.e("CompanionBackend", "POST $path failed: ${e.message}")
            null
        }
    }

    private suspend fun <T> getJson(path: String, token: String?, clazz: Class<T>): T? =
        getJsonWithStatus(path, token, clazz).second

    /** Returns the HTTP status code alongside the decoded body (0 = no response / network error). */
    private suspend fun <T> getJsonWithStatus(path: String, token: String?, clazz: Class<T>): Pair<Int, T?> = withContext(Dispatchers.IO) {
        try {
            val conn = openConnection(path, "GET", token)
            val code = conn.responseCode
            if (code in 200..299) {
                val response = conn.inputStream.bufferedReader().readText()
                val result = when (clazz) {
                    StatusResponseDto::class.java -> json.decodeFromString<StatusResponseDto>(response)
                    CheckInPayloadDto::class.java -> json.decodeFromString<CheckInPayloadDto>(response)
                    AlertListResponseDto::class.java -> json.decodeFromString<AlertListResponseDto>(response)
                    else -> throw IllegalArgumentException("Unknown DTO class: ${clazz.name}")
                }
                @Suppress("UNCHECKED_CAST")
                code to (result as T)
            } else {
                Log.w("CompanionBackend", "GET $path returned $code")
                code to null
            }
        } catch (e: Exception) {
            Log.e("CompanionBackend", "GET $path failed: ${e.message}")
            0 to null
        }
    }

    private fun openConnection(path: String, method: String, token: String?): HttpURLConnection {
        val url = URL("$baseUrl$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer $token")
        }
        conn.connectTimeout = TIMEOUT_MS
        conn.readTimeout = TIMEOUT_MS
        if (method == "POST" || method == "PUT") {
            conn.doOutput = true
        }
        return conn
    }
}

@Serializable
data class PairRequestDto(val code: String, val caregiverDeviceId: String)

@Serializable
data class PairResponseDto(
    val deviceToken: String,
    val seniorDeviceId: String,
    val permissions: List<String>
)

sealed class SeniorStatusResult {
    data class Success(val status: StatusResponseDto) : SeniorStatusResult()
    data object NotAuthorized : SeniorStatusResult() // 403: permission not granted or revoked
    data object NoStatusYet : SeniorStatusResult() // 404: senior hasn't reported status yet
    data object NetworkError : SeniorStatusResult() // request failed or returned an unexpected code
}

@Serializable
data class StatusResponseDto(
    val seniorDeviceId: String,
    val lastSeen: Long,
    val batteryLevel: Int,
    val isCharging: Boolean
)

@Serializable
data class CheckInPayloadDto(
    val timestamp: Long,
    val message: String = "I'm OK"
)

@Serializable
data class StoredAlertDto(
    val alertId: String,
    val type: String,
    val timestamp: Long,
    val details: String,
    val seen: Boolean
)

@Serializable
data class AlertListResponseDto(val alerts: List<StoredAlertDto>)

@Serializable
data class ConfigPayloadDto(val reminders: List<RemoteReminderDto>)

@Serializable
data class RemoteReminderDto(
    val id: String,
    val title: String,
    val type: String,
    val time: String
)
