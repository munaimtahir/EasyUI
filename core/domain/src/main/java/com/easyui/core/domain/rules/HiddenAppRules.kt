package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.InstalledApp

object HiddenAppRules {
    fun visibleApps(apps: List<InstalledApp>, hiddenPackages: Set<String>): List<InstalledApp> =
        apps.filterNot { it.packageName in hiddenPackages }

    fun visibleHomeTiles(tiles: List<HomeTile>, hiddenPackages: Set<String>): List<HomeTile> =
        tiles.filterNot { tile -> tile.packageName != null && tile.packageName in hiddenPackages }
}
