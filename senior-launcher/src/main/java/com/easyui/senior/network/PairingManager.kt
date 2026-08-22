package com.easyui.senior.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyui.senior.storage.coreDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Manages the pairing lifecycle for the Senior Launcher:
 *
 * 1. Generates a local seniorDeviceId (persisted in DataStore)
 * 2. Initiates pairing with the backend to get a short-lived pairing code
 * 3. Displays the code to the senior user for sharing with caregiver
 * 4. After caregiver pairs, receives a device token from the backend
 * 5. Persists the device token for subsequent API calls
 * 6. Surfaces current pairing status (paired / pending / none)
 */
class PairingManager(private val context: Context) {

    companion object {
        private val KEY_SENIOR_DEVICE_ID = stringPreferencesKey("pairing_senior_device_id")
        private val KEY_DEVICE_TOKEN = stringPreferencesKey("pairing_device_token")
        private val KEY_PAIRING_CODE = stringPreferencesKey("pairing_pending_code")
        private val KEY_PAIRING_CODE_EXPIRES = longPreferencesKey("pairing_code_expires_at")
        private val KEY_PAIRING_COMPLETION_SECRET = stringPreferencesKey("pairing_completion_secret")
        private val KEY_PERMISSIONS = stringPreferencesKey("pairing_permissions")

        const val PERMISSION_BATTERY = "battery"
        const val PERMISSION_CHECKIN = "checkin"
        const val PERMISSION_CONFIG = "config"
        const val PERMISSION_ALERTS = "alerts"
    }

    /** Current pairing state, read from DataStore */
    suspend fun getState(): PairingState = withContext(Dispatchers.IO) {
        val prefs = context.coreDataStore.data.first()
        val seniorDeviceId = prefs[KEY_SENIOR_DEVICE_ID] ?: ensureDeviceId()
        val token = prefs[KEY_DEVICE_TOKEN]
        val code = prefs[KEY_PAIRING_CODE]
        val codeExpiry = prefs[KEY_PAIRING_CODE_EXPIRES] ?: 0L
        val permissions = prefs[KEY_PERMISSIONS]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

        PairingState(
            seniorDeviceId = seniorDeviceId,
            deviceToken = token,
            pendingPairingCode = code?.takeIf { codeExpiry > System.currentTimeMillis() },
            permissions = permissions,
            isPaired = token != null
        )
    }

    /** Request a new pairing code from the backend */
    suspend fun requestPairingCode(): String? = withContext(Dispatchers.IO) {
        val seniorDeviceId = ensureDeviceId()
        val result = BackendClient.initiatePairing(seniorDeviceId)
        if (result != null) {
            context.coreDataStore.edit { prefs ->
                prefs[KEY_PAIRING_CODE] = result.code
                prefs[KEY_PAIRING_CODE_EXPIRES] = result.expiresAt
                prefs[KEY_PAIRING_COMPLETION_SECRET] = result.completionSecret
            }
            result.code
        } else null
    }

    /** Apply token after caregiver has confirmed pairing */
    suspend fun applyDeviceToken(token: String, permissions: List<String>) = withContext(Dispatchers.IO) {
        BackendClient.deviceToken = token
        context.coreDataStore.edit { prefs ->
            prefs[KEY_DEVICE_TOKEN] = token
            prefs[KEY_PERMISSIONS] = permissions.joinToString(",")
            prefs.remove(KEY_PAIRING_CODE)
            prefs.remove(KEY_PAIRING_CODE_EXPIRES)
            prefs.remove(KEY_PAIRING_COMPLETION_SECRET)
        }
    }

    /** Complete a caregiver-initiated pairing using the secret retained only on this senior device. */
    suspend fun refreshPairingCompletion(): Boolean = withContext(Dispatchers.IO) {
        val prefs = context.coreDataStore.data.first()
        if (prefs[KEY_DEVICE_TOKEN] != null) return@withContext true
        val seniorDeviceId = prefs[KEY_SENIOR_DEVICE_ID] ?: return@withContext false
        val completionSecret = prefs[KEY_PAIRING_COMPLETION_SECRET] ?: return@withContext false
        val completion = BackendClient.getPairingCompletion(seniorDeviceId, completionSecret)
            ?: return@withContext false
        applyDeviceToken(completion.deviceToken, completion.permissions)
        StatusReportWorker.enqueueImmediate(context)
        true
    }

    /** Revoke pairing — clears all pairing data locally and remote */
    suspend fun revokePairing() = withContext(Dispatchers.IO) {
        val token = getState().deviceToken
        if (token != null) {
            try {
                BackendClient.revokePairing()
            } catch (e: Exception) {
                // Ignore network error to guarantee local cleanup works offline-first
            }
        }
        BackendClient.deviceToken = null
        context.coreDataStore.edit { prefs ->
            prefs.remove(KEY_DEVICE_TOKEN)
            prefs.remove(KEY_PAIRING_CODE)
            prefs.remove(KEY_PAIRING_CODE_EXPIRES)
            prefs.remove(KEY_PAIRING_COMPLETION_SECRET)
            prefs.remove(KEY_PERMISSIONS)
        }
    }

    /** Clear remote device records and local pairing settings */
    suspend fun deleteDeviceData() = withContext(Dispatchers.IO) {
        val token = getState().deviceToken
        if (token != null) {
            try {
                BackendClient.deleteDeviceData()
            } catch (e: Exception) {
                // Ignore network error to guarantee local cleanup works offline-first
            }
        }
        revokePairing()
    }

    /** Check if a specific permission is granted */
    suspend fun hasPermission(permission: String): Boolean {
        return getState().permissions.contains(permission)
    }

    /** Load saved token into BackendClient on app start */
    suspend fun restoreSession() = withContext(Dispatchers.IO) {
        val state = getState()
        if (state.deviceToken != null) {
            BackendClient.deviceToken = state.deviceToken
        }
    }

    private suspend fun ensureDeviceId(): String {
        val prefs = context.coreDataStore.data.first()
        val existing = prefs[KEY_SENIOR_DEVICE_ID]
        if (existing != null) return existing
        val newId = UUID.randomUUID().toString()
        context.coreDataStore.edit { it[KEY_SENIOR_DEVICE_ID] = newId }
        return newId
    }
}

data class PairingState(
    val seniorDeviceId: String,
    val deviceToken: String?,
    val pendingPairingCode: String?,
    val permissions: List<String>,
    val isPaired: Boolean
)
