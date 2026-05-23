package com.easyui.core.domain.repository

import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.SkinConfig
import kotlinx.coroutines.flow.Flow

interface LauncherSettingsRepository {
    val settings: Flow<LauncherSettings>
    suspend fun updateOnboardingComplete(complete: Boolean)
    suspend fun updateEmergencyPhoneNumber(phoneNumber: String)
    suspend fun updateEmergencyNumbers(numbers: List<EmergencyNumber>)
    suspend fun updateSosNumbers(numbers: List<String>)
    suspend fun updateClockPreference(use24HourClock: Boolean)
    suspend fun updateCaregiverProtectionEnabled(enabled: Boolean)
    suspend fun updateLayoutLocked(locked: Boolean)
    suspend fun updateEasyUiLockEnabled(enabled: Boolean)
    suspend fun updateEasyUiLockTimeoutSeconds(seconds: Int)
    suspend fun updateAppVisibilityPreset(presetName: String)
    suspend fun updateHomeReadabilityPreset(presetName: String)
    suspend fun updateVerySimpleModeEnabled(enabled: Boolean)
    suspend fun updateShowBatteryInfo(enabled: Boolean)
    suspend fun updateHomePageCount(pageCount: Int)
    suspend fun updateHealthInfo(healthInfo: HealthInfo)
    suspend fun setSkinConfig(config: SkinConfig)
    suspend fun getSkinConfig(): SkinConfig
    suspend fun storePinCredential(credential: com.easyui.core.domain.model.PinCredential)
    suspend fun updateSetupProtectionLevel(levelName: String)
    suspend fun updateSetupOptionalPermissions(permissionNames: Set<String>)
    suspend fun updateGuidedSetupStep(step: Int)
    suspend fun updateGuidedSetupCompleted(completed: Boolean)
    suspend fun updateEmergencyMode(mode: String)
    suspend fun updateAllAppsVisible(visible: Boolean)
    suspend fun updateBatteryLowCheckEnabled(enabled: Boolean)
    suspend fun updateBatteryLowThreshold(threshold: Int)
    suspend fun updateBatteryCriticalThreshold(threshold: Int)
    suspend fun updateInternetCheckEnabled(enabled: Boolean)
    suspend fun updateNoInternetDelayMinutes(minutes: Int)
    suspend fun updateDefaultLauncherCheckEnabled(enabled: Boolean)
    suspend fun updateEmergencyContactCheckEnabled(enabled: Boolean)
    suspend fun updateLayoutLockCheckEnabled(enabled: Boolean)
    suspend fun updatePermissionCheckEnabled(enabled: Boolean)
}
