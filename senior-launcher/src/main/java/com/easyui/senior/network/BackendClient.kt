package com.easyui.senior.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import com.easyui.senior.BuildConfig

/**
 * Lightweight HTTP client for the Senior Launcher.
 * Uses only java.net (no OkHttp dependency needed) to keep the APK minimal.
 * All calls are suspending and must be called from a coroutine.
 */
object BackendClient {

    private val DEFAULT_BASE_URL = BuildConfig.BACKEND_BASE_URL
    private const val TIMEOUT_MS = 25_000

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    var baseUrl: String = DEFAULT_BASE_URL
    var deviceToken: String? = null

    // ─── Pairing ────────────────────────────────────────────────────────────

    suspend fun initiatePairing(seniorDeviceId: String): PairingTokenDto? {
        val body = """{"seniorDeviceId":"$seniorDeviceId"}"""
        return postJson("/initiate-pairing", body, null, PairingTokenDto::class.java)
    }

    suspend fun completePairing(code: String, caregiverDeviceId: String): PairResponseDto? {
        val payload = PairRequestDto(code = code, caregiverDeviceId = caregiverDeviceId)
        val body = json.encodeToString(payload)
        return postJson("/pair", body, null, PairResponseDto::class.java)
    }

    suspend fun getPairingCompletion(seniorDeviceId: String, completionSecret: String): PairingCompletionDto? {
        return getJson(
            "/pairing-status/$seniorDeviceId?secret=$completionSecret",
            null,
            PairingCompletionDto::class.java
        )
    }

    // ─── Status ─────────────────────────────────────────────────────────────

    suspend fun postStatus(batteryLevel: Int, isCharging: Boolean, appVersion: String): Boolean {
        val payload = StatusPayloadDto(
            batteryLevel = batteryLevel,
            isCharging = isCharging,
            appVersion = appVersion,
            syncTimestamp = System.currentTimeMillis()
        )
        val body = json.encodeToString(payload)
        return postRaw("/status", body, deviceToken) == true
    }

    // ─── Check-in ───────────────────────────────────────────────────────────

    suspend fun postCheckIn(message: String = "I'm OK"): Boolean {
        val payload = CheckInPayloadDto(
            timestamp = System.currentTimeMillis(),
            message = message
        )
        val body = json.encodeToString(payload)
        return postRaw("/checkin", body, deviceToken) == true
    }

    // ─── Alerts ─────────────────────────────────────────────────────────────

    suspend fun postAlert(type: String, details: String = ""): Boolean {
        val payload = AlertPayloadDto(
            type = type,
            timestamp = System.currentTimeMillis(),
            details = details
        )
        val body = json.encodeToString(payload)
        return postRaw("/alert", body, deviceToken) == true
    }

    // ─── Config ─────────────────────────────────────────────────────────────

    /** Called by senior launcher to fetch pending caregiver-pushed config */
    suspend fun fetchConfig(): ConfigPayloadDto? {
        return getJson("/config", deviceToken, ConfigPayloadDto::class.java)
    }

    suspend fun revokePairing(): Boolean {
        val token = deviceToken ?: return false
        return postRaw("/revoke", "", token) == true
    }

    suspend fun deleteDeviceData(): Boolean {
        val token = deviceToken ?: return false
        return postRaw("/delete-device", "", token) == true
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
                    PairingTokenDto::class.java -> json.decodeFromString<PairingTokenDto>(response)
                    PairResponseDto::class.java -> json.decodeFromString<PairResponseDto>(response)
                    else -> throw IllegalArgumentException("Unknown DTO class: ${clazz.name}")
                }
                @Suppress("UNCHECKED_CAST")
                result as T
            } else {
                Log.w("BackendClient", "POST $path returned $code")
                null
            }
        } catch (e: Exception) {
            Log.e("BackendClient", "POST $path failed: ${e.message}")
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
                Log.w("BackendClient", "POST $path returned $code")
                false
            }
        } catch (e: Exception) {
            Log.e("BackendClient", "POST $path failed: ${e.message}")
            null
        }
    }

    private suspend fun <T> getJson(path: String, token: String?, clazz: Class<T>): T? = withContext(Dispatchers.IO) {
        try {
            val conn = openConnection(path, "GET", token)
            val code = conn.responseCode
            if (code in 200..299) {
                val response = conn.inputStream.bufferedReader().readText()
                val result = when (clazz) {
                    ConfigPayloadDto::class.java -> json.decodeFromString<ConfigPayloadDto>(response)
                    PairingCompletionDto::class.java -> json.decodeFromString<PairingCompletionDto>(response)
                    else -> throw IllegalArgumentException("Unknown DTO class: ${clazz.name}")
                }
                @Suppress("UNCHECKED_CAST")
                result as T
            } else {
                Log.w("BackendClient", "GET $path returned $code")
                null
            }
        } catch (e: Exception) {
            Log.e("BackendClient", "GET $path failed: ${e.message}")
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

// ─── DTO Models (kotlinx.serialization) ─────────────────────────────────────

@Serializable
data class PairRequestDto(val code: String, val caregiverDeviceId: String)

@Serializable
data class PairResponseDto(
    val deviceToken: String,
    val seniorDeviceId: String,
    val permissions: List<String>
)

@Serializable
data class PairingTokenDto(
    val code: String,
    val seniorDeviceId: String,
    val expiresAt: Long,
    val completionSecret: String
)

@Serializable
data class PairingCompletionDto(
    val seniorDeviceId: String,
    val deviceToken: String,
    val permissions: List<String>
)

@Serializable
data class StatusPayloadDto(
    val batteryLevel: Int,
    val isCharging: Boolean,
    val appVersion: String,
    val syncTimestamp: Long
)

@Serializable
data class CheckInPayloadDto(
    val timestamp: Long,
    val message: String = "I'm OK"
)

@Serializable
data class AlertPayloadDto(
    val type: String,
    val timestamp: Long,
    val details: String = ""
)

@Serializable
data class ConfigPayloadDto(
    val reminders: List<RemoteReminderDto> = emptyList()
)

@Serializable
data class RemoteReminderDto(
    val id: String,
    val title: String,
    val type: String,
    val time: String
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
data class StatusResponseDto(
    val seniorDeviceId: String,
    val lastSeen: Long,
    val batteryLevel: Int,
    val isCharging: Boolean,
    val lastCheckIn: Long = 0L
)
