package com.easyui.launcher.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.repository.LauncherSettingsRepository
import com.easyui.launcher.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val container: AppContainer,
) : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch(container.ioDispatcher) {
            container.ensureStarterLayout()
            _state.update { it.copy(starterLayoutReady = true) }
        }
        viewModelScope.launch {
            container.launcherSettingsRepository.settings.collect { settings ->
                _state.update { current ->
                    current.copy(settings = settings)
                }
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            container.launcherSettingsRepository.updateOnboardingComplete(true)
        }
    }
}
