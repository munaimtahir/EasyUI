package com.easyui.launcher.app

import com.easyui.core.domain.model.LauncherSettings

data class AppState(
    val settings: LauncherSettings = LauncherSettings(),
    val starterLayoutReady: Boolean = false,
)
