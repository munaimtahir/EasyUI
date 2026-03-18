package com.easyui.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.repository.LauncherSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class DataStoreLauncherSettingsRepository(
    context: Context,
) : LauncherSettingsRepository {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("launcher_settings.preferences_pb") },
    )

    override val settings: Flow<LauncherSettings> =
        dataStore.data.map { preferences ->
            LauncherSettings(
                onboardingComplete = preferences[Keys.ONBOARDING_COMPLETE] ?: false,
                emergencyPhoneNumber = preferences[Keys.EMERGENCY_PHONE_NUMBER] ?: "911",
                emergencyNumbers = decodeEmergencyNumbers(preferences[Keys.EMERGENCY_NUMBERS]),
                sosNumbers = decodeSosNumbers(preferences[Keys.SOS_NUMBERS]),
                use24HourClock = preferences[Keys.USE_24_HOUR_CLOCK] ?: false,
                caregiverProtectionEnabled = preferences[Keys.CAREGIVER_PROTECTION_ENABLED] ?: false,
                layoutLocked = preferences[Keys.LAYOUT_LOCKED] ?: false,
                easyUiLockEnabled = preferences[Keys.EASYUI_LOCK_ENABLED] ?: false,
                easyUiLockTimeoutSeconds = preferences[Keys.EASYUI_LOCK_TIMEOUT_SECONDS] ?: 60,
                pinSaltHex = preferences[Keys.PIN_SALT_HEX],
                pinHashHex = preferences[Keys.PIN_HASH_HEX],
                appVisibilityPreset = preferences[Keys.APP_VISIBILITY_PRESET] ?: "CUSTOM",
                homeReadabilityPreset = preferences[Keys.HOME_READABILITY_PRESET] ?: "STANDARD",
                verySimpleModeEnabled = preferences[Keys.VERY_SIMPLE_MODE_ENABLED] ?: false,
                showBatteryInfo = preferences[Keys.SHOW_BATTERY_INFO] ?: false,
                homePageCount = preferences[Keys.HOME_PAGE_COUNT] ?: 2,
                healthInfo = HealthInfo(
                    fullName = preferences[Keys.HEALTH_NAME] ?: "",
                    age = preferences[Keys.HEALTH_AGE] ?: "",
                    bloodGroup = preferences[Keys.HEALTH_BLOOD_GROUP] ?: "",
                    allergies = preferences[Keys.HEALTH_ALLERGIES] ?: "",
                    medicalConditions = preferences[Keys.HEALTH_CONDITIONS] ?: "",
                    medicines = preferences[Keys.HEALTH_MEDICINES] ?: "",
                    doctorOrEmergencyContact = preferences[Keys.HEALTH_DOCTOR_CONTACT] ?: "",
                    notes = preferences[Keys.HEALTH_NOTES] ?: "",
                ),
            )
        }

    override suspend fun updateOnboardingComplete(complete: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.ONBOARDING_COMPLETE] = complete
        }
    }

    override suspend fun updateEmergencyPhoneNumber(phoneNumber: String) {
        dataStore.edit { preferences ->
            preferences[Keys.EMERGENCY_PHONE_NUMBER] = phoneNumber
        }
    }

    override suspend fun updateEmergencyNumbers(numbers: List<EmergencyNumber>) {
        dataStore.edit { preferences ->
            preferences[Keys.EMERGENCY_NUMBERS] = encodeEmergencyNumbers(numbers)
        }
    }

    override suspend fun updateSosNumbers(numbers: List<String>) {
        dataStore.edit { preferences ->
            preferences[Keys.SOS_NUMBERS] = encodeSosNumbers(numbers)
        }
    }

    override suspend fun updateClockPreference(use24HourClock: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.USE_24_HOUR_CLOCK] = use24HourClock
        }
    }

    override suspend fun updateCaregiverProtectionEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.CAREGIVER_PROTECTION_ENABLED] = enabled
        }
    }

    override suspend fun updateLayoutLocked(locked: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.LAYOUT_LOCKED] = locked
        }
    }

    override suspend fun updateEasyUiLockEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.EASYUI_LOCK_ENABLED] = enabled
        }
    }

    override suspend fun updateEasyUiLockTimeoutSeconds(seconds: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.EASYUI_LOCK_TIMEOUT_SECONDS] = seconds.coerceIn(15, 300)
        }
    }

    override suspend fun updateAppVisibilityPreset(presetName: String) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_VISIBILITY_PRESET] = presetName
        }
    }

    override suspend fun updateHomeReadabilityPreset(presetName: String) {
        dataStore.edit { preferences ->
            preferences[Keys.HOME_READABILITY_PRESET] = presetName
        }
    }

    override suspend fun updateVerySimpleModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.VERY_SIMPLE_MODE_ENABLED] = enabled
        }
    }

    override suspend fun updateShowBatteryInfo(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_BATTERY_INFO] = enabled
        }
    }

    override suspend fun updateHomePageCount(pageCount: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.HOME_PAGE_COUNT] = pageCount.coerceIn(1, 3)
        }
    }

    override suspend fun updateHealthInfo(healthInfo: HealthInfo) {
        dataStore.edit { preferences ->
            preferences[Keys.HEALTH_NAME] = healthInfo.fullName
            preferences[Keys.HEALTH_AGE] = healthInfo.age
            preferences[Keys.HEALTH_BLOOD_GROUP] = healthInfo.bloodGroup
            preferences[Keys.HEALTH_ALLERGIES] = healthInfo.allergies
            preferences[Keys.HEALTH_CONDITIONS] = healthInfo.medicalConditions
            preferences[Keys.HEALTH_MEDICINES] = healthInfo.medicines
            preferences[Keys.HEALTH_DOCTOR_CONTACT] = healthInfo.doctorOrEmergencyContact
            preferences[Keys.HEALTH_NOTES] = healthInfo.notes
        }
    }

    override suspend fun storePinCredential(credential: PinCredential) {
        dataStore.edit { preferences ->
            preferences[Keys.PIN_SALT_HEX] = credential.saltHex
            preferences[Keys.PIN_HASH_HEX] = credential.hashHex
        }
    }

    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val EMERGENCY_PHONE_NUMBER = stringPreferencesKey("emergency_phone_number")
        val EMERGENCY_NUMBERS = stringPreferencesKey("emergency_numbers")
        val SOS_NUMBERS = stringPreferencesKey("sos_numbers")
        val USE_24_HOUR_CLOCK = booleanPreferencesKey("use_24_hour_clock")
        val CAREGIVER_PROTECTION_ENABLED = booleanPreferencesKey("caregiver_protection_enabled")
        val LAYOUT_LOCKED = booleanPreferencesKey("layout_locked")
        val EASYUI_LOCK_ENABLED = booleanPreferencesKey("easyui_lock_enabled")
        val EASYUI_LOCK_TIMEOUT_SECONDS = intPreferencesKey("easyui_lock_timeout_seconds")
        val PIN_SALT_HEX = stringPreferencesKey("pin_salt_hex")
        val PIN_HASH_HEX = stringPreferencesKey("pin_hash_hex")
        val APP_VISIBILITY_PRESET = stringPreferencesKey("app_visibility_preset")
        val HOME_READABILITY_PRESET = stringPreferencesKey("home_readability_preset")
        val VERY_SIMPLE_MODE_ENABLED = booleanPreferencesKey("very_simple_mode_enabled")
        val SHOW_BATTERY_INFO = booleanPreferencesKey("show_battery_info")
        val HOME_PAGE_COUNT = intPreferencesKey("home_page_count")
        val HEALTH_NAME = stringPreferencesKey("health_name")
        val HEALTH_AGE = stringPreferencesKey("health_age")
        val HEALTH_BLOOD_GROUP = stringPreferencesKey("health_blood_group")
        val HEALTH_ALLERGIES = stringPreferencesKey("health_allergies")
        val HEALTH_CONDITIONS = stringPreferencesKey("health_conditions")
        val HEALTH_MEDICINES = stringPreferencesKey("health_medicines")
        val HEALTH_DOCTOR_CONTACT = stringPreferencesKey("health_doctor_contact")
        val HEALTH_NOTES = stringPreferencesKey("health_notes")
    }

    private fun encodeEmergencyNumbers(numbers: List<EmergencyNumber>): String {
        val array = JSONArray()
        numbers.take(6).forEach { entry ->
            val label = entry.label.trim()
            val number = entry.phoneNumber.trim()
            if (label.isNotBlank() && number.isNotBlank()) {
                array.put(
                    JSONObject().apply {
                        put("label", label)
                        put("number", number)
                    },
                )
            }
        }
        return array.toString()
    }

    private fun decodeEmergencyNumbers(raw: String?): List<EmergencyNumber> {
        if (raw.isNullOrBlank()) {
            return listOf(
                EmergencyNumber(label = "Ambulance", phoneNumber = "911"),
                EmergencyNumber(label = "Police", phoneNumber = "911"),
                EmergencyNumber(label = "Fire", phoneNumber = "911"),
            )
        }
        val parsed = runCatching { JSONArray(raw) }.getOrNull()?.let { json ->
            buildList {
                for (index in 0 until json.length()) {
                    val obj = json.optJSONObject(index) ?: continue
                    val label = obj.optString("label", "").trim()
                    val number = obj.optString("number", "").trim()
                    if (label.isNotBlank() && number.isNotBlank()) {
                        add(EmergencyNumber(label, number))
                    }
                }
            }
        } ?: emptyList()
        return if (parsed.isEmpty()) {
            listOf(
                EmergencyNumber(label = "Ambulance", phoneNumber = "911"),
                EmergencyNumber(label = "Police", phoneNumber = "911"),
                EmergencyNumber(label = "Fire", phoneNumber = "911"),
            )
        } else {
            parsed
        }
    }

    private fun encodeSosNumbers(numbers: List<String>): String {
        val array = JSONArray()
        numbers.take(3).map { it.trim() }.filter { it.isNotBlank() }.forEach(array::put)
        return array.toString()
    }

    private fun decodeSosNumbers(raw: String?): List<String> =
        runCatching { JSONArray(raw ?: "[]") }.getOrNull()?.let { json ->
            buildList {
                for (index in 0 until json.length()) {
                    val value = json.optString(index, "").trim()
                    if (value.isNotBlank()) add(value)
                }
            }.take(3)
        }
            ?: emptyList()
}
