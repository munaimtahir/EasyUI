package com.easyui.launcher.navigation

sealed class Routes(val route: String) {
    data object Intro : Routes("intro")
    data object LauncherGuidance : Routes("launcher_guidance")
    data object CaregiverHelp : Routes("caregiver_help")
    data object Home : Routes("home")
    data object AppList : Routes("app_list")
    data object CaregiverTools : Routes("caregiver_tools")
    data object HomeDisplay : Routes("home_display")
    data object PinSetup : Routes("pin_setup")
    data object PinVerify : Routes("pin_verify")
    data object EditLayout : Routes("edit_layout")
    data object ManageContacts : Routes("manage_contacts")
    data object HiddenApps : Routes("hidden_apps")
    data object ResetLauncher : Routes("reset_launcher")
}
