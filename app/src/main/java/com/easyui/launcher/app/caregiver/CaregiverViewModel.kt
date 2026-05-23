package com.easyui.launcher.app.caregiver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.AppVisibilityPreset
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.model.LayoutMode
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.model.ProtectedAction
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.VisualTheme
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.rules.AppCatalogRules
import com.easyui.core.domain.rules.CaregiverProtectionRules
import com.easyui.core.domain.rules.ContactTileRules
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.domain.rules.LauncherResetRules
import com.easyui.core.domain.security.PinHasher
import com.easyui.launcher.di.AppContainer
import com.easyui.launcher.navigation.Routes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.os.SystemClock

private const val CAREGIVER_SESSION_TIMEOUT_MS = 15 * 60 * 1000L  // 15 minutes
private const val SESSION_TIMEOUT_WARNING_MS = 13 * 60 * 1000L    // 13 minutes (warn 2 min before)

class CaregiverViewModel(
    private val container: AppContainer,
) : ViewModel() {
    val messages = MutableSharedFlow<String>()
    private val localState = MutableStateFlow(CaregiverUiState())

    val state: StateFlow<CaregiverUiState> =
        combine(
            container.launcherSettingsRepository.settings,
            container.hiddenAppRepository.observeHiddenPackages(),
            container.appCatalogRepository.observeInstalledApps(),
            container.homeLayoutRepository.observeTiles(),
            localState,
        ) { settings, hiddenPackages, apps, layoutTiles, local ->
            val setupCompleteness = com.easyui.core.domain.rules.GuardianRules.calculateSetupCompleteness(
                settings = settings,
                isDefaultLauncher = container.defaultLauncherManager.isDefaultLauncher(),
                hasRequiredPermissions = true, // Simplified
                favoriteContactCount = layoutTiles.count { it.type == HomeTileType.CONTACT },
                allowedAppCount = layoutTiles.count { it.type == HomeTileType.APP }
            )
            local.copy(
                settings = settings,
                hiddenPackages = hiddenPackages,
                installedApps = apps,
                layoutTiles = layoutTiles,
                allAppsVisible = settings.allAppsVisible,
                setupCompleteness = setupCompleteness
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CaregiverUiState(),
        )

    fun requestCaregiverAccess(): String = requestProtectedRoute(ProtectedAction.OPEN_CAREGIVER_SETTINGS)

    fun beginProtectedAction(action: ProtectedAction): String = requestProtectedRoute(action)

    fun updatePinInput(value: String) {
        localState.update { it.copy(pinInput = value, pinError = null) }
    }

    fun updateConfirmPinInput(value: String) {
        localState.update { it.copy(confirmPinInput = value, pinError = null) }
    }

    fun submitPinSetup(): Boolean {
        val hadExistingPinConfigured =
            !state.value.settings.pinHashHex.isNullOrBlank() && !state.value.settings.pinSaltHex.isNullOrBlank()
        val validation = CaregiverProtectionRules.validatePin(
            pin = state.value.pinInput,
            confirmation = state.value.confirmPinInput,
        )
        if (!validation.valid) {
            localState.update { it.copy(pinError = validation.message) }
            return false
        }
        viewModelScope.launch {
            val credential = PinHasher.create(state.value.pinInput)
            val pinSaveBehavior = CaregiverProtectionRules.pinSaveBehavior(
                hadExistingPinConfigured = hadExistingPinConfigured,
                protectionEnabled = state.value.settings.caregiverProtectionEnabled,
                layoutLocked = state.value.settings.layoutLocked,
            )
            container.launcherSettingsRepository.storePinCredential(credential)
            container.launcherSettingsRepository.updateCaregiverProtectionEnabled(pinSaveBehavior.protectionEnabled)
            container.launcherSettingsRepository.updateLayoutLocked(pinSaveBehavior.layoutLocked)
            localState.update {
                it.copy(
                    pinInput = "",
                    confirmPinInput = "",
                    pinError = null,
                    pendingAction = null,
                )
            }
            messages.emit(
                if (hadExistingPinConfigured) {
                    "Caregiver PIN was updated."
                } else {
                    "Caregiver PIN is now active."
                },
            )
        }
        return true
    }

    fun completePinVerification(): String? {
        val pendingAction = state.value.pendingAction ?: return null
        val credential = state.value.settings.toPinCredential() ?: run {
            localState.update { it.copy(pinError = "No caregiver PIN is configured yet.") }
            return null
        }
        val valid = PinHasher.verify(state.value.pinInput, credential)
        if (!valid) {
            localState.update { it.copy(pinError = "PIN did not match. Try again.") }
            return null
        }
        localState.update {
            it.copy(
                pinInput = "",
                pinError = null,
                pendingAction = null,
                caregiverSessionActive = true,
                sessionLastActivityTimeMs = SystemClock.uptimeMillis(),
                sessionTimeoutWarningShown = false,
            )
        }
        return destinationFor(pendingAction)
    }

    fun endCaregiverSession() {
        localState.update {
            it.copy(
                caregiverSessionActive = false,
                pinInput = "",
                confirmPinInput = "",
                pinError = null,
                pendingAction = null,
            )
        }
    }

    fun clearPendingAction() {
        localState.update { it.copy(pendingAction = null, pinInput = "", pinError = null) }
    }
    
    fun updateSessionActivity() {
        if (state.value.caregiverSessionActive) {
            localState.update {
                it.copy(
                    sessionLastActivityTimeMs = SystemClock.uptimeMillis(),
                    sessionTimeoutWarningShown = false,
                )
            }
        }
    }
    
    fun checkSessionTimeout(): SessionTimeoutState {
        if (!state.value.caregiverSessionActive) return SessionTimeoutState.Active
        
        val timeSinceLastActivityMs = SystemClock.uptimeMillis() - state.value.sessionLastActivityTimeMs
        
        return when {
            timeSinceLastActivityMs >= CAREGIVER_SESSION_TIMEOUT_MS -> {
                endCaregiverSession()
                SessionTimeoutState.TimedOut
            }
            timeSinceLastActivityMs >= SESSION_TIMEOUT_WARNING_MS && !state.value.sessionTimeoutWarningShown -> {
                localState.update { it.copy(sessionTimeoutWarningShown = true) }
                SessionTimeoutState.WarningActive
            }
            else -> SessionTimeoutState.Active
        }
    }
    
    enum class SessionTimeoutState {
        Active,
        WarningActive,
        TimedOut,
    }

    fun toggleProtectionEnabled() {
        val enabled = !state.value.settings.caregiverProtectionEnabled
        viewModelScope.launch {
            container.launcherSettingsRepository.updateCaregiverProtectionEnabled(enabled)
            messages.emit(if (enabled) "Caregiver PIN checks are now on." else "Caregiver PIN checks are now off.")
        }
    }

    fun toggleLayoutLock() {
        val locked = !state.value.settings.layoutLocked
        viewModelScope.launch {
            container.launcherSettingsRepository.updateLayoutLocked(locked)
            messages.emit(if (locked) "Home layout is locked." else "Home layout can now be adjusted.")
        }
    }

    fun setBatteryInfoVisible(visible: Boolean) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateShowBatteryInfo(visible)
            messages.emit(if (visible) "Battery details now show on home." else "Battery details are hidden.")
        }
    }

    fun setAllAppsVisible(visible: Boolean) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateAllAppsVisible(visible)
            messages.emit(if (visible) "'All Apps' button is now visible." else "'All Apps' button is hidden.")
        }
    }

    fun updateHomePageCount(pageCount: Int) {
        val clamped = HomeLayoutRules.clampPageCount(pageCount)
        updateLayout { tiles -> HomeLayoutRules.forcePageCount(tiles, clamped) }
        viewModelScope.launch {
            container.launcherSettingsRepository.updateHomePageCount(clamped)
            messages.emit("EasyUI home now uses $clamped page${if (clamped == 1) "" else "s"}.")
        }

    }

    fun moveTileUp(tileId: String) {
        updateLayout { tiles ->
            HomeLayoutRules.moveTileEarlier(tiles, tileId, effectivePageCount())
        }
    }

    fun moveTileDown(tileId: String) {
        updateLayout { tiles ->
            HomeLayoutRules.moveTileLater(tiles, tileId, effectivePageCount())
        }
    }

    fun removeTile(tileId: String) {
        updateLayout { tiles -> tiles.filterNot { it.id == tileId } }
    }

    fun saveContactTile(
        tileId: String?,
        displayName: String,
        phoneNumber: String,
        photoUri: String?,
    ): String? {
        val error = ContactTileRules.validate(displayName, phoneNumber)
        if (error != null) {
            return error
        }
        var saved = true
        updateLayout { tiles ->
            HomeLayoutRules.upsertContactTile(
                tiles = tiles,
                tile = HomeTile(
                    id = tileId ?: "contact-${System.currentTimeMillis()}",
                    position = 0,
                    title = displayName.trim(),
                    type = HomeTileType.CONTACT,
                    phoneNumber = phoneNumber.trim(),
                    photoUri = photoUri,
                ),
                pageCount = effectivePageCount(),
            ) ?: run {
                saved = false
                tiles
            }
        }
        return if (saved) null else "Add another home page or remove a home tile first."
    }

    fun assignAllowedApp(packageName: String, position: Int) {
        val app = state.value.installedApps.firstOrNull { it.packageName == packageName } ?: return
        updateLayout { tiles ->
            HomeLayoutRules.assignAppToPosition(
                tiles = tiles,
                app = app,
                position = position,
                pageCount = effectivePageCount(),
            ) ?: run {
                viewModelScope.launch {
                    messages.emit("Choose an open app slot for this app.")
                }
                tiles
            }
        }
    }

    fun removeAllowedApp(packageName: String) {
        updateLayout { tiles ->
            HomeLayoutRules.removeAppAssignment(
                tiles = tiles,
                packageName = packageName,
                pageCount = effectivePageCount(),
            )
        }
    }

    fun updateHomeReadabilityPreset(preset: HomeReadabilityPreset) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateHomeReadabilityPreset(preset.name)
            val mappedLayoutMode = when (preset) {
                HomeReadabilityPreset.STANDARD -> LayoutMode.SIMPLE_CLASSIC
                HomeReadabilityPreset.LARGER_TEXT,
                HomeReadabilityPreset.LARGER_TILES,
                HomeReadabilityPreset.EXTRA_SIMPLE_SPACING,
                -> LayoutMode.VERY_SIMPLE
            }
            container.launcherSettingsRepository.setSkinConfig(
                state.value.settings.skinConfig.copy(
                    layoutMode = mappedLayoutMode,
                    readabilityPreset = preset
                ),
            )
            messages.emit(
                when (preset) {
                    HomeReadabilityPreset.STANDARD -> "Home display is set to Standard."
                    HomeReadabilityPreset.LARGER_TEXT -> "Home display now uses larger text."
                    HomeReadabilityPreset.LARGER_TILES -> "Home display now uses larger tiles."
                    HomeReadabilityPreset.EXTRA_SIMPLE_SPACING -> "Home display now uses extra simple spacing."
                },
            )
        }
    }

    fun setVerySimpleModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateVerySimpleModeEnabled(enabled)
            container.launcherSettingsRepository.setSkinConfig(
                state.value.settings.skinConfig.copy(
                    layoutMode = if (enabled) LayoutMode.VERY_SIMPLE else LayoutMode.SIMPLE_CLASSIC,
                ),
            )
            messages.emit(if (enabled) "Very simple home mode is now on." else "Very simple home mode is now off.")
        }
    }

    fun updateSkinLayoutMode(mode: LayoutMode) {
        viewModelScope.launch {
            val current = state.value.settings.skinConfig
            container.launcherSettingsRepository.setSkinConfig(current.copy(layoutMode = mode))
            val verySimple = mode == LayoutMode.VERY_SIMPLE
            container.launcherSettingsRepository.updateVerySimpleModeEnabled(verySimple)
            messages.emit("Layout mode set to ${displayName(mode.name)}.")
        }
    }

    fun updateSkinVisualTheme(theme: VisualTheme) {
        viewModelScope.launch {
            val current = state.value.settings.skinConfig
            container.launcherSettingsRepository.setSkinConfig(current.copy(visualTheme = theme))
            messages.emit("Theme set to ${displayName(theme.name)}.")
        }
    }

    fun updateSkinAccessibilityMode(mode: AccessibilityMode) {
        viewModelScope.launch {
            val current = state.value.settings.skinConfig
            container.launcherSettingsRepository.setSkinConfig(current.copy(accessibilityMode = mode))
            messages.emit("Accessibility mode set to ${displayName(mode.name)}.")
        }
    }

    fun resetLauncher() {
        viewModelScope.launch(container.ioDispatcher) {
            val starterLayout = LauncherResetRules.resetLayout(container.appCatalogRepository.getInstalledApps())
            container.homeLayoutRepository.replaceTiles(starterLayout)
            container.hiddenAppRepository.clearHiddenPackages()
            container.launcherSettingsRepository.updateAppVisibilityPreset(AppVisibilityPreset.CUSTOM.name)
            container.launcherSettingsRepository.updateVerySimpleModeEnabled(false)
            container.launcherSettingsRepository.updateHomeReadabilityPreset(HomeReadabilityPreset.STANDARD.name)
            container.launcherSettingsRepository.updateShowBatteryInfo(false)
            container.launcherSettingsRepository.updateHomePageCount(2)
            container.launcherSettingsRepository.updateSosNumbers(emptyList())
            container.launcherSettingsRepository.updateEmergencyNumbers(
                listOf(
                    EmergencyNumber(label = "Ambulance", phoneNumber = "911"),
                    EmergencyNumber(label = "Police", phoneNumber = "911"),
                    EmergencyNumber(label = "Fire", phoneNumber = "911"),
                ),
            )
            container.launcherSettingsRepository.updateEasyUiLockEnabled(false)
            container.launcherSettingsRepository.updateEasyUiLockTimeoutSeconds(60)
            container.launcherSettingsRepository.setSkinConfig(SkinConfig())
            messages.emit("EasyUI is back to its safe default layout.")
        }
    }

    fun restoreDefaultHomeLayout() {
        viewModelScope.launch(container.ioDispatcher) {
            val starterLayout = LauncherResetRules.resetLayout(container.appCatalogRepository.getInstalledApps())
            container.homeLayoutRepository.replaceTiles(starterLayout)
            container.launcherSettingsRepository.updateHomePageCount(2)
            messages.emit("Home layout was restored to EasyUI defaults.")
        }
    }

    fun updateEmergencyNumber(number: String) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateEmergencyPhoneNumber(number)
            messages.emit("Emergency number saved.")
        }
    }

    fun updateEmergencyNumbers(numbers: List<EmergencyNumber>) {
        viewModelScope.launch {
            val cleaned = numbers
                .map { EmergencyNumber(it.label.trim(), it.phoneNumber.trim()) }
                .filter { it.label.isNotBlank() && it.phoneNumber.isNotBlank() }
            container.launcherSettingsRepository.updateEmergencyNumbers(cleaned)
            messages.emit("Emergency numbers saved.")
        }
    }

    fun updateSosNumbers(numbers: List<String>) {
        viewModelScope.launch {
            val cleaned = numbers.map { it.trim() }.filter { it.isNotBlank() }.take(3)
            container.launcherSettingsRepository.updateSosNumbers(cleaned)
            messages.emit("SOS numbers saved.")
        }
    }

    fun setEasyUiLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateEasyUiLockEnabled(enabled)
            messages.emit(if (enabled) "EasyUI lock overlay is on." else "EasyUI lock overlay is off.")
        }
    }

    fun updateEasyUiLockTimeoutSeconds(seconds: Int) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateEasyUiLockTimeoutSeconds(seconds)
            messages.emit("Lock timeout saved.")
        }
    }

    fun toggleAppHidden(packageName: String) {
        viewModelScope.launch {
            val currentlyHidden = state.value.hiddenPackages
            if (packageName in currentlyHidden) {
                container.hiddenAppRepository.setHidden(packageName, false)
                messages.emit("App will now show in EasyUI app surfaces.")
            } else {
                container.hiddenAppRepository.setHidden(packageName, true)
                messages.emit("App is now hidden from EasyUI app surfaces.")
            }
        }
    }

    fun updateHealthInfo(healthInfo: HealthInfo) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateHealthInfo(healthInfo)
            messages.emit("Health information saved.")
        }
    }

    fun installedAppsForAllowedApps(): List<InstalledApp> =
        AppCatalogRules.sortAlphabetically(state.value.installedApps)

    fun homePages(): List<List<HomeTile?>> =
        HomeLayoutRules.pages(state.value.layoutTiles, effectivePageCount())

    fun assignedAppPackages(): Set<String> =
        HomeLayoutRules.appTiles(state.value.layoutTiles).mapNotNull { it.packageName }.toSet()

    fun effectivePageCount(): Int =
        HomeLayoutRules.effectivePageCount(
            configuredPageCount = state.value.settings.homePageCount,
            tiles = state.value.layoutTiles,
        )

    fun contactTiles(): List<HomeTile> = HomeLayoutRules.contactTiles(state.value.layoutTiles)

    private fun requestProtectedRoute(action: ProtectedAction): String {
        localState.update { it.copy(pendingAction = action, pinError = null, pinInput = "") }
        if (state.value.caregiverSessionActive) {
            localState.update { it.copy(pendingAction = null) }
            return destinationFor(action)
        }
        val settings = state.value.settings
        val hasPin = !settings.pinHashHex.isNullOrBlank() && !settings.pinSaltHex.isNullOrBlank()
        val requiresPin = CaregiverProtectionRules.requiresPin(
            protectionEnabled = settings.caregiverProtectionEnabled,
            hasPinConfigured = hasPin,
            action = action,
        )
        return if (requiresPin) {
            Routes.PinVerify.route
        } else {
            localState.update { it.copy(pendingAction = null, caregiverSessionActive = true) }
            destinationFor(action)
        }
    }

    private fun updateLayout(transform: (List<HomeTile>) -> List<HomeTile>) {
        viewModelScope.launch(container.ioDispatcher) {
            val updated = HomeLayoutRules.ensureRequiredActions(transform(container.homeLayoutRepository.getTiles()))
            container.homeLayoutRepository.replaceTiles(updated)
        }
    }

    private fun destinationFor(action: ProtectedAction): String =
        when (action) {
            ProtectedAction.OPEN_CAREGIVER_SETTINGS -> Routes.CaregiverTools.route
            ProtectedAction.MANAGE_LAYOUT_PAGES -> Routes.LayoutPages.route
            ProtectedAction.MANAGE_ALLOWED_APPS -> Routes.AllowedApps.route
            ProtectedAction.MANAGE_FAVORITE_CONTACTS -> Routes.ManageContacts.route
            ProtectedAction.RESET_LAUNCHER -> Routes.ResetLauncher.route
            ProtectedAction.CHANGE_PIN -> Routes.PinSetup.route
            ProtectedAction.TOGGLE_PROTECTION -> Routes.CaregiverTools.route
            ProtectedAction.TOGGLE_LAYOUT_LOCK -> Routes.CaregiverTools.route
            ProtectedAction.MANAGE_HIDDEN_APPS -> Routes.ManageHiddenApps.route
        }

    private fun com.easyui.core.domain.model.LauncherSettings.toPinCredential(): PinCredential? {
        val salt = pinSaltHex ?: return null
        val hash = pinHashHex ?: return null
        return PinCredential(saltHex = salt, hashHex = hash)
    }

    private fun displayName(raw: String): String =
        raw.lowercase()
            .split("_")
            .joinToString(" ") { part -> part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
}
