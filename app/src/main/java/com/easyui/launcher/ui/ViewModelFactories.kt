package com.easyui.launcher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.easyui.launcher.app.AppListViewModel
import com.easyui.launcher.app.AppViewModel
import com.easyui.launcher.app.HomeViewModel
import com.easyui.launcher.app.caregiver.BackupViewModel
import com.easyui.launcher.app.caregiver.CaregiverViewModel
import com.easyui.launcher.di.AppContainer

class AppViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel(container) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(container) as T
            modelClass.isAssignableFrom(AppListViewModel::class.java) -> AppListViewModel(container) as T
            modelClass.isAssignableFrom(CaregiverViewModel::class.java) -> CaregiverViewModel(container) as T
            modelClass.isAssignableFrom(BackupViewModel::class.java) -> BackupViewModel(container) as T
            modelClass.isAssignableFrom(com.easyui.launcher.app.GuidedSetupViewModel::class.java) -> com.easyui.launcher.app.GuidedSetupViewModel(container) as T
            else -> error("Unknown view model: ${modelClass.name}")
        }
}
