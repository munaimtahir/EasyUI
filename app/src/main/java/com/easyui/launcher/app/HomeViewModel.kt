package com.easyui.launcher.app

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.BatteryStatus
import com.easyui.core.domain.model.DeviceStatus
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.TileDisplayKind
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.domain.rules.ContactTileRules
import com.easyui.core.domain.rules.HomeLayoutRules
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

private const val SosTapWindowMs = 2_400L
private const val SosCooldownMs = 8_000L
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
        val batteryStatus: BatteryStatus,
        val deviceStatus: DeviceStatus,
        val settings: LauncherSettings,
        val now: LocalDateTime,
    )

    val state: StateFlow<HomeUiState> =
        combine(
            container.homeLayoutRepository.observeTiles(),
            container.batteryStatusRepository.observeBatteryStatus(),
            container.deviceStatusRepository.observeDeviceStatus(),
            settingsState,
            timeFlow(),
        ) { tiles, batteryStatus, deviceStatus, settings, now ->
            BaseHomeState(
                tiles = tiles,
                batteryStatus = batteryStatus,
                deviceStatus = deviceStatus,
                settings = settings,
                now = now,
            )
        }.combine(localState) { base, local ->
            HomeUiState(
                timeText = base.now.format(
                    if (base.settings.use24HourClock) {
                        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                    } else {
                        DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
                    },
                ),
                batteryPercent = base.batteryStatus.percentage?.let { "$it%" } ?: "--%",
                chargingLabel = if (base.batteryStatus.isCharging) "Charging" else "Not charging",
                signalLabel = base.deviceStatus.signalLabel,
                simLabel = base.deviceStatus.simLabel,
                wifiLabel = base.deviceStatus.wifiLabel,
                tiles = primaryTiles(base.tiles),
                sosTriggerProgress = local.sosTapProgress,
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
        onOpenHealthInfo: () -> Unit,
    ) {
        val tile = state.value.tiles.firstOrNull { it.id == tileId } ?: return
        viewModelScope.launch {
            when (tile.kind) {
                TileDisplayKind.PHONE_CONTACTS, TileDisplayKind.DIALER -> onOpenPhoneContacts()
                TileDisplayKind.FLASHLIGHT -> {
                    val result = container.flashlightController.performToggle()
                    result.fallbackMessage?.let { messages.emit(it) }
                }
                TileDisplayKind.CAMERA -> {
                    if (!container.cameraActionHandler.launchCamera()) {
                        messages.emit("Camera is not available on this device.")
                    }
                }
                TileDisplayKind.EMERGENCY -> onOpenEmergency()
                TileDisplayKind.HEALTH_INFO -> onOpenHealthInfo()
                TileDisplayKind.SOS -> registerSosTap()
                TileDisplayKind.APPS_LIST -> messages.emit("All Apps is hidden in this senior-focused layout.")
                TileDisplayKind.APP,
                TileDisplayKind.CONTACT,
                -> Unit
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

    private suspend fun registerSosTap() {
        val now = SystemClock.elapsedRealtime()
        val current = localState.value
        if (now < current.sosCooldownUntilMs) {
            messages.emit("SOS is cooling down for a moment.")
            return
        }
        val nextCount = if (now - current.lastSosTapAtMs <= SosTapWindowMs) current.sosTapCount + 1 else 1
        localState.update {
            it.copy(
                sosTapCount = nextCount,
                sosTapProgress = nextCount.coerceAtMost(3),
                lastSosTapAtMs = now,
            )
        }
        if (nextCount < 3) return

        localState.update {
            it.copy(
                sosTapCount = 0,
                sosTapProgress = 0,
                sosCooldownUntilMs = now + SosCooldownMs,
            )
        }
        runSosFlow()
    }

    private suspend fun runSosFlow() {
        val numbers = settingsState.value.sosNumbers.take(3)
        if (numbers.isEmpty()) {
            messages.emit("Set at least one SOS number in caregiver settings first.")
            return
        }

        val sentCount = numbers.count { number ->
            container.emergencyActionHandler.sendSms(
                phoneNumber = number,
                message = "EasyUI SOS alert: please call back immediately.",
            )
        }
        val callPlaced = container.emergencyActionHandler.callPhone(numbers.first())
        val callLabel = if (callPlaced) "calling primary caregiver." else "unable to auto-call primary caregiver."
        messages.emit("SOS activated: sent $sentCount/${numbers.size} messages, $callLabel")
    }

    private fun primaryTiles(tiles: List<HomeTile>): List<TileDisplayModel> {
        val fixed = HomeLayoutRules.ensureRequiredActions(tiles).filter { it.position in 0..5 }.sortedBy { it.position }
        return fixed.map { tile ->
            when (tile.action) {
                HomeTileAction.OPEN_DIALER -> TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = "Open caregiver contacts",
                    enabled = true,
                    kind = TileDisplayKind.PHONE_CONTACTS,
                )
                HomeTileAction.FLASHLIGHT -> TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = "Toggle light",
                    enabled = true,
                    kind = TileDisplayKind.FLASHLIGHT,
                )
                HomeTileAction.OPEN_CAMERA -> TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = "Open camera now",
                    enabled = true,
                    kind = TileDisplayKind.CAMERA,
                )
                HomeTileAction.EMERGENCY -> TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = "Emergency call options",
                    enabled = true,
                    kind = TileDisplayKind.EMERGENCY,
                )
                HomeTileAction.OPEN_HEALTH_INFO -> TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = "View medical card",
                    enabled = true,
                    kind = TileDisplayKind.HEALTH_INFO,
                )
                HomeTileAction.SOS -> TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = "Tap 3x quickly",
                    enabled = true,
                    kind = TileDisplayKind.SOS,
                )
                HomeTileAction.OPEN_APP_LIST, null -> TileDisplayModel(
                    id = tile.id,
                    title = tile.title,
                    subtitle = "Unavailable in this layout",
                    enabled = false,
                    kind = TileDisplayKind.APPS_LIST,
                    avatarFallback = ContactTileRules.photoFallback(null, tile.title),
                )
            }
        }
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
        val sosTapCount: Int = 0,
        val sosTapProgress: Int = 0,
        val lastSosTapAtMs: Long = 0L,
        val sosCooldownUntilMs: Long = 0L,
    )
}
