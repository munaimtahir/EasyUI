package com.easyui.core.data.repository

import com.easyui.core.data.backup.BackupSerializer
import com.easyui.core.domain.model.BackupData
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.ValidationResult
import com.easyui.core.domain.repository.BackupRepository
import com.easyui.core.domain.repository.HiddenAppRepository
import com.easyui.core.domain.repository.HomeLayoutRepository
import com.easyui.core.domain.repository.LauncherSettingsRepository
import java.time.Instant
import kotlinx.coroutines.flow.first

class LocalBackupRepository(
    private val homeLayoutRepository: HomeLayoutRepository,
    private val launcherSettingsRepository: LauncherSettingsRepository,
    private val hiddenAppRepository: HiddenAppRepository,
) : BackupRepository {

    override suspend fun exportJson(): String {
        val settings: LauncherSettings = launcherSettingsRepository.settings.first()
        val tiles = homeLayoutRepository.getTiles()
        val hiddenPackages = hiddenAppRepository.getHiddenPackages()
        return BackupSerializer.serialize(
            settings = settings,
            tiles = tiles,
            hiddenPackages = hiddenPackages,
            exportedAt = Instant.now().toString(),
        )
    }

    override fun validate(json: String): ValidationResult = BackupSerializer.validate(json)

    override suspend fun applyBackup(data: BackupData) {
        homeLayoutRepository.replaceTiles(data.tiles)
        hiddenAppRepository.replaceHiddenPackages(data.hiddenPackages)
        with(launcherSettingsRepository) {
            updateEmergencyPhoneNumber(data.settings.emergencyPhoneNumber)
            updateClockPreference(data.settings.use24HourClock)
            updateCaregiverProtectionEnabled(data.settings.caregiverProtectionEnabled)
            updateLayoutLocked(data.settings.layoutLocked)
            updateAppVisibilityPreset(data.settings.appVisibilityPreset)
            updateHomeReadabilityPreset(data.settings.homeReadabilityPreset)
            updateVerySimpleModeEnabled(data.settings.verySimpleModeEnabled)
            updateShowBatteryInfo(data.settings.showBatteryInfo)
            updateHomePageCount(data.settings.homePageCount)
            updateHealthInfo(data.settings.healthInfo)
            updateOnboardingComplete(true)
        }
    }
}
