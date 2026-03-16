package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp

object HomeLayoutRules {
    private val preferredPackages = listOf(
        "com.google.android.dialer",
        "com.samsung.android.dialer",
        "com.android.dialer",
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",
        "com.google.android.apps.photos",
        "com.sec.android.gallery3d",
        "com.google.android.GoogleCamera",
        "com.sec.android.app.camera",
        "com.android.camera",
        "com.android.settings",
    )

    fun starterLayout(installedApps: List<InstalledApp>): List<HomeTile> {
        val sortedApps = AppCatalogRules.sortAlphabetically(installedApps)
        val preferredApps = preferredPackages.mapNotNull { packageName ->
            sortedApps.firstOrNull { it.packageName == packageName }
        }
        val selectedApps = (preferredApps + sortedApps).distinctBy { it.packageName }.take(3)

        val starterTiles = buildList {
            add(
                HomeTile(
                    id = "apps-list",
                    position = size,
                    title = "All Apps",
                    type = HomeTileType.ACTION,
                    action = HomeTileAction.OPEN_APP_LIST,
                ),
            )
            selectedApps.forEach { app ->
                add(
                    HomeTile(
                        id = "app-${app.packageName}",
                        position = size,
                        title = app.label,
                        type = HomeTileType.APP,
                        packageName = app.packageName,
                    ),
                )
            }
            add(
                HomeTile(
                    id = "flashlight",
                    position = size,
                    title = "Flashlight",
                    type = HomeTileType.ACTION,
                    action = HomeTileAction.FLASHLIGHT,
                ),
            )
            add(
                HomeTile(
                    id = "emergency",
                    position = size,
                    title = "Emergency Call",
                    type = HomeTileType.ACTION,
                    action = HomeTileAction.EMERGENCY,
                ),
            )
        }
        return normalize(starterTiles)
    }

    fun contactTiles(tiles: List<HomeTile>): List<HomeTile> =
        normalize(tiles).filter { it.type == HomeTileType.CONTACT }

    fun upsertContactTile(tiles: List<HomeTile>, tile: HomeTile): List<HomeTile> {
        val normalized = normalize(tiles)
        val existingIndex = normalized.indexOfFirst { it.id == tile.id }
        val updated = normalized.toMutableList()
        if (existingIndex >= 0) {
            updated[existingIndex] = tile.copy(position = updated[existingIndex].position)
        } else {
            updated += tile.copy(position = updated.size)
        }
        return normalize(updated)
    }

    fun normalize(tiles: List<HomeTile>): List<HomeTile> =
        tiles
            .distinctBy { it.id }
            .sortedBy { it.position }
            .mapIndexed { index, tile -> tile.copy(position = index) }

    fun isValid(tiles: List<HomeTile>): Boolean =
        normalize(tiles).size == tiles.size &&
            tiles.map { it.id }.distinct().size == tiles.size &&
            tiles.mapIndexed { index, tile -> tile.position == index }.all { it }
}
