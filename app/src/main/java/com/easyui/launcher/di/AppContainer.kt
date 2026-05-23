package com.easyui.launcher.di

import android.content.Context
import com.easyui.core.data.repository.LocalBackupRepository
import com.easyui.core.domain.repository.BackupRepository
import com.easyui.core.data.database.EasyUiDatabase
import com.easyui.core.data.datastore.DataStoreLauncherSettingsRepository
import com.easyui.core.data.repository.DataStoreHiddenAppRepository
import com.easyui.core.data.repository.RoomHomeLayoutRepository
import com.easyui.core.data.repository.DataStoreRemoteLinkRepository
import com.easyui.core.domain.repository.RemoteLinkRepository
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.repository.AppCatalogRepository
import com.easyui.core.domain.repository.AppLauncher
import com.easyui.core.domain.repository.BatteryStatusRepository
import com.easyui.core.domain.repository.CameraActionHandler
import com.easyui.core.domain.repository.DefaultLauncherManager
import com.easyui.core.domain.repository.DeviceStatusRepository
import com.easyui.core.domain.repository.EmergencyActionHandler
import com.easyui.core.domain.repository.FlashlightController
import com.easyui.core.domain.repository.HiddenAppRepository
import com.easyui.core.domain.repository.HomeLayoutRepository
import com.easyui.core.domain.repository.LauncherSettingsRepository
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.platform.actions.AndroidAppLauncher
import com.easyui.core.platform.actions.AndroidBatteryStatusRepository
import com.easyui.core.platform.actions.AndroidCameraActionHandler
import com.easyui.core.platform.actions.AndroidDeviceStatusRepository
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
    val remoteLinkRepository: RemoteLinkRepository = DataStoreRemoteLinkRepository(appContext)
    val flashlightController: FlashlightController = AndroidFlashlightController(appContext)
    val emergencyActionHandler: EmergencyActionHandler = AndroidEmergencyActionHandler(appContext)
    val cameraActionHandler: CameraActionHandler = AndroidCameraActionHandler(appContext)
    val batteryStatusRepository: BatteryStatusRepository = AndroidBatteryStatusRepository(appContext)
    val deviceStatusRepository: DeviceStatusRepository = AndroidDeviceStatusRepository(appContext)
    val appLauncher: AppLauncher = AndroidAppLauncher(appContext)
    val defaultLauncherManager: DefaultLauncherManager = AndroidDefaultLauncherManager(appContext)
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val backupRepository: BackupRepository = LocalBackupRepository(
        homeLayoutRepository = homeLayoutRepository,
        launcherSettingsRepository = launcherSettingsRepository,
        hiddenAppRepository = hiddenAppRepository,
    )

    suspend fun ensureStarterLayout() = withContext(ioDispatcher) {
        val existing = homeLayoutRepository.getTiles()
        val installedApps = appCatalogRepository.getInstalledApps()
        val layout =
            if (existing.isEmpty()) {
                createStarterLayout(installedApps)
            } else {
                HomeLayoutRules.ensureRequiredActions(existing)
            }
        if (layout != existing) {
            homeLayoutRepository.replaceTiles(layout)
        }
    }

    fun createStarterLayout(installedApps: List<InstalledApp>): List<HomeTile> {
        return HomeLayoutRules.starterLayout(installedApps)
    }
}
