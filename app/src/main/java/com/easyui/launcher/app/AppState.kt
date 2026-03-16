package com.easyui.launcher.app

import com.easyui.core.domain.model.LauncherSettings

data class AppState(
    val settings: LauncherSettings = LauncherSettings(),
    val settingsLoaded: Boolean = false,
    val starterLayoutReady: Boolean = false,
)
