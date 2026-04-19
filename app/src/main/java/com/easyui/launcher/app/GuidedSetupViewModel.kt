package com.easyui.launcher.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.AppVisibilityPreset
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.LayoutMode
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.domain.security.PinHasher
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
    val totalSteps: Int = 10,
    val settingsLoaded: Boolean = false,
    val onboardingComplete: Boolean = false,
    val guidedSetupCompleted: Boolean = false,
    val guidedSetupStep: Int = 1,
    val isDefaultLauncher: Boolean = false,
    val homeReadabilityPreset: HomeReadabilityPreset = HomeReadabilityPreset.STANDARD,
    val homePageCount: Int = 2,
    val pinInput: String = "",
    val confirmPinInput: String = "",
    val pinError: String? = null,
    val hasPinConfigured: Boolean = false,
    val layoutLocked: Boolean = false,
    val emergencyMode: String = "MENU",
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
        local.copy(
            settingsLoaded = true,
            onboardingComplete = settings.onboardingComplete,
            guidedSetupCompleted = settings.guidedSetupCompleted,
            guidedSetupStep = settings.guidedSetupStep.coerceAtLeast(1),
            homeReadabilityPreset = try {
                HomeReadabilityPreset.valueOf(settings.homeReadabilityPreset)
            } catch (e: Exception) {
                HomeReadabilityPreset.STANDARD
            },
            homePageCount = settings.homePageCount,
            isDefaultLauncher = container.defaultLauncherManager.isDefaultLauncher(),
            hasPinConfigured = !settings.pinHashHex.isNullOrBlank(),
            layoutLocked = settings.layoutLocked,
            emergencyMode = settings.emergencyMode
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GuidedSetupUiState())

    fun setStep(step: Int) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateGuidedSetupStep(step)
        }
    }

    fun nextStep() {
        val current = state.value.guidedSetupStep
        if (current < state.value.totalSteps) {
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

    fun updateEmergencyMode(mode: String) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateEmergencyMode(mode)
            val currentTiles = container.homeLayoutRepository.getTiles()
            val updatedTiles = HomeLayoutRules.updateEmergencyAction(currentTiles, mode == "SOS")
            container.homeLayoutRepository.replaceTiles(updatedTiles)
        }
    }

    fun updateLayoutLocked(locked: Boolean) {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateLayoutLocked(locked)
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
            container.launcherSettingsRepository.setSkinConfig(currentSkin.copy(layoutMode = mappedLayoutMode))
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
