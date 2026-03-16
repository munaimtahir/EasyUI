package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.InstalledApp

object LauncherResetRules {
    fun resetLayout(installedApps: List<InstalledApp>): List<HomeTile> =
        HomeLayoutRules.starterLayout(installedApps)

    fun resetHiddenPackages(): Set<String> = emptySet()
}
