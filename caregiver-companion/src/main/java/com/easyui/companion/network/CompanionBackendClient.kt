package com.easyui.companion.network

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object CompanionBackendClient {
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"
    private const val TIMEOUT_MS = 8_000

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

    private fun <T> postJson(path: String, body: String, token: String?, clazz: Class<T>): T? {
        return try {
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

    private fun postRaw(path: String, body: String, token: String?): Boolean? {
        return try {
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

    private fun <T> getJson(path: String, token: String?, clazz: Class<T>): T? {
        return try {
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
                result as T
            } else {
                Log.w("CompanionBackend", "GET $path returned $code")
                null
            }
        } catch (e: Exception) {
            Log.e("CompanionBackend", "GET $path failed: ${e.message}")
            null
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
    val message: String
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
