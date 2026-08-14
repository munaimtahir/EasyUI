package com.easyui.companion.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

val Context.companionDataStore by preferencesDataStore(name = "companion_settings")

class CompanionSession(private val context: Context) {

    companion object {
        private val KEY_CAREGIVER_DEVICE_ID = stringPreferencesKey("caregiver_device_id")
        private val KEY_DEVICE_TOKEN = stringPreferencesKey("device_token")
        private val KEY_SENIOR_DEVICE_ID = stringPreferencesKey("linked_senior_device_id")
        private val KEY_PERMISSIONS = stringPreferencesKey("linked_permissions")
    }

    suspend fun getSession(): CompanionSessionState = withContext(Dispatchers.IO) {
        val prefs = context.companionDataStore.data.first()
        val caregiverDeviceId = prefs[KEY_CAREGIVER_DEVICE_ID] ?: ensureCaregiverDeviceId()
        val token = prefs[KEY_DEVICE_TOKEN]
        val seniorDeviceId = prefs[KEY_SENIOR_DEVICE_ID]
        val permissions = prefs[KEY_PERMISSIONS]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

        CompanionSessionState(
            caregiverDeviceId = caregiverDeviceId,
            deviceToken = token,
            linkedSeniorDeviceId = seniorDeviceId,
            permissions = permissions,
            isPaired = token != null && seniorDeviceId != null
        )
    }

    suspend fun saveSession(token: String, seniorDeviceId: String, permissions: List<String>) = withContext(Dispatchers.IO) {
        context.companionDataStore.edit { prefs ->
            prefs[KEY_DEVICE_TOKEN] = token
            prefs[KEY_SENIOR_DEVICE_ID] = seniorDeviceId
            prefs[KEY_PERMISSIONS] = permissions.joinToString(",")
        }
    }

    suspend fun clearSession() = withContext(Dispatchers.IO) {
        context.companionDataStore.edit { prefs ->
            prefs.remove(KEY_DEVICE_TOKEN)
            prefs.remove(KEY_SENIOR_DEVICE_ID)
            prefs.remove(KEY_PERMISSIONS)
        }
    }

    private suspend fun ensureCaregiverDeviceId(): String {
        val prefs = context.companionDataStore.data.first()
        val existing = prefs[KEY_CAREGIVER_DEVICE_ID]
        if (existing != null) return existing
        val newId = UUID.randomUUID().toString()
        context.companionDataStore.edit { it[KEY_CAREGIVER_DEVICE_ID] = newId }
        return newId
    }
}

data class CompanionSessionState(
    val caregiverDeviceId: String,
    val deviceToken: String?,
    val linkedSeniorDeviceId: String?,
    val permissions: List<String>,
    val isPaired: Boolean
)
