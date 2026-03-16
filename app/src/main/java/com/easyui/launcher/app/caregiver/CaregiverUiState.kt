package com.easyui.launcher.app.caregiver

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.ProtectedAction

data class CaregiverUiState(
    val settings: LauncherSettings = LauncherSettings(),
    val hiddenPackages: Set<String> = emptySet(),
    val installedApps: List<InstalledApp> = emptyList(),
    val layoutTiles: List<HomeTile> = emptyList(),
    val caregiverSessionActive: Boolean = false,
    val pinInput: String = "",
    val confirmPinInput: String = "",
    val pinError: String? = null,
    val pendingAction: ProtectedAction? = null,
)
