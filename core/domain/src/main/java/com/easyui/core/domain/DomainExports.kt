package com.easyui.core.domain

typealias AccessibilityMode = com.easyui.core.domain.model.AccessibilityMode
typealias BatteryStatus = com.easyui.core.domain.model.BatteryStatus
typealias DeviceStatus = com.easyui.core.domain.model.DeviceStatus
typealias InstalledApp = com.easyui.core.domain.model.InstalledApp
typealias LauncherActionState = com.easyui.core.domain.model.LauncherActionState
typealias LayoutMode = com.easyui.core.domain.model.LayoutMode
typealias SkinConfig = com.easyui.core.domain.model.SkinConfig
typealias VisualTheme = com.easyui.core.domain.model.VisualTheme

typealias AppCatalogRepository = com.easyui.core.domain.repository.AppCatalogRepository
typealias AppLauncher = com.easyui.core.domain.repository.AppLauncher
typealias BatteryStatusRepository = com.easyui.core.domain.repository.BatteryStatusRepository
typealias CameraActionHandler = com.easyui.core.domain.repository.CameraActionHandler
typealias DefaultLauncherManager = com.easyui.core.domain.repository.DefaultLauncherManager
typealias DeviceStatusRepository = com.easyui.core.domain.repository.DeviceStatusRepository
typealias EmergencyActionHandler = com.easyui.core.domain.repository.EmergencyActionHandler
typealias FlashlightController = com.easyui.core.domain.repository.FlashlightController

object ActionAvailabilityResolver {
    fun flashlight(isSupported: Boolean) =
        com.easyui.core.domain.rules.ActionAvailabilityResolver.flashlight(isSupported)

    fun dialer(hasDialer: Boolean) =
        com.easyui.core.domain.rules.ActionAvailabilityResolver.dialer(hasDialer)

    fun emergency(hasDialer: Boolean, phoneNumber: String) =
        com.easyui.core.domain.rules.ActionAvailabilityResolver.emergency(hasDialer, phoneNumber)

    fun camera(hasCamera: Boolean) =
        com.easyui.core.domain.rules.ActionAvailabilityResolver.camera(hasCamera)
}

object AppCatalogRules {
    fun sortAlphabetically(apps: List<InstalledApp>) =
        com.easyui.core.domain.rules.AppCatalogRules.sortAlphabetically(apps)

    fun filterByQuery(apps: List<InstalledApp>, query: String) =
        com.easyui.core.domain.rules.AppCatalogRules.filterByQuery(apps, query)

    fun filterHiddenApps(apps: List<InstalledApp>, hiddenPackages: Set<String>) =
        com.easyui.core.domain.rules.AppCatalogRules.filterHiddenApps(apps, hiddenPackages)
}
