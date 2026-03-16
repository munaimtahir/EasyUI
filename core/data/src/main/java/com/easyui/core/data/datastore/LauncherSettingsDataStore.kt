package com.easyui.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.repository.LauncherSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
                use24HourClock = preferences[Keys.USE_24_HOUR_CLOCK] ?: false,
                caregiverProtectionEnabled = preferences[Keys.CAREGIVER_PROTECTION_ENABLED] ?: false,
                layoutLocked = preferences[Keys.LAYOUT_LOCKED] ?: false,
                pinSaltHex = preferences[Keys.PIN_SALT_HEX],
                pinHashHex = preferences[Keys.PIN_HASH_HEX],
                appVisibilityPreset = preferences[Keys.APP_VISIBILITY_PRESET] ?: "CUSTOM",
                homeReadabilityPreset = preferences[Keys.HOME_READABILITY_PRESET] ?: "STANDARD",
                verySimpleModeEnabled = preferences[Keys.VERY_SIMPLE_MODE_ENABLED] ?: false,
                showBatteryInfo = preferences[Keys.SHOW_BATTERY_INFO] ?: false,
                homePageCount = preferences[Keys.HOME_PAGE_COUNT] ?: 1,
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

    override suspend fun storePinCredential(credential: PinCredential) {
        dataStore.edit { preferences ->
            preferences[Keys.PIN_SALT_HEX] = credential.saltHex
            preferences[Keys.PIN_HASH_HEX] = credential.hashHex
        }
    }

    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val EMERGENCY_PHONE_NUMBER = stringPreferencesKey("emergency_phone_number")
        val USE_24_HOUR_CLOCK = booleanPreferencesKey("use_24_hour_clock")
        val CAREGIVER_PROTECTION_ENABLED = booleanPreferencesKey("caregiver_protection_enabled")
        val LAYOUT_LOCKED = booleanPreferencesKey("layout_locked")
        val PIN_SALT_HEX = stringPreferencesKey("pin_salt_hex")
        val PIN_HASH_HEX = stringPreferencesKey("pin_hash_hex")
        val APP_VISIBILITY_PRESET = stringPreferencesKey("app_visibility_preset")
        val HOME_READABILITY_PRESET = stringPreferencesKey("home_readability_preset")
        val VERY_SIMPLE_MODE_ENABLED = booleanPreferencesKey("very_simple_mode_enabled")
        val SHOW_BATTERY_INFO = booleanPreferencesKey("show_battery_info")
        val HOME_PAGE_COUNT = androidx.datastore.preferences.core.intPreferencesKey("home_page_count")
    }
}
