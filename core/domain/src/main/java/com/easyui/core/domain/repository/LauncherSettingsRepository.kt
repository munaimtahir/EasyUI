package com.easyui.core.domain.repository

import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.HealthInfo
import kotlinx.coroutines.flow.Flow

interface LauncherSettingsRepository {
    val settings: Flow<LauncherSettings>
    suspend fun updateOnboardingComplete(complete: Boolean)
    suspend fun updateEmergencyPhoneNumber(phoneNumber: String)
    suspend fun updateClockPreference(use24HourClock: Boolean)
    suspend fun updateCaregiverProtectionEnabled(enabled: Boolean)
    suspend fun updateLayoutLocked(locked: Boolean)
    suspend fun updateAppVisibilityPreset(presetName: String)
    suspend fun updateHomeReadabilityPreset(presetName: String)
    suspend fun updateVerySimpleModeEnabled(enabled: Boolean)
    suspend fun updateShowBatteryInfo(enabled: Boolean)
    suspend fun updateHomePageCount(pageCount: Int)
    suspend fun updateHealthInfo(healthInfo: HealthInfo)
    suspend fun storePinCredential(credential: com.easyui.core.domain.model.PinCredential)
}
