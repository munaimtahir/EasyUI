package com.easyui.launcher.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.AppVisibilityPreset
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.LayoutMode
import com.easyui.core.domain.model.OptionalPermission
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.model.SetupProtectionLevel
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.model.VisualTheme
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.domain.security.PinHasher
import com.easyui.feature.onboarding.GuidedSetupStep
import com.easyui.launcher.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GuidedSetupUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = GuidedSetupStep.entries.size,
    val settingsLoaded: Boolean = false,
    val onboardingComplete: Boolean = false,
    val guidedSetupCompleted: Boolean = false,
    val guidedSetupStep: Int = 1, // This is the index (1-based)
    val currentStepEnum: GuidedSetupStep = GuidedSetupStep.LAUNCHER_ACTIVATION,
    val isDefaultLauncher: Boolean = false,
    val homeReadabilityPreset: HomeReadabilityPreset = HomeReadabilityPreset.STANDARD,
    val homePageCount: Int = 2,
    val setupProtectionLevel: SetupProtectionLevel = SetupProtectionLevel.RECOMMENDED,
    val setupOptionalPermissions: Set<OptionalPermission> = emptySet(),
    val pinInput: String = "",
    val confirmPinInput: String = "",
    val pinError: String? = null,
    val hasPinConfigured: Boolean = false,
    val layoutLocked: Boolean = false,
    val emergencyMode: String = "MENU",
    val emergencyPhoneNumber: String = "911",
)

class GuidedSetupViewModel(
    private val container: AppContainer,
) : ViewModel() {

    private val _localState = MutableStateFlow(GuidedSetupUiState())
    private val _launcherRefreshTrigger = MutableStateFlow(0)

    val state: StateFlow<GuidedSetupUiState> = combine(
        container.launcherSettingsRepository.settings,
        _launcherRefreshTrigger,
        _localState
    ) { settings, _, local ->
        val stepIndex = settings.guidedSetupStep.coerceIn(1, GuidedSetupStep.entries.size)
        local.copy(
            settingsLoaded = true,
            onboardingComplete = settings.onboardingComplete,
            guidedSetupCompleted = settings.guidedSetupCompleted,
            guidedSetupStep = stepIndex,
            currentStep = stepIndex,
            totalSteps = GuidedSetupStep.entries.size,
            currentStepEnum = GuidedSetupStep.entries[stepIndex - 1],
            homeReadabilityPreset = try {
                HomeReadabilityPreset.valueOf(settings.homeReadabilityPreset)
            } catch (e: Exception) {
                HomeReadabilityPreset.STANDARD
            },
            homePageCount = settings.homePageCount,
            isDefaultLauncher = container.defaultLauncherManager.isDefaultLauncher(),
            hasPinConfigured = !settings.pinHashHex.isNullOrBlank(),
            layoutLocked = settings.layoutLocked,
            setupProtectionLevel = runCatching { SetupProtectionLevel.valueOf(settings.setupProtectionLevel) }
                .getOrDefault(SetupProtectionLevel.RECOMMENDED),
            setupOptionalPermissions = settings.setupOptionalPermissions.mapNotNull { name ->
                runCatching { OptionalPermission.valueOf(name) }.getOrNull()
            }.toSet(),
            emergencyMode = settings.emergencyMode,
            emergencyPhoneNumber = settings.emergencyPhoneNumber
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GuidedSetupUiState())

    fun setStep(step: Int) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateGuidedSetupStep(step.coerceIn(1, GuidedSetupStep.entries.size))
        }
    }

    fun nextStep() {
        val current = state.value.guidedSetupStep
        if (current < GuidedSetupStep.entries.size) {
            setStep(current + 1)
        } else {
            completeSetup()
        }
    }

    fun previousStep() {
        val current = state.value.guidedSetupStep
        if (current > 1) {
            setStep(current - 1)
        }
    }

    fun completeSetup() {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateGuidedSetupCompleted(true)
            container.launcherSettingsRepository.updateOnboardingComplete(true)
        }
    }

    fun updateEmergencyNumber(number: String) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateEmergencyPhoneNumber(number)
        }
    }

    fun updateEmergencyMode(mode: String) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateEmergencyMode(mode)
            val currentTiles = container.homeLayoutRepository.getTiles()
            val updatedTiles = HomeLayoutRules.updateEmergencyAction(currentTiles, mode == "SOS")
            container.homeLayoutRepository.replaceTiles(updatedTiles)
        }
    }

    fun updateEmergencyPhoneNumber(number: String) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateEmergencyPhoneNumber(number)
        }
    }

    fun updateLayoutLocked(locked: Boolean) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateLayoutLocked(locked)
        }
    }

    fun updateSetupProtectionLevel(level: SetupProtectionLevel) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateSetupProtectionLevel(level.name)
            // Recommended level defaults to locked layout and hidden all apps
            if (level == SetupProtectionLevel.RECOMMENDED) {
                container.launcherSettingsRepository.updateLayoutLocked(true)
                container.launcherSettingsRepository.updateAllAppsVisible(false)
            } else {
                container.launcherSettingsRepository.updateAllAppsVisible(true)
            }
        }
    }

    fun setOptionalPermission(permission: OptionalPermission, enabled: Boolean) {
        val current = state.value.setupOptionalPermissions.toMutableSet()
        if (enabled) current.add(permission) else current.remove(permission)
        viewModelScope.launch {
            container.launcherSettingsRepository.updateSetupOptionalPermissions(current.map { it.name }.toSet())
        }
    }

    fun updateVisualTheme(theme: VisualTheme) {
        viewModelScope.launch {
            val current = container.launcherSettingsRepository.getSkinConfig()
            container.launcherSettingsRepository.setSkinConfig(current.copy(visualTheme = theme))
        }
    }

    fun updateAccessibilityMode(mode: AccessibilityMode) {
        viewModelScope.launch {
            val current = container.launcherSettingsRepository.getSkinConfig()
            container.launcherSettingsRepository.setSkinConfig(current.copy(accessibilityMode = mode))
        }
    }

    fun updateReadabilityPreset(preset: HomeReadabilityPreset) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateHomeReadabilityPreset(preset.name)
            val mappedLayoutMode = when (preset) {
                HomeReadabilityPreset.STANDARD -> LayoutMode.SIMPLE_CLASSIC
                else -> LayoutMode.VERY_SIMPLE
            }
            val currentSkin = container.launcherSettingsRepository.getSkinConfig()
            container.launcherSettingsRepository.setSkinConfig(currentSkin.copy(
                layoutMode = mappedLayoutMode,
                readabilityPreset = preset
            ))
        }
    }

    fun updatePinInput(value: String) {
        _localState.update { it.copy(pinInput = value, pinError = null) }
    }

    fun updateConfirmPinInput(value: String) {
        _localState.update { it.copy(confirmPinInput = value, pinError = null) }
    }

    fun savePin(): Boolean {
        val pin = state.value.pinInput
        val confirm = state.value.confirmPinInput
        if (pin.length < 4) {
            _localState.update { it.copy(pinError = "PIN must be at least 4 digits.") }
            return false
        }
        if (pin != confirm) {
            _localState.update { it.copy(pinError = "PINs do not match.") }
            return false
        }
        viewModelScope.launch {
            val credential = PinHasher.create(pin)
            container.launcherSettingsRepository.storePinCredential(credential)
            container.launcherSettingsRepository.updateCaregiverProtectionEnabled(true)
            _localState.update { it.copy(pinInput = "", confirmPinInput = "", pinError = null) }
        }
        return true
    }

    fun openLauncherSettings() {
        container.defaultLauncherManager.openDefaultLauncherSettings()
        // Refresh after a delay to catch the change when returning
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            refreshLauncherStatus()
        }
    }

    fun refreshLauncherStatus() {
        _launcherRefreshTrigger.update { it + 1 }
    }
}
