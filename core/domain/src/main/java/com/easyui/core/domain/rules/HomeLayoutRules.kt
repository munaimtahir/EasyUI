package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import kotlin.math.max

object HomeLayoutRules {
    const val SLOTS_PER_PAGE = 6
    const val MAX_PAGE_COUNT = 3

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

    private val requiredActionTiles = listOf(
        HomeTile(
            id = "phone",
            position = 0,
            title = "Phone",
            type = HomeTileType.ACTION,
            action = HomeTileAction.OPEN_DIALER,
        ),
        HomeTile(
            id = "messages",
            position = 1,
            title = "Messages",
            type = HomeTileType.ACTION,
            action = HomeTileAction.OPEN_MESSAGES,
        ),
        HomeTile(
            id = "contacts",
            position = 2,
            title = "Contacts",
            type = HomeTileType.ACTION,
            action = HomeTileAction.OPEN_CONTACTS,
        ),
        HomeTile(
            id = "photos",
            position = 3,
            title = "Photos",
            type = HomeTileType.ACTION,
            action = HomeTileAction.OPEN_PHOTOS,
        ),
        HomeTile(
            id = "camera",
            position = 4,
            title = "Camera",
            type = HomeTileType.ACTION,
            action = HomeTileAction.OPEN_CAMERA,
        ),
        HomeTile(
            id = "emergency",
            position = 5,
            title = "Emergency",
            type = HomeTileType.ACTION,
            action = HomeTileAction.EMERGENCY,
        ),
    )

    private val requiredActionIds = requiredActionTiles.map { it.id }.toSet()
    private val legacyPrimaryActionIds = setOf("flashlight", "health-info", "sos")
    private val legacyPrimaryActions = setOf(
        HomeTileAction.FLASHLIGHT,
        HomeTileAction.OPEN_HEALTH_INFO,
        HomeTileAction.SOS,
    )
    private val reservedPositions = requiredActionTiles.map { it.position }.toSet()

    fun clampPageCount(pageCount: Int): Int = pageCount.coerceIn(1, MAX_PAGE_COUNT)

    fun totalSlots(pageCount: Int): Int = clampPageCount(pageCount) * SLOTS_PER_PAGE

    fun effectivePageCount(configuredPageCount: Int, tiles: List<HomeTile>): Int {
        val normalized = ensureRequiredActions(tiles)
        val highestPosition = normalized.maxOfOrNull { it.position } ?: 0
        val requiredPages = (highestPosition / SLOTS_PER_PAGE) + 1
        return clampPageCount(max(clampPageCount(configuredPageCount), requiredPages))
    }

    fun starterLayout(installedApps: List<InstalledApp>): List<HomeTile> {
        val sortedApps = AppCatalogRules.sortAlphabetically(installedApps)
        val preferredApps = preferredPackages.mapNotNull { packageName ->
            sortedApps.firstOrNull { it.packageName == packageName }
        }
        val selectedHomeApp = (preferredApps + sortedApps).distinctBy { it.packageName }.firstOrNull()
        val userTiles = selectedHomeApp?.let { app ->
            listOf(
                HomeTile(
                    id = "app-${app.packageName}",
                    position = 6,
                    title = app.label,
                    type = HomeTileType.APP,
                    packageName = app.packageName,
                ),
            )
        } ?: emptyList()
        return ensureRequiredActions(userTiles)
    }

    fun ensureRequiredActions(
        tiles: List<HomeTile>,
        pageCount: Int = MAX_PAGE_COUNT,
    ): List<HomeTile> {
        val maxPosition = totalSlots(pageCount) - 1
        val normalizedUsers = normalize(
            tiles.filterNot(::isPrimarySurfaceTile),
            pageCount = pageCount,
        )
        val adjustedUsers = mutableListOf<HomeTile>()
        val occupiedUserPositions = mutableSetOf<Int>()
        normalizedUsers.forEach { tile ->
            val desired = tile.position.coerceIn(0, maxPosition)
            val assigned = nextAvailableUserPosition(desired, occupiedUserPositions, maxPosition)
                ?: nextAvailableUserPosition(0, occupiedUserPositions, maxPosition)
                ?: return@forEach
            occupiedUserPositions += assigned
            adjustedUsers += tile.copy(position = assigned)
        }
        return (adjustedUsers + requiredActionTiles).sortedBy { it.position }
    }

    fun normalize(
        tiles: List<HomeTile>,
        pageCount: Int = MAX_PAGE_COUNT,
    ): List<HomeTile> {
        val maxPosition = totalSlots(pageCount) - 1
        val occupied = mutableSetOf<Int>()
        return tiles
            .distinctBy { it.id }
            .sortedWith(compareBy<HomeTile> { it.position }.thenBy { it.id })
            .mapNotNull { tile ->
                val desired = tile.position.coerceIn(0, maxPosition)
                val assigned = nextAvailablePosition(desired, occupied, maxPosition)
                    ?: nextAvailablePosition(0, occupied, maxPosition)
                    ?: return@mapNotNull null
                occupied += assigned
                tile.copy(position = assigned)
            }
            .sortedBy { it.position }
    }

    fun contactTiles(tiles: List<HomeTile>): List<HomeTile> =
        ensureRequiredActions(tiles).filter { it.type == HomeTileType.CONTACT }

    fun appTiles(tiles: List<HomeTile>): List<HomeTile> =
        ensureRequiredActions(tiles).filter { it.type == HomeTileType.APP }

    fun pages(
        tiles: List<HomeTile>,
        pageCount: Int,
    ): List<List<HomeTile?>> {
        val effectivePageCount = effectivePageCount(pageCount, tiles)
        val sortedTiles = ensureRequiredActions(tiles, effectivePageCount)
        val slotLookup = sortedTiles.associateBy { it.position }
        return List(effectivePageCount) { pageIndex ->
            List(SLOTS_PER_PAGE) { slotIndex ->
                slotLookup[(pageIndex * SLOTS_PER_PAGE) + slotIndex]
            }
        }
    }

    fun firstAvailableUserPosition(
        tiles: List<HomeTile>,
        pageCount: Int,
    ): Int? {
        val effectivePageCount = effectivePageCount(pageCount, tiles)
        val occupied = ensureRequiredActions(tiles, effectivePageCount).map { it.position }.toSet()
        val maxPosition = totalSlots(effectivePageCount) - 1
        return nextAvailableUserPosition(0, occupied, maxPosition)
    }

    fun upsertContactTile(
        tiles: List<HomeTile>,
        tile: HomeTile,
        pageCount: Int,
    ): List<HomeTile>? {
        val effectivePageCount = effectivePageCount(pageCount, tiles)
        val base = ensureRequiredActions(tiles, effectivePageCount)
        val existing = base.firstOrNull { it.id == tile.id }
        val targetPosition = existing?.position
            ?: firstAvailableUserPosition(base, effectivePageCount)
            ?: return null
        val updated = base.filterNot { it.id == tile.id } + tile.copy(position = targetPosition)
        return ensureRequiredActions(updated, effectivePageCount)
    }

    fun assignAppToPosition(
        tiles: List<HomeTile>,
        app: InstalledApp,
        position: Int,
        pageCount: Int,
    ): List<HomeTile>? {
        val effectivePageCount = effectivePageCount(pageCount, tiles)
        val maxPosition = totalSlots(effectivePageCount) - 1
        if (position !in 0..maxPosition || isReservedPosition(position)) return null

        val base = ensureRequiredActions(tiles, effectivePageCount)
        val occupant = base.firstOrNull { it.position == position }
        if (occupant != null && occupant.type != HomeTileType.APP) return null

        val updated = base.filterNot { existing ->
            existing.packageName == app.packageName || (existing.position == position && existing.type == HomeTileType.APP)
        } + HomeTile(
            id = "app-${app.packageName}",
            position = position,
            title = app.label,
            type = HomeTileType.APP,
            packageName = app.packageName,
        )
        return ensureRequiredActions(updated, effectivePageCount)
    }

    fun removeAppAssignment(
        tiles: List<HomeTile>,
        packageName: String,
        pageCount: Int,
    ): List<HomeTile> =
        ensureRequiredActions(
            tiles.filterNot { it.type == HomeTileType.APP && it.packageName == packageName },
            pageCount,
        )

    fun updateEmergencyAction(
        tiles: List<HomeTile>,
        useSos: Boolean,
    ): List<HomeTile> {
        val action = if (useSos) HomeTileAction.SOS else HomeTileAction.EMERGENCY
        return tiles.map { tile ->
            if (tile.id == "emergency" && tile.position == 5) {
                tile.copy(action = action)
            } else {
                tile
            }
        }
    }

    fun moveTileEarlier(
        tiles: List<HomeTile>,
        tileId: String,
        pageCount: Int,
    ): List<HomeTile> =
        moveTile(tiles, tileId, pageCount, direction = -1)

    fun moveTileLater(
        tiles: List<HomeTile>,
        tileId: String,
        pageCount: Int,
    ): List<HomeTile> =
        moveTile(tiles, tileId, pageCount, direction = 1)

    fun canUsePageCount(
        tiles: List<HomeTile>,
        pageCount: Int,
    ): Boolean {
        val maxPosition = totalSlots(pageCount) - 1
        return ensureRequiredActions(tiles).none { it.position > maxPosition }
    }

    fun isReservedPosition(position: Int): Boolean = position in reservedPositions

    fun isValid(
        tiles: List<HomeTile>,
        pageCount: Int = MAX_PAGE_COUNT,
    ): Boolean {
        val maxPosition = totalSlots(pageCount) - 1
        val ids = mutableSetOf<String>()
        val positions = mutableSetOf<Int>()
        return tiles.all { tile ->
            tile.position in 0..maxPosition &&
                ids.add(tile.id) &&
                positions.add(tile.position)
        }
    }

    private fun moveTile(
        tiles: List<HomeTile>,
        tileId: String,
        pageCount: Int,
        direction: Int,
    ): List<HomeTile> {
        val effectivePageCount = effectivePageCount(pageCount, tiles)
        val base = ensureRequiredActions(tiles, effectivePageCount)
        val movable = base.filterNot(::isPrimarySurfaceTile).sortedBy { it.position }
        val index = movable.indexOfFirst { it.id == tileId }
        if (index == -1) return base
        val swapIndex = index + direction
        if (swapIndex !in movable.indices) return base
        val current = movable[index]
        val neighbor = movable[swapIndex]
        return ensureRequiredActions(
            base.map { tile ->
                when (tile.id) {
                    current.id -> tile.copy(position = neighbor.position)
                    neighbor.id -> tile.copy(position = current.position)
                    else -> tile
                }
            },
            effectivePageCount,
        )
    }

    private fun isPrimarySurfaceTile(tile: HomeTile): Boolean =
        tile.id in requiredActionIds ||
            tile.id in legacyPrimaryActionIds ||
            tile.action in legacyPrimaryActions ||
            tile.action in setOf(
            HomeTileAction.OPEN_DIALER,
            HomeTileAction.OPEN_MESSAGES,
            HomeTileAction.OPEN_CONTACTS,
            HomeTileAction.OPEN_PHOTOS,
            HomeTileAction.EMERGENCY,
            HomeTileAction.OPEN_CAMERA,
        )

    private fun nextAvailablePosition(
        start: Int,
        occupied: Set<Int>,
        maxPosition: Int,
    ): Int? =
        (start..maxPosition).firstOrNull { it !in occupied }

    private fun nextAvailableUserPosition(
        start: Int,
        occupied: Set<Int>,
        maxPosition: Int,
    ): Int? =
        (start..maxPosition).firstOrNull { it !in occupied && it !in reservedPositions }
}
