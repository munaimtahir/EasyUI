package com.easyui.launcher.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.TileDisplayKind
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.domain.rules.BatteryDisplayRules
import com.easyui.core.domain.rules.ContactTileRules
import com.easyui.core.domain.rules.FallbackStateRules
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.domain.rules.VerySimpleModeRules
import com.easyui.launcher.di.AppContainer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val container: AppContainer,
) : ViewModel() {
    val messages = MutableSharedFlow<String>()
    private val settingsState = container.launcherSettingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LauncherSettings(),
    )

    val state: StateFlow<HomeUiState> =
        combine(
            container.homeLayoutRepository.observeTiles(),
            container.appCatalogRepository.observeInstalledApps(),
            settingsState,
            container.batteryStatusRepository.observeBatteryStatus(),
            timeFlow(),
        ) { tiles, apps, settings, batteryStatus, now ->
            val readabilityPreset = settings.homeReadabilityPreset.asReadabilityPreset()
            val effectivePageCount = HomeLayoutRules.effectivePageCount(settings.homePageCount, tiles)
            val visibleTiles = VerySimpleModeRules.simplify(
                tiles = HomeLayoutRules.ensureRequiredActions(tiles, effectivePageCount),
                enabled = settings.verySimpleModeEnabled,
            )
            val fallback = FallbackStateRules.home(
                tileCount = visibleTiles.size,
                verySimpleModeEnabled = settings.verySimpleModeEnabled,
            )
            HomeUiState(
                timeText = now.format(
                    if (settings.use24HourClock) {
                        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                    } else {
                        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                    },
                ),
                dateText = now.format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())),
                batterySummary = if (settings.showBatteryInfo) BatteryDisplayRules.summary(batteryStatus) else null,
                pages = HomeLayoutRules.pages(visibleTiles, effectivePageCount).map { pageTiles ->
                    pageTiles.map { tile ->
                        tile?.let {
                            toDisplayModel(
                                tile = it,
                                apps = apps,
                                settings = settings,
                            )
                        }
                    }
                },
                readabilityPreset = readabilityPreset,
                verySimpleModeEnabled = settings.verySimpleModeEnabled,
                fallbackTitle = fallback?.title,
                fallbackBody = fallback?.body,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    fun onTileClick(
        tileId: String,
        onOpenApps: () -> Unit,
        onOpenHealthInfo: () -> Unit,
    ) {
        val tile = state.value.pages.flatten().filterNotNull().firstOrNull { it.id == tileId } ?: return
        viewModelScope.launch {
            when (tile.kind) {
                TileDisplayKind.APPS_LIST -> onOpenApps()
                TileDisplayKind.DIALER -> {
                    if (!container.emergencyActionHandler.launchDialer(null)) {
                        messages.emit("The dialer is not available on this device.")
                    }
                }
                TileDisplayKind.FLASHLIGHT -> {
                    val result = container.flashlightController.performToggle()
                    result.fallbackMessage?.let { messages.emit(it) }
                }
                TileDisplayKind.EMERGENCY -> {
                    val phoneNumber = settingsState.value.emergencyPhoneNumber
                    val launched = container.emergencyActionHandler.launchDialer(phoneNumber)
                    if (!launched) {
                        messages.emit("Emergency calling is not available on this device.")
                    }
                }
                TileDisplayKind.CAMERA -> {
                    if (!container.cameraActionHandler.launchCamera()) {
                        messages.emit("Camera is not available on this device.")
                    }
                }
                TileDisplayKind.HEALTH_INFO -> onOpenHealthInfo()
                TileDisplayKind.APP -> {
                    val domainTile = container.homeLayoutRepository.getTiles().firstOrNull { it.id == tileId }
                    if (domainTile?.packageName == null) {
                        messages.emit("That app is no longer available.")
                    } else {
                        val installedApp = container.appCatalogRepository.getInstalledApps()
                            .firstOrNull { it.packageName == domainTile.packageName }
                        if (installedApp == null || !container.appLauncher.launch(installedApp.packageName, installedApp.activityName)) {
                            messages.emit("That app is no longer available.")
                        }
                    }
                }
                TileDisplayKind.CONTACT -> {
                    val domainTile = container.homeLayoutRepository.getTiles().firstOrNull { it.id == tileId }
                    val phoneNumber = domainTile?.phoneNumber
                    if (phoneNumber.isNullOrBlank()) {
                        messages.emit("This contact does not have a phone number yet.")
                    } else if (!container.emergencyActionHandler.launchDialer(phoneNumber)) {
                        messages.emit("The dialer is not available on this device.")
                    }
                }
            }
        }
    }

    private fun timeFlow() = kotlinx.coroutines.flow.flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(60_000)
        }
    }

    private suspend fun toDisplayModel(
        tile: HomeTile,
        apps: List<InstalledApp>,
        settings: LauncherSettings,
    ): TileDisplayModel =
        when (tile.action) {
            HomeTileAction.OPEN_DIALER -> {
                val state = container.emergencyActionHandler.currentState(null)
                TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = if (state.enabled) "Open the dialer" else state.fallbackMessage.orEmpty(),
                    enabled = state.enabled,
                    kind = TileDisplayKind.DIALER,
                )
            }
            HomeTileAction.OPEN_APP_LIST -> TileDisplayModel(
                id = tile.id,
                title = tile.title,
                subtitle = "Open full app list",
                enabled = true,
                kind = TileDisplayKind.APPS_LIST,
            )
            HomeTileAction.FLASHLIGHT -> {
                val state = container.flashlightController.currentState()
                TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = if (state.enabled) "Turn light on or off" else state.fallbackMessage.orEmpty(),
                    enabled = state.enabled,
                    kind = TileDisplayKind.FLASHLIGHT,
                )
            }
            HomeTileAction.EMERGENCY -> {
                val state = container.emergencyActionHandler.currentState(settings.emergencyPhoneNumber)
                TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = if (state.enabled) "Open dialer with saved emergency number" else state.fallbackMessage.orEmpty(),
                    enabled = state.enabled,
                    kind = TileDisplayKind.EMERGENCY,
                )
            }
            HomeTileAction.OPEN_CAMERA -> {
                val state = container.cameraActionHandler.currentState()
                TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = if (state.enabled) "Open camera" else state.fallbackMessage.orEmpty(),
                    enabled = state.enabled,
                    kind = TileDisplayKind.CAMERA,
                )
            }
            HomeTileAction.OPEN_HEALTH_INFO -> TileDisplayModel(
                id = tile.id,
                title = tile.title,
                subtitle = if (settings.healthInfo.hasAnyValue()) {
                    "View saved health details"
                } else {
                    "No health details saved yet"
                },
                enabled = true,
                kind = TileDisplayKind.HEALTH_INFO,
            )
            null -> {
                if (tile.type == com.easyui.core.domain.model.HomeTileType.CONTACT) {
                    val state = container.emergencyActionHandler.currentState(tile.phoneNumber)
                    TileDisplayModel(
                        id = tile.id,
                        title = tile.title,
                        subtitle = if (state.enabled) {
                            tile.phoneNumber ?: "Open the dialer"
                        } else {
                            state.fallbackMessage.orEmpty()
                        },
                        enabled = state.enabled,
                        kind = TileDisplayKind.CONTACT,
                        avatarImageUri = tile.photoUri,
                        avatarFallback = ContactTileRules.photoFallback(tile.photoUri, tile.title),
                    )
                } else {
                    val installedApp = apps.firstOrNull { it.packageName == tile.packageName }
                    TileDisplayModel(
                        id = tile.id,
                        title = tile.title,
                        subtitle = installedApp?.let { "Open ${it.label}" } ?: "App not installed",
                        enabled = installedApp != null,
                        kind = TileDisplayKind.APP,
                    )
                }
            }
        }

    private fun String.asReadabilityPreset(): HomeReadabilityPreset =
        runCatching { HomeReadabilityPreset.valueOf(this) }.getOrDefault(HomeReadabilityPreset.STANDARD)
}
