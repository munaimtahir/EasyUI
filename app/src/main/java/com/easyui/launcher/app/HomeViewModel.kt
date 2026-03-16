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
import com.easyui.core.domain.rules.ContactTileRules
import com.easyui.core.domain.rules.FallbackStateRules
import com.easyui.core.domain.rules.HiddenAppRules
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
            container.hiddenAppRepository.observeHiddenPackages(),
            settingsState,
            timeFlow(),
        ) { tiles, apps, hiddenPackages, settings, now ->
            val readabilityPreset = settings.homeReadabilityPreset.asReadabilityPreset()
            val visibleTiles = VerySimpleModeRules.simplify(
                tiles = HiddenAppRules.visibleHomeTiles(tiles, hiddenPackages),
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
                tiles = toDisplayModels(
                    tiles = visibleTiles,
                    apps = HiddenAppRules.visibleApps(apps, hiddenPackages),
                    settings = settings,
                ),
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

    fun onTileClick(tileId: String, onOpenApps: () -> Unit) {
        val tile = state.value.tiles.firstOrNull { it.id == tileId } ?: return
        viewModelScope.launch {
            when (tile.kind) {
                TileDisplayKind.APPS_LIST -> onOpenApps()
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
                TileDisplayKind.APP -> {
                    val app = state.value.tiles.firstOrNull { it.id == tileId }
                    val domainTile = container.homeLayoutRepository.getTiles().firstOrNull { it.id == tileId }
                    if (app == null || domainTile?.packageName == null) {
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

    private suspend fun toDisplayModels(
        tiles: List<HomeTile>,
        apps: List<InstalledApp>,
        settings: LauncherSettings,
    ): List<TileDisplayModel> =
        tiles.map { tile ->
            when (tile.action) {
                HomeTileAction.OPEN_APP_LIST -> TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = "Browse every app",
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
                        subtitle = if (state.enabled) "Open the dialer" else state.fallbackMessage.orEmpty(),
                        enabled = state.enabled,
                        kind = TileDisplayKind.EMERGENCY,
                    )
                }
                null -> {
                    if (tile.type == com.easyui.core.domain.model.HomeTileType.CONTACT) {
                        val state = container.emergencyActionHandler.currentState(tile.phoneNumber.orEmpty())
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
        }

    private fun String.asReadabilityPreset(): HomeReadabilityPreset =
        runCatching { HomeReadabilityPreset.valueOf(this) }.getOrDefault(HomeReadabilityPreset.STANDARD)
}
