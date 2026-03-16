package com.easyui.launcher.app.caregiver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.AppVisibilityPreset
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.model.ProtectedAction
import com.easyui.core.domain.rules.AppVisibilityPresetRules
import com.easyui.core.domain.rules.CaregiverProtectionRules
import com.easyui.core.domain.rules.ContactTileRules
import com.easyui.core.domain.rules.HiddenAppRules
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
            local.copy(
                settings = settings,
                hiddenPackages = hiddenPackages,
                installedApps = apps,
                layoutTiles = layoutTiles,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CaregiverUiState(),
        )

    fun beginProtectedAction(action: ProtectedAction): String {
        val settings = state.value.settings
        val hasPin = !settings.pinHashHex.isNullOrBlank() && !settings.pinSaltHex.isNullOrBlank()
        val requiresPin = CaregiverProtectionRules.requiresPin(
            protectionEnabled = settings.caregiverProtectionEnabled,
            hasPinConfigured = hasPin,
            action = action,
        )
        localState.update { it.copy(pendingAction = action, pinError = null, pinInput = "") }
        return if (requiresPin) {
            Routes.PinVerify.route
        } else {
            if (action == ProtectedAction.ENTER_EDIT_MODE && settings.layoutLocked) {
                viewModelScope.launch {
                    container.launcherSettingsRepository.updateLayoutLocked(false)
                }
            }
            localState.update { it.copy(pendingAction = null) }
            destinationFor(action)
        }
    }

    fun updatePinInput(value: String) {
        localState.update { it.copy(pinInput = value, pinError = null) }
    }

    fun updateConfirmPinInput(value: String) {
        localState.update { it.copy(confirmPinInput = value, pinError = null) }
    }

    fun submitPinSetup(): Boolean {
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
            container.launcherSettingsRepository.storePinCredential(credential)
            container.launcherSettingsRepository.updateCaregiverProtectionEnabled(true)
            container.launcherSettingsRepository.updateLayoutLocked(true)
            localState.update { it.copy(pinInput = "", confirmPinInput = "", pinError = null, pendingAction = null) }
            messages.emit("Caregiver PIN is now active.")
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
        localState.update { it.copy(pinInput = "", pinError = null, pendingAction = null) }
        return when (pendingAction) {
            ProtectedAction.ENTER_EDIT_MODE -> {
                if (state.value.settings.layoutLocked) {
                    viewModelScope.launch {
                        container.launcherSettingsRepository.updateLayoutLocked(false)
                    }
                }
                Routes.EditLayout.route
            }
            ProtectedAction.TOGGLE_PROTECTION -> {
                toggleProtectionEnabled()
                Routes.CaregiverTools.route
            }
            ProtectedAction.TOGGLE_LAYOUT_LOCK -> {
                toggleLayoutLock()
                Routes.CaregiverTools.route
            }
            else -> destinationFor(pendingAction)
        }
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
            messages.emit(if (locked) "Home layout is locked." else "Home layout can now be edited.")
        }
    }

    fun moveTileUp(tileId: String) {
        updateLayout { tiles ->
            val index = tiles.indexOfFirst { it.id == tileId }
            if (index <= 0) tiles else tiles.toMutableList().apply { add(index - 1, removeAt(index)) }
        }
    }

    fun moveTileDown(tileId: String) {
        updateLayout { tiles ->
            val index = tiles.indexOfFirst { it.id == tileId }
            if (index == -1 || index == tiles.lastIndex) tiles else tiles.toMutableList().apply { add(index + 1, removeAt(index)) }
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
        updateLayout { tiles ->
            HomeLayoutRules.upsertContactTile(
                tiles = tiles,
                tile = HomeTile(
                    id = tileId ?: "contact-${System.currentTimeMillis()}",
                    position = tiles.size,
                    title = displayName.trim(),
                    type = HomeTileType.CONTACT,
                    phoneNumber = phoneNumber.trim(),
                    photoUri = photoUri,
                ),
            )
        }
        return null
    }

    fun addAppTile(app: InstalledApp) {
        updateLayout { tiles ->
            if (tiles.any { it.packageName == app.packageName }) return@updateLayout tiles
            tiles + HomeTile(
                id = "app-${app.packageName}",
                position = tiles.size,
                title = app.label,
                type = HomeTileType.APP,
                packageName = app.packageName,
            )
        }
    }

    fun setHidden(packageName: String, hidden: Boolean) {
        viewModelScope.launch {
            container.hiddenAppRepository.setHidden(packageName, hidden)
            container.launcherSettingsRepository.updateAppVisibilityPreset(AppVisibilityPreset.CUSTOM.name)
        }
    }

    fun applyVisibilityPreset(preset: AppVisibilityPreset) {
        viewModelScope.launch(container.ioDispatcher) {
            if (preset == AppVisibilityPreset.CUSTOM) {
                container.launcherSettingsRepository.updateAppVisibilityPreset(preset.name)
                messages.emit("EasyUI kept the current app visibility setup.")
                return@launch
            }
            val hiddenPackages = AppVisibilityPresetRules.hiddenPackagesForPreset(
                apps = state.value.installedApps,
                preset = preset,
            )
            container.hiddenAppRepository.clearHiddenPackages()
            hiddenPackages.forEach { packageName ->
                container.hiddenAppRepository.setHidden(packageName, true)
            }
            container.launcherSettingsRepository.updateAppVisibilityPreset(preset.name)
            val message = when (preset) {
                AppVisibilityPreset.CUSTOM -> "EasyUI kept the current app visibility setup."
                AppVisibilityPreset.ESSENTIALS_ONLY -> "EasyUI now shows only essential apps."
                AppVisibilityPreset.MINIMAL_COMMON_APPS -> "EasyUI now shows a small set of common apps."
            }
            messages.emit(message)
        }
    }

    fun updateHomeReadabilityPreset(preset: HomeReadabilityPreset) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateHomeReadabilityPreset(preset.name)
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
            messages.emit(if (enabled) "Very simple home mode is now on." else "Very simple home mode is now off.")
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
            messages.emit("EasyUI is back to its safe default layout.")
        }
    }

    fun visibleAppsForHiddenSettings(): List<InstalledApp> =
        state.value.installedApps

    fun availableAppsForLayout(): List<InstalledApp> {
        val currentPackages = state.value.layoutTiles.mapNotNull { it.packageName }.toSet()
        return HiddenAppRules.visibleApps(state.value.installedApps, state.value.hiddenPackages)
            .filterNot { it.packageName in currentPackages }
    }

    fun editableTiles(): List<HomeTile> = HomeLayoutRules.normalize(state.value.layoutTiles)

    fun contactTiles(): List<HomeTile> = HomeLayoutRules.contactTiles(state.value.layoutTiles)

    fun clearPendingAction() {
        localState.update { it.copy(pendingAction = null, pinInput = "", pinError = null) }
    }

    private fun updateLayout(transform: (List<HomeTile>) -> List<HomeTile>) {
        viewModelScope.launch(container.ioDispatcher) {
            val updated = HomeLayoutRules.normalize(transform(container.homeLayoutRepository.getTiles()))
            container.homeLayoutRepository.replaceTiles(updated)
        }
    }

    private fun destinationFor(action: ProtectedAction): String =
        when (action) {
            ProtectedAction.ENTER_EDIT_MODE -> Routes.EditLayout.route
            ProtectedAction.MANAGE_HOME_DISPLAY -> Routes.HomeDisplay.route
            ProtectedAction.MANAGE_APP_VISIBILITY -> Routes.HiddenApps.route
            ProtectedAction.MANAGE_FAVORITE_CONTACTS -> Routes.ManageContacts.route
            ProtectedAction.RESET_LAUNCHER -> Routes.ResetLauncher.route
            ProtectedAction.CHANGE_PIN -> Routes.PinSetup.route
            ProtectedAction.TOGGLE_PROTECTION -> Routes.CaregiverTools.route
            ProtectedAction.TOGGLE_LAYOUT_LOCK -> Routes.CaregiverTools.route
        }

    private fun com.easyui.core.domain.model.LauncherSettings.toPinCredential(): PinCredential? {
        val salt = pinSaltHex ?: return null
        val hash = pinHashHex ?: return null
        return PinCredential(saltHex = salt, hashHex = hash)
    }
}
