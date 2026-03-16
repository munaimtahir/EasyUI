package com.easyui.launcher.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.rules.AppCatalogRules
import com.easyui.core.domain.rules.FallbackStateRules
import com.easyui.launcher.di.AppContainer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppListUiState(
    val query: String = "",
    val apps: List<InstalledApp> = emptyList(),
    val emptyTitle: String? = null,
    val emptyBody: String? = null,
)

class AppListViewModel(
    private val container: AppContainer,
) : ViewModel() {
    private val query = MutableStateFlow("")
    val messages = MutableSharedFlow<String>()

    val state: StateFlow<AppListUiState> =
        combine(
            query,
            container.appCatalogRepository.observeInstalledApps(),
        ) { currentQuery, apps ->
            val visibleApps = AppCatalogRules.filterByQuery(apps, currentQuery)
            val fallbackState = FallbackStateRules.appList(currentQuery, visibleApps.size)
            AppListUiState(
                query = currentQuery,
                apps = visibleApps,
                emptyTitle = fallbackState?.title,
                emptyBody = fallbackState?.body,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppListUiState(),
        )

    fun updateQuery(value: String) {
        query.value = value
    }

    fun launchApp(app: InstalledApp) {
        viewModelScope.launch {
            if (!container.appLauncher.launch(app.packageName, app.activityName)) {
                messages.emit("That app is no longer available.")
            }
        }
    }
}
