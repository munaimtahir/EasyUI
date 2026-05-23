package com.easyui.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.LayoutMode
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.OptionalPermission
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.model.SetupProtectionLevel
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.VisualTheme
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.repository.LauncherSettingsRepository
import kotlinx.coroutines.flow.first
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
                skinConfig = decodeSkinConfig(
                    layoutModeName = preferences[Keys.SKIN_LAYOUT_MODE],
                    visualThemeName = preferences[Keys.SKIN_VISUAL_THEME],
                    accessibilityModeName = preferences[Keys.SKIN_ACCESSIBILITY_MODE],
                    readabilityPresetName = preferences[Keys.HOME_READABILITY_PRESET],
                    verySimpleModeEnabled = preferences[Keys.VERY_SIMPLE_MODE_ENABLED] ?: false,
                ),
                setupProtectionLevel = preferences[Keys.SETUP_PROTECTION_LEVEL] ?: SetupProtectionLevel.RECOMMENDED.name,
                setupOptionalPermissions = preferences[Keys.SETUP_OPTIONAL_PERMISSIONS] ?: defaultOptionalPermissions(),
                guidedSetupStep = preferences[Keys.GUIDED_SETUP_STEP] ?: 0,
                guidedSetupCompleted = preferences[Keys.GUIDED_SETUP_COMPLETED] ?: false,
                emergencyMode = preferences[Keys.EMERGENCY_MODE] ?: "MENU",
                allAppsVisible = preferences[Keys.ALL_APPS_VISIBLE] ?: true,
                batteryLowCheckEnabled = preferences[Keys.BATTERY_LOW_CHECK_ENABLED] ?: true,
                batteryLowThreshold = preferences[Keys.BATTERY_LOW_THRESHOLD] ?: 20,
                batteryCriticalThreshold = preferences[Keys.BATTERY_CRITICAL_THRESHOLD] ?: 10,
                internetCheckEnabled = preferences[Keys.INTERNET_CHECK_ENABLED] ?: true,
                noInternetDelayMinutes = preferences[Keys.NO_INTERNET_DELAY_MINUTES] ?: 30,
                defaultLauncherCheckEnabled = preferences[Keys.DEFAULT_LAUNCHER_CHECK_ENABLED] ?: true,
                emergencyContactCheckEnabled = preferences[Keys.EMERGENCY_CONTACT_CHECK_ENABLED] ?: true,
                layoutLockCheckEnabled = preferences[Keys.LAYOUT_LOCK_CHECK_ENABLED] ?: true,
                permissionCheckEnabled = preferences[Keys.PERMISSION_CHECK_ENABLED] ?: true,
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

    override suspend fun setSkinConfig(config: SkinConfig) {
        dataStore.edit { preferences ->
            preferences[Keys.SKIN_LAYOUT_MODE] = config.layoutMode.name
            preferences[Keys.SKIN_VISUAL_THEME] = config.visualTheme.name
            preferences[Keys.SKIN_ACCESSIBILITY_MODE] = config.accessibilityMode.name
            preferences[Keys.HOME_READABILITY_PRESET] = config.readabilityPreset.name
        }
    }

    override suspend fun getSkinConfig(): SkinConfig {
        val preferences = dataStore.data.first()
        return decodeSkinConfig(
            layoutModeName = preferences[Keys.SKIN_LAYOUT_MODE],
            visualThemeName = preferences[Keys.SKIN_VISUAL_THEME],
            accessibilityModeName = preferences[Keys.SKIN_ACCESSIBILITY_MODE],
            readabilityPresetName = preferences[Keys.HOME_READABILITY_PRESET],
            verySimpleModeEnabled = preferences[Keys.VERY_SIMPLE_MODE_ENABLED] ?: false,
        )
    }

    override suspend fun storePinCredential(credential: PinCredential) {
        dataStore.edit { preferences ->
            preferences[Keys.PIN_SALT_HEX] = credential.saltHex
            preferences[Keys.PIN_HASH_HEX] = credential.hashHex
        }
    }

    override suspend fun updateSetupProtectionLevel(levelName: String) {
        dataStore.edit { preferences ->
            preferences[Keys.SETUP_PROTECTION_LEVEL] = levelName
        }
    }

    override suspend fun updateSetupOptionalPermissions(permissionNames: Set<String>) {
        dataStore.edit { preferences ->
            preferences[Keys.SETUP_OPTIONAL_PERMISSIONS] = permissionNames
        }
    }

    override suspend fun updateGuidedSetupStep(step: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.GUIDED_SETUP_STEP] = step
        }
    }

    override suspend fun updateGuidedSetupCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.GUIDED_SETUP_COMPLETED] = completed
        }
    }

    override suspend fun updateEmergencyMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[Keys.EMERGENCY_MODE] = mode
        }
    }

    override suspend fun updateAllAppsVisible(visible: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.ALL_APPS_VISIBLE] = visible
        }
    }

    override suspend fun updateBatteryLowCheckEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.BATTERY_LOW_CHECK_ENABLED] = enabled
        }
    }

    override suspend fun updateBatteryLowThreshold(threshold: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.BATTERY_LOW_THRESHOLD] = threshold.coerceIn(5, 50)
        }
    }

    override suspend fun updateBatteryCriticalThreshold(threshold: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.BATTERY_CRITICAL_THRESHOLD] = threshold.coerceIn(2, 20)
        }
    }

    override suspend fun updateInternetCheckEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.INTERNET_CHECK_ENABLED] = enabled
        }
    }

    override suspend fun updateNoInternetDelayMinutes(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.NO_INTERNET_DELAY_MINUTES] = minutes.coerceIn(0, 1440)
        }
    }

    override suspend fun updateDefaultLauncherCheckEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DEFAULT_LAUNCHER_CHECK_ENABLED] = enabled
        }
    }

    override suspend fun updateEmergencyContactCheckEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.EMERGENCY_CONTACT_CHECK_ENABLED] = enabled
        }
    }

    override suspend fun updateLayoutLockCheckEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.LAYOUT_LOCK_CHECK_ENABLED] = enabled
        }
    }

    override suspend fun updatePermissionCheckEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.PERMISSION_CHECK_ENABLED] = enabled
        }
    }

    private fun defaultOptionalPermissions(): Set<String> = setOf(
        OptionalPermission.PHONE_DIALER.name,
        OptionalPermission.CONTACTS.name,
        OptionalPermission.CAMERA.name,
        OptionalPermission.PHOTOS_MEDIA.name,
        OptionalPermission.BACKUP_RESTORE_FILES.name,
        OptionalPermission.NOTIFICATIONS.name,
    )

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
        val SKIN_LAYOUT_MODE = stringPreferencesKey("skin_layout_mode")
        val SKIN_VISUAL_THEME = stringPreferencesKey("skin_visual_theme")
        val SKIN_ACCESSIBILITY_MODE = stringPreferencesKey("skin_accessibility_mode")
        val SETUP_PROTECTION_LEVEL = stringPreferencesKey("setup_protection_level")
        val SETUP_OPTIONAL_PERMISSIONS = stringSetPreferencesKey("setup_optional_permissions")
        val GUIDED_SETUP_STEP = intPreferencesKey("guided_setup_step")
        val GUIDED_SETUP_COMPLETED = booleanPreferencesKey("guided_setup_completed")
        val EMERGENCY_MODE = stringPreferencesKey("emergency_mode")
        val ALL_APPS_VISIBLE = booleanPreferencesKey("all_apps_visible")
        val BATTERY_LOW_CHECK_ENABLED = booleanPreferencesKey("battery_low_check_enabled")
        val BATTERY_LOW_THRESHOLD = intPreferencesKey("battery_low_threshold")
        val BATTERY_CRITICAL_THRESHOLD = intPreferencesKey("battery_critical_threshold")
        val INTERNET_CHECK_ENABLED = booleanPreferencesKey("internet_check_enabled")
        val NO_INTERNET_DELAY_MINUTES = intPreferencesKey("no_internet_delay_minutes")
        val DEFAULT_LAUNCHER_CHECK_ENABLED = booleanPreferencesKey("default_launcher_check_enabled")
        val EMERGENCY_CONTACT_CHECK_ENABLED = booleanPreferencesKey("emergency_contact_check_enabled")
        val LAYOUT_LOCK_CHECK_ENABLED = booleanPreferencesKey("layout_lock_check_enabled")
        val PERMISSION_CHECK_ENABLED = booleanPreferencesKey("permission_check_enabled")
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

    private fun decodeSkinConfig(
        layoutModeName: String?,
        visualThemeName: String?,
        accessibilityModeName: String?,
        readabilityPresetName: String?,
        verySimpleModeEnabled: Boolean,
    ): SkinConfig {
        val layoutMode = runCatching { LayoutMode.valueOf(layoutModeName.orEmpty()) }
            .getOrElse { if (verySimpleModeEnabled) LayoutMode.VERY_SIMPLE else LayoutMode.SIMPLE_CLASSIC }
        val visualTheme = runCatching { VisualTheme.valueOf(visualThemeName.orEmpty()) }
            .getOrDefault(VisualTheme.DARK_COMFORT)
        val accessibilityMode = runCatching { AccessibilityMode.valueOf(accessibilityModeName.orEmpty()) }
            .getOrDefault(AccessibilityMode.NONE)
        val readabilityPreset = runCatching { HomeReadabilityPreset.valueOf(readabilityPresetName.orEmpty()) }
            .getOrDefault(HomeReadabilityPreset.STANDARD)
        return SkinConfig(
            layoutMode = layoutMode,
            visualTheme = visualTheme,
            accessibilityMode = accessibilityMode,
            readabilityPreset = readabilityPreset,
        )
    }
}
