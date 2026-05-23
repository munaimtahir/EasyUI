package com.easyui.launcher.app

import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.TileDisplayModel

data class HomeUiState(
    val timeText: String = "",
    val dateText: String = "",
    val tiles: List<TileDisplayModel> = emptyList(),
    val pages: List<List<TileDisplayModel?>> = emptyList(),
    val skinConfig: SkinConfig = SkinConfig(),
    val pageCount: Int = 1,
    val layoutLocked: Boolean = false,
    val emergencyPhoneNumber: String = "",
    val batteryPercentage: Int? = null,
    val isCharging: Boolean = false,
    val isBatteryLow: Boolean = false,
    val showBatteryInfo: Boolean = false,
    val allAppsVisible: Boolean = true,
    val installedApps: List<com.easyui.core.domain.model.InstalledApp> = emptyList(),
    val healthState: com.easyui.core.domain.model.PhoneHealthState = com.easyui.core.domain.model.PhoneHealthState(
        checks = emptyList(),
        overallStatus = com.easyui.core.domain.model.GuardianCheckStatus.OK,
        primaryMessage = "Phone is ready"
    )
)
