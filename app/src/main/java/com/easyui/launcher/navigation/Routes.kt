package com.easyui.launcher.navigation

sealed class Routes(val route: String) {
    data object Intro : Routes("intro")
    data object LauncherGuidance : Routes("launcher_guidance")
    data object CaregiverHelp : Routes("caregiver_help")
    data object Home : Routes("home")
    data object AppList : Routes("app_list")
    data object CaregiverTools : Routes("caregiver_tools")
    data object LayoutPages : Routes("layout_pages")
    data object AllowedApps : Routes("allowed_apps")
    data object PinSetup : Routes("pin_setup")
    data object PinVerify : Routes("pin_verify")
    data object ManageContacts : Routes("manage_contacts")
    data object ResetLauncher : Routes("reset_launcher")
    data object EmergencySettings : Routes("emergency_settings")
    data object BackupRestore : Routes("backup_restore")
    data object ManageHiddenApps : Routes("manage_hidden_apps")
}
