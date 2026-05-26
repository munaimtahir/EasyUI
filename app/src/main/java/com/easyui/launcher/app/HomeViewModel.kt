package com.easyui.launcher.app

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.TileDisplayKind
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.domain.rules.PrimaryHomeAppKind
import com.easyui.core.domain.rules.PrimaryHomeAppRules
import com.easyui.launcher.di.AppContainer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val ClockTapWindowMs = 3_000L
private const val ClockTapTriggerCount = 5
private const val CaregiverAccessDebounceMs = 1_500L

class HomeViewModel(
    private val container: AppContainer,
) : ViewModel() {
    val messages = MutableSharedFlow<String>()
    private val localState = MutableStateFlow(LocalHomeState())

    private val settingsState = container.launcherSettingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LauncherSettings(),
    )

    private data class BaseHomeState(
        val tiles: List<HomeTile>,
        val installedApps: List<InstalledApp>,
        val settings: LauncherSettings,
        val now: LocalDateTime,
        val battery: com.easyui.core.domain.model.BatteryStatus,
        val deviceStatus: com.easyui.core.domain.model.DeviceStatus,
        val isDefaultLauncher: Boolean,
    )

    val state: StateFlow<HomeUiState> =
        combine(
            container.homeLayoutRepository.observeTiles(),
            container.appCatalogRepository.observeInstalledApps(),
            settingsState,
            timeFlow(),
            container.batteryStatusRepository.observeBatteryStatus(),
            container.deviceStatusRepository.observeDeviceStatus(),
        ) { args: Array<Any?> ->
            BaseHomeState(
                tiles = args[0] as List<HomeTile>,
                installedApps = args[1] as List<InstalledApp>,
                settings = args[2] as LauncherSettings,
                now = args[3] as LocalDateTime,
                battery = args[4] as com.easyui.core.domain.model.BatteryStatus,
                deviceStatus = args[5] as com.easyui.core.domain.model.DeviceStatus,
                isDefaultLauncher = container.defaultLauncherManager.isDefaultLauncher()
            )
        }.combine(localState) { base, _ ->
            val setupCompleteness = com.easyui.core.domain.rules.GuardianRules.calculateSetupCompleteness(
                settings = base.settings,
                isDefaultLauncher = base.isDefaultLauncher,
                hasRequiredPermissions = true, // Simplified for now, can be improved
                favoriteContactCount = base.tiles.count { it.type == HomeTileType.CONTACT },
                allowedAppCount = base.tiles.count { it.type == HomeTileType.APP }
            )
            
            val healthState = com.easyui.core.domain.rules.GuardianRules.calculatePhoneHealthState(
                settings = base.settings,
                batteryPercentage = base.battery.percentage,
                isCharging = base.battery.isCharging,
                isInternetAvailable = base.deviceStatus.isInternetAvailable,
                isDefaultLauncher = base.isDefaultLauncher,
                isBatteryOptimized = base.deviceStatus.isBatteryOptimized,
                hasRequiredPermissions = true,
                setupCompleteness = setupCompleteness
            )

            val pages = renderPages(base.tiles, base.installedApps, base.settings)
            val allTiles = pages.flatten().filterNotNull()
            val effectivePageCount = HomeLayoutRules.effectivePageCount(
                configuredPageCount = base.settings.homePageCount,
                tiles = base.tiles,
            )

            HomeUiState(
                timeText = base.now.format(
                    if (base.settings.use24HourClock) {
                        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                    } else {
                        DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
                    },
                ),
                dateText = base.now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())),
                tiles = allTiles,
                pages = pages,
                skinConfig = base.settings.skinConfig,
                pageCount = effectivePageCount,
                layoutLocked = base.settings.layoutLocked,
                emergencyPhoneNumber = base.settings.emergencyPhoneNumber,
                batteryPercentage = base.battery.percentage,
                isCharging = base.battery.isCharging,
                isBatteryLow = base.battery.isLow || (base.battery.percentage ?: 100) <= 20,
                showBatteryInfo = base.settings.showBatteryInfo,
                allAppsVisible = base.settings.allAppsVisible,
                installedApps = base.installedApps,
                healthState = healthState
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    fun onTileClick(
        tileId: String,
        onOpenPhoneContacts: () -> Unit,
        onOpenEmergency: () -> Unit,
        onOpenMessages: () -> Unit,
        onOpenPhotos: () -> Unit,
        onOpenCamera: () -> Unit,
    ) {
        val tile = state.value.tiles.firstOrNull { it.id == tileId } ?: return
        viewModelScope.launch {
            when (tile.kind) {
                TileDisplayKind.PHONE -> onOpenPhoneContacts()
                TileDisplayKind.CONTACTS -> onOpenPhoneContacts()
                TileDisplayKind.MESSAGES -> onOpenMessages()
                TileDisplayKind.PHOTOS -> onOpenPhotos()
                TileDisplayKind.APP -> launchResolvedApp(tile)
                TileDisplayKind.FAVORITE_CONTACT -> {
                    val number = tile.phoneNumber
                    if (number.isNullOrBlank() || !container.emergencyActionHandler.callPhone(number)) {
                        messages.emit("This contact is not available right now.")
                    }
                }
                TileDisplayKind.CAMERA -> onOpenCamera()
                TileDisplayKind.EMERGENCY -> {
                    if (tile.id == "emergency-sos") {
                        val number = settingsState.value.emergencyPhoneNumber
                        triggerDirectEmergencyCall(number)
                    } else {
                        onOpenEmergency()
                    }
                }
            }
        }
    }

    fun onTopBarLongPressCaregiverAccess(onAccessRequested: () -> Unit) {
        if (!canTriggerCaregiverAccess()) return
        onAccessRequested()
    }

    fun onClockTappedCaregiverAccess(onAccessRequested: () -> Unit) {
        val now = SystemClock.elapsedRealtime()
        val current = localState.value
        val nextCount =
            if (now - current.lastClockTapAtMs <= ClockTapWindowMs) {
                current.clockTapCount + 1
            } else {
                1
            }
        localState.update {
            it.copy(
                clockTapCount = nextCount,
                lastClockTapAtMs = now,
            )
        }
        if (nextCount < ClockTapTriggerCount) return
        localState.update { it.copy(clockTapCount = 0, lastClockTapAtMs = 0L) }
        if (!canTriggerCaregiverAccess()) return
        onAccessRequested()
    }

    suspend fun triggerDirectEmergencyCall(number: String): Boolean =
        container.emergencyActionHandler.callPhone(number)

    private fun canTriggerCaregiverAccess(): Boolean {
        val now = SystemClock.elapsedRealtime()
        val allowed = now - localState.value.lastCaregiverAccessAtMs >= CaregiverAccessDebounceMs
        if (allowed) {
            localState.update { it.copy(lastCaregiverAccessAtMs = now, clockTapCount = 0, lastClockTapAtMs = 0L) }
        }
        return allowed
    }

    private suspend fun launchDialer() {
        if (!container.emergencyActionHandler.launchDialer(phoneNumber = null)) {
            messages.emit("Phone is not available on this device.")
        }
    }

    private suspend fun launchResolvedApp(tile: TileDisplayModel) {
        val packageName = tile.packageName
        val activityName = tile.activityName
        val launched = packageName != null &&
            activityName != null &&
            container.appLauncher.launch(packageName, activityName)
        if (!launched) {
            messages.emit("${tile.title} is not available on this device.")
        }
    }

    private fun actionTile(
        tile: HomeTile,
        kind: TileDisplayKind,
        app: InstalledApp?,
    ): TileDisplayModel =
        TileDisplayModel(
            id = tile.id,
            title = tile.title,
            subtitle = tile.title,
            enabled = true,
            kind = kind,
            packageName = app?.packageName,
            activityName = app?.activityName,
        )

    private fun renderPages(
        tiles: List<HomeTile>,
        installedApps: List<InstalledApp>,
        settings: LauncherSettings,
    ): List<List<TileDisplayModel?>> {
        val pageCount = HomeLayoutRules.effectivePageCount(
            configuredPageCount = settings.homePageCount,
            tiles = tiles,
        )
        val appLookup = installedApps.associateBy { it.packageName }
        return HomeLayoutRules.pages(tiles, pageCount).mapIndexed { pageIndex, pageTiles ->
            pageTiles.mapIndexed { slotIndex, tile ->
                when {
                    tile == null -> null
                    pageIndex == 0 && slotIndex <= 5 -> firstPageTile(tile, installedApps, settings)
                    tile.type == HomeTileType.APP -> {
                        val app = tile.packageName?.let(appLookup::get)
                        TileDisplayModel(
                            id = tile.id,
                            title = tile.title,
                            subtitle = tile.title,
                            enabled = true,
                            kind = TileDisplayKind.APP,
                            packageName = app?.packageName ?: tile.packageName,
                            activityName = app?.activityName,
                        )
                    }
                    tile.type == HomeTileType.CONTACT -> TileDisplayModel(
                        id = tile.id,
                        title = tile.title,
                        subtitle = "Call",
                        enabled = true,
                        kind = TileDisplayKind.FAVORITE_CONTACT,
                        phoneNumber = tile.phoneNumber,
                        avatarImageUri = tile.photoUri,
                        avatarFallback = tile.title.take(2).uppercase(Locale.getDefault()),
                    )
                    else -> null
                }
            }
        }
    }

    private fun firstPageTile(
        tile: HomeTile,
        installedApps: List<InstalledApp>,
        settings: LauncherSettings,
    ): TileDisplayModel? =
        when (tile.action) {
            HomeTileAction.OPEN_DIALER -> TileDisplayModel(
                id = tile.id,
                title = tile.title,
                subtitle = "Phone",
                enabled = true,
                kind = TileDisplayKind.PHONE,
            )
            HomeTileAction.OPEN_MESSAGES -> actionTile(
                tile = tile,
                kind = TileDisplayKind.MESSAGES,
                app = PrimaryHomeAppRules.resolve(PrimaryHomeAppKind.MESSAGES, installedApps),
            )
            HomeTileAction.OPEN_CONTACTS -> TileDisplayModel(
                id = tile.id,
                title = tile.title,
                subtitle = "Contacts",
                enabled = true,
                kind = TileDisplayKind.CONTACTS,
            )
            HomeTileAction.OPEN_PHOTOS -> actionTile(
                tile = tile,
                kind = TileDisplayKind.PHOTOS,
                app = PrimaryHomeAppRules.resolve(PrimaryHomeAppKind.PHOTOS, installedApps),
            )
            HomeTileAction.OPEN_CAMERA -> TileDisplayModel(
                id = tile.id,
                title = tile.title,
                subtitle = "Camera",
                enabled = true,
                kind = TileDisplayKind.CAMERA,
            )
            HomeTileAction.EMERGENCY,
            HomeTileAction.SOS,
            -> TileDisplayModel(
                id = if (tile.action == HomeTileAction.SOS) "emergency-sos" else tile.id,
                title = tile.title,
                subtitle = if (tile.action == HomeTileAction.SOS) "SOS" else "Emergency",
                enabled = true,
                kind = TileDisplayKind.EMERGENCY,
                phoneNumber = if (tile.action == HomeTileAction.SOS) settings.emergencyPhoneNumber else null
            )
            HomeTileAction.OPEN_APP_LIST,
            HomeTileAction.FLASHLIGHT,
            HomeTileAction.OPEN_HEALTH_INFO,
            null,
            -> null
        }

    private fun timeFlow() = kotlinx.coroutines.flow.flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(30_000)
        }
    }

    private data class LocalHomeState(
        val clockTapCount: Int = 0,
        val lastClockTapAtMs: Long = 0L,
        val lastCaregiverAccessAtMs: Long = 0L,
    )
}
