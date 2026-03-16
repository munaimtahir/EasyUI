package com.easyui.launcher.di

import android.content.Context
import com.easyui.core.data.database.EasyUiDatabase
import com.easyui.core.data.datastore.DataStoreLauncherSettingsRepository
import com.easyui.core.data.repository.DataStoreHiddenAppRepository
import com.easyui.core.data.repository.RoomHomeLayoutRepository
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.repository.AppCatalogRepository
import com.easyui.core.domain.repository.AppLauncher
import com.easyui.core.domain.repository.DefaultLauncherManager
import com.easyui.core.domain.repository.EmergencyActionHandler
import com.easyui.core.domain.repository.FlashlightController
import com.easyui.core.domain.repository.HiddenAppRepository
import com.easyui.core.domain.repository.HomeLayoutRepository
import com.easyui.core.domain.repository.LauncherSettingsRepository
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.platform.actions.AndroidAppLauncher
import com.easyui.core.platform.actions.AndroidEmergencyActionHandler
import com.easyui.core.platform.actions.AndroidFlashlightController
import com.easyui.core.platform.apps.AndroidAppCatalogRepository
import com.easyui.core.platform.launcher.AndroidDefaultLauncherManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppContainer(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val database = EasyUiDatabase.build(appContext)

    val appCatalogRepository: AppCatalogRepository = AndroidAppCatalogRepository(appContext)
    val homeLayoutRepository: HomeLayoutRepository = RoomHomeLayoutRepository(database.homeTileDao())
    val launcherSettingsRepository: LauncherSettingsRepository =
        DataStoreLauncherSettingsRepository(appContext)
    val hiddenAppRepository: HiddenAppRepository = DataStoreHiddenAppRepository(appContext)
    val flashlightController: FlashlightController = AndroidFlashlightController(appContext)
    val emergencyActionHandler: EmergencyActionHandler = AndroidEmergencyActionHandler(appContext)
    val appLauncher: AppLauncher = AndroidAppLauncher(appContext)
    val defaultLauncherManager: DefaultLauncherManager = AndroidDefaultLauncherManager(appContext)
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun ensureStarterLayout() = withContext(ioDispatcher) {
        if (homeLayoutRepository.getTiles().isNotEmpty()) return@withContext
        val installedApps = appCatalogRepository.getInstalledApps()
        homeLayoutRepository.replaceTiles(createStarterLayout(installedApps))
    }

    fun createStarterLayout(installedApps: List<InstalledApp>): List<HomeTile> {
        val starterLayout = HomeLayoutRules.starterLayout(installedApps)
        return if (starterLayout.any { it.action == HomeTileAction.OPEN_APP_LIST }) {
            starterLayout
        } else {
            listOf(
                HomeTile(
                    id = "apps-list",
                    position = 0,
                    title = "All Apps",
                    type = HomeTileType.ACTION,
                    action = HomeTileAction.OPEN_APP_LIST,
                ),
            ) + starterLayout
        }
    }
}
