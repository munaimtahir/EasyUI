package com.easyui.launcher.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.activity.compose.BackHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.easyui.feature.apps.AppListScreen
import com.easyui.feature.caregiver.BackupRestoreScreen
import com.easyui.feature.caregiver.AllowedAppsScreen
import com.easyui.feature.caregiver.CaregiverToolsScreen
import com.easyui.feature.caregiver.EmergencySettingsScreen
import com.easyui.feature.caregiver.FavoriteContactsScreen
import com.easyui.feature.caregiver.HealthInfoEditorScreen
import com.easyui.feature.caregiver.HiddenAppsScreen
import com.easyui.feature.caregiver.LayoutPagesScreen
import com.easyui.feature.caregiver.PinEntryScreen
import com.easyui.feature.caregiver.ResetLauncherScreen
import com.easyui.feature.caregiver.GuardianSettingsScreen
import com.easyui.feature.caregiver.LinkedDevicesScreen
import com.easyui.feature.caregiver.RemoteDeviceDetailScreen
import com.easyui.core.ui.components.ThemeSelector
import com.easyui.feature.home.HealthInfoScreen
import com.easyui.feature.home.AssistedRecoveryScreen
import com.easyui.feature.home.EmergencyCallScreen
import com.easyui.feature.home.HomeScreen
import com.easyui.core.domain.model.RecoveryActionType
import com.easyui.feature.home.PhoneContactsScreen
import com.easyui.feature.home.SeniorMessagesScreen
import com.easyui.feature.home.SeniorPhotosScreen
import com.easyui.feature.home.SeniorCameraScreen
import com.easyui.feature.home.SafeHandoffScreen
import com.easyui.feature.home.SafeFallbackScreen
import com.easyui.feature.onboarding.CaregiverHelpScreen
import com.easyui.feature.onboarding.DefaultLauncherGuidanceScreen
import com.easyui.feature.onboarding.IntroScreen
import com.easyui.feature.onboarding.WelcomeScreen
import com.easyui.feature.onboarding.LauncherActivationScreen
import com.easyui.feature.onboarding.GuidedSetupStep
import com.easyui.feature.onboarding.ProtectionOptionsScreen
import com.easyui.feature.onboarding.ReadabilityPresetScreen
import com.easyui.feature.onboarding.ThemePickerScreen
import com.easyui.feature.onboarding.HomeLayoutSetupScreen
import com.easyui.feature.onboarding.AllowedAppsSetupScreen
import com.easyui.feature.onboarding.ContactsSetupScreen
import com.easyui.feature.onboarding.SecuritySetupScreen
import com.easyui.feature.onboarding.DeviceSupportScreen
import com.easyui.feature.onboarding.ReviewConfirmScreen
import com.easyui.feature.onboarding.CompletionScreen
import com.easyui.launcher.app.AppListViewModel
import com.easyui.launcher.app.AppViewModel
import com.easyui.launcher.app.GuidedSetupViewModel
import com.easyui.launcher.app.HomeViewModel
import com.easyui.launcher.app.caregiver.BackupViewModel
import com.easyui.launcher.app.caregiver.CaregiverViewModel
import com.easyui.launcher.app.caregiver.RemoteLinkViewModel
import com.easyui.launcher.di.AppContainer
import com.easyui.launcher.ui.AppViewModelFactory
import com.easyui.core.domain.model.ProtectedAction
import com.easyui.core.domain.rules.PrimaryHomeAppKind
import com.easyui.core.domain.rules.PrimaryHomeAppRules
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.security.PinHasher
import com.easyui.core.ui.theme.EasyUiSpacing
import com.easyui.core.ui.theme.EasyUiTheme
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyUiNavGraph(
    container: AppContainer,
    navController: NavHostController = rememberNavController(),
    initialIntent: Intent? = null,
) {
    val factory = remember(container) { AppViewModelFactory(container) }
    val appViewModel: AppViewModel = viewModel(factory = factory)
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val appListViewModel: AppListViewModel = viewModel(factory = factory)
    val caregiverViewModel: CaregiverViewModel = viewModel(factory = factory)
    val remoteLinkViewModel: RemoteLinkViewModel = viewModel(factory = factory)
    val backupViewModel: BackupViewModel = viewModel(factory = factory)
    val guidedSetupViewModel: GuidedSetupViewModel = viewModel(factory = factory)
    val appState by appViewModel.state.collectAsState()
    val homeState by homeViewModel.state.collectAsState()
    val appListState by appListViewModel.state.collectAsState()
    val caregiverState by caregiverViewModel.state.collectAsState()
    val remoteLinkDevices by remoteLinkViewModel.linkedDevices.collectAsState()
    val guidedSetupState by guidedSetupViewModel.state.collectAsState()
    val uiScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(remoteLinkViewModel) {
        remoteLinkViewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(initialIntent) {
        val data = initialIntent?.dataString
        if (data != null && data.startsWith("easyui://status")) {
            remoteLinkViewModel.importStatusFromDeepLink(data)
            navController.navigate(Routes.LinkedDevices.route)
        }
    }
    var launcherStatusVersion by remember { mutableIntStateOf(0) }
    var lastInteractionAt by remember { mutableLongStateOf(android.os.SystemClock.elapsedRealtime()) }
    val alertCaregiver = {
        val packet = com.easyui.core.domain.model.RemoteStatusPacket(
            deviceName = android.os.Build.MODEL,
            healthState = homeState.healthState,
            setupCompleteness = caregiverState.setupCompleteness
        )
        val link = remoteLinkViewModel.generateShareLink(packet)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "EasyUI Remote Status for ${packet.deviceName}:\n$link")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Status Link"))
    }
    var easyUiLocked by remember { mutableStateOf(false) }
    var lockPinInput by remember { mutableStateOf("") }
    var lockPinError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(homeViewModel) {
        homeViewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(appListViewModel) {
        appListViewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(caregiverViewModel) {
        caregiverViewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(appState.settings.easyUiLockEnabled, appState.settings.easyUiLockTimeoutSeconds) {
        while (true) {
            kotlinx.coroutines.delay(1_000)
            if (!appState.settings.easyUiLockEnabled) {
                easyUiLocked = false
                continue
            }
            val elapsed = android.os.SystemClock.elapsedRealtime() - lastInteractionAt
            if (elapsed >= appState.settings.easyUiLockTimeoutSeconds * 1_000L) {
                easyUiLocked = true
            }
        }
    }
    DisposableEffect(lifecycleOwner, appState.settings.easyUiLockEnabled) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                guidedSetupViewModel.refreshLauncherStatus()
                if (appState.settings.easyUiLockEnabled) {
                    easyUiLocked = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    EasyUiTheme(skinConfig = appState.settings.skinConfig) {
        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        if (!appState.settingsLoaded || !appState.starterLayoutReady) {
            LoadingScreen(modifier = androidx.compose.ui.Modifier.padding(innerPadding))
            return@Scaffold
        }
        val startDestination =
            if (appState.settings.onboardingComplete) Routes.Home.route else Routes.GuidedSetup.route
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures { lastInteractionAt = android.os.SystemClock.elapsedRealtime() }
                },
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable(Routes.GuidedSetup.route) {
                    // Force state reads here so that screens like AllowedAppsSetupScreen recompose correctly
                    val currentTiles = caregiverState.layoutTiles
                    val currentSettings = caregiverState.settings
                    
                    when (guidedSetupState.currentStepEnum) {
                        GuidedSetupStep.LAUNCHER_ACTIVATION -> LauncherActivationScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            isDefaultLauncher = guidedSetupState.isDefaultLauncher,
                            onOpenSettings = { guidedSetupViewModel.openLauncherSettings() },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        GuidedSetupStep.WELCOME -> WelcomeScreen(onNext = { guidedSetupViewModel.nextStep() })
                        GuidedSetupStep.PROTECTION_OPTIONS -> ProtectionOptionsScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            current = guidedSetupState.setupProtectionLevel,
                            onSelect = { level ->
                                guidedSetupViewModel.updateSetupProtectionLevel(level)
                                guidedSetupViewModel.updateLayoutLocked(level == com.easyui.core.domain.model.SetupProtectionLevel.RECOMMENDED)
                            },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() },
                        )
                        GuidedSetupStep.SECURITY_SETUP -> SecuritySetupScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            pin = guidedSetupState.pinInput,
                            confirmPin = guidedSetupState.confirmPinInput,
                            errorMessage = guidedSetupState.pinError,
                            layoutLocked = guidedSetupState.layoutLocked,
                            onLayoutLockedChange = { guidedSetupViewModel.updateLayoutLocked(it) },
                            onPinChange = { guidedSetupViewModel.updatePinInput(it) },
                            onConfirmPinChange = { guidedSetupViewModel.updateConfirmPinInput(it) },
                            onNext = { 
                                val pinEmpty = guidedSetupState.pinInput.isEmpty() && guidedSetupState.confirmPinInput.isEmpty()
                                if (pinEmpty || guidedSetupViewModel.savePin()) {
                                    guidedSetupViewModel.nextStep()
                                }
                            },
                            onBack = { guidedSetupViewModel.previousStep() },
                            onSkip = { guidedSetupViewModel.nextStep() }
                        )
                        GuidedSetupStep.THEME_PICKER -> ThemePickerScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            visualTheme = appState.settings.skinConfig.visualTheme,
                            accessibilityMode = appState.settings.skinConfig.accessibilityMode,
                            onThemeSelected = { theme, mode ->
                                guidedSetupViewModel.updateSkinConfig(theme, mode)
                            },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() },
                        )
                        GuidedSetupStep.READABILITY_PRESET -> ReadabilityPresetScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            currentPreset = guidedSetupState.homeReadabilityPreset,
                            onPresetSelected = { guidedSetupViewModel.updateReadabilityPreset(it) },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        GuidedSetupStep.HOME_LAYOUT_SETUP -> HomeLayoutSetupScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            homePageCount = guidedSetupState.homePageCount,
                            onPageCountChange = { caregiverViewModel.updateHomePageCount(it) },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        GuidedSetupStep.ALLOWED_APPS_SETUP -> AllowedAppsSetupScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            pageCount = caregiverViewModel.effectivePageCount(),
                            pages = caregiverViewModel.homePages(),
                            installedApps = caregiverViewModel.installedAppsForAllowedApps(),
                            assignedAppPackages = caregiverViewModel.assignedAppPackages(),
                            onAssignApp = caregiverViewModel::assignAllowedApp,
                            onRemoveApp = caregiverViewModel::removeAllowedApp,
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        GuidedSetupStep.CONTACTS_SETUP -> ContactsSetupScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            tiles = caregiverViewModel.contactTiles(),
                            onMoveUp = caregiverViewModel::moveTileUp,
                            onMoveDown = caregiverViewModel::moveTileDown,
                            onEdit = caregiverViewModel::saveContactTile,
                            onRemove = caregiverViewModel::removeTile,
                            emergencyMode = guidedSetupState.emergencyMode,
                            onEmergencyModeChange = { guidedSetupViewModel.updateEmergencyMode(it) },
                            emergencyPhoneNumber = guidedSetupState.emergencyPhoneNumber,
                            onEmergencyPhoneNumberChange = { guidedSetupViewModel.updateEmergencyPhoneNumber(it) },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        GuidedSetupStep.DEVICE_SUPPORT -> DeviceSupportScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            showBattery = caregiverState.settings.showBatteryInfo,
                            onToggleBattery = { caregiverViewModel.setBatteryInfoVisible(it) },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        GuidedSetupStep.REVIEW_CONFIRM -> ReviewConfirmScreen(
                            currentStep = guidedSetupState.currentStep,
                            totalSteps = guidedSetupState.totalSteps,
                            onConfirm = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() },
                            readability = guidedSetupState.homeReadabilityPreset.name.replace("_", " "),
                            pageCount = caregiverViewModel.effectivePageCount(),
                            allowedAppCount = caregiverViewModel.assignedAppPackages().size,
                            emergencyMode = guidedSetupState.emergencyMode,
                            layoutLocked = guidedSetupState.layoutLocked,
                            hasPin = guidedSetupState.hasPinConfigured
                        )
                        GuidedSetupStep.COMPLETION -> CompletionScreen(
                            onFinish = {
                                guidedSetupViewModel.completeSetup()
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.GuidedSetup.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
                composable(Routes.Home.route) {
                    BackHandler(enabled = true) {}
                    val openCaregiverAccess: () -> Unit = {
                        navController.navigate(caregiverViewModel.requestCaregiverAccess())
                    }
                    HomeScreen(
                        timeText = homeState.timeText,
                        dateText = homeState.dateText,
                        tiles = homeState.tiles,
                        skinConfig = homeState.skinConfig,
                        pageCount = homeState.pageCount,
                        layoutLocked = homeState.layoutLocked,
                        batteryPercentage = homeState.batteryPercentage,
                        isCharging = homeState.isCharging,
                        isBatteryLow = homeState.isBatteryLow,
                        showBatteryInfo = homeState.showBatteryInfo,
                        onTileClick = { tileId ->
                            homeViewModel.onTileClick(
                                tileId = tileId,
                                onOpenPhoneContacts = { navController.navigate(Routes.PhoneContacts.route) },
                                onOpenEmergency = { navController.navigate(Routes.EmergencyCall.route) },
                                onOpenMessages = { navController.navigate(Routes.Messages.route) },
                                onOpenPhotos = { navController.navigate(Routes.Photos.route) },
                                onOpenCamera = { navController.navigate(Routes.Camera.route) },
                            )
                        },
                        onOpenAppList = { navController.navigate(Routes.AppList.route) },
                        onStatusBarLongPress = {
                            homeViewModel.onTopBarLongPressCaregiverAccess(openCaregiverAccess)
                        },
                        onClockTapped = {
                            homeViewModel.onClockTappedCaregiverAccess(openCaregiverAccess)
                        },
                        onAlertCaregiver = { alertCaregiver() },
                        onOpenRecovery = {
                            navController.navigate(Routes.AssistedRecovery.route)
                        },
                        healthState = homeState.healthState
                    )
                }
                composable(Routes.AssistedRecovery.route) {
                    val guidance = homeState.healthState.primaryRecoveryGuidance
                    if (guidance != null) {
                        AssistedRecoveryScreen(
                            guidance = guidance,
                            onExecuteAction = {
                                try {
                                    when (guidance.type) {
                                        RecoveryActionType.SET_DEFAULT_LAUNCHER -> {
                                            container.defaultLauncherManager.triggerLauncherChooser()
                                        }
                                        RecoveryActionType.OPEN_WIFI_SETTINGS -> {
                                            val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            if (!com.easyui.core.platform.util.IntentHardener.attemptLaunch(context, intent)) {
                                                navController.navigate(Routes.SafeFallback.createRoute("Wi-Fi Settings"))
                                            }
                                        }
                                        RecoveryActionType.OPEN_BATTERY_SETTINGS -> {
                                            val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            if (!com.easyui.core.platform.util.IntentHardener.attemptLaunch(context, intent)) {
                                                navController.navigate(Routes.SafeFallback.createRoute("Battery Settings"))
                                            }
                                        }
                                        RecoveryActionType.OPEN_EMERGENCY_SETTINGS -> {
                                            navController.navigate(Routes.EmergencySettings.route)
                                        }
                                        RecoveryActionType.OPEN_CAREGIVER_TOOLS -> {
                                            navController.navigate(Routes.CaregiverTools.route)
                                        }
                                        RecoveryActionType.REQUEST_PERMISSIONS -> {
                                            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts("package", context.packageName, null)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            if (!com.easyui.core.platform.util.IntentHardener.attemptLaunch(context, intent)) {
                                                navController.navigate(Routes.SafeFallback.createRoute("Permission Settings"))
                                            }
                                        }
                                        RecoveryActionType.FIX_BATTERY_OPTIMIZATION -> {
                                            container.deviceStatusRepository.requestIgnoreBatteryOptimizations()
                                        }
                                        RecoveryActionType.NONE -> {}
                                    }
                                } catch (e: Exception) {
                                    uiScope.launch {
                                        snackbarHostState.showSnackbar("Could not open system settings. Please find them manually.")
                                    }
                                }
                            },
                            onAlertCaregiver = { alertCaregiver() },
                            onBackHome = { navController.popBackStack() }
                        )
                    } else {
                        navController.popBackStack()
                    }
                }
                composable(Routes.PhoneContacts.route) {
                    PhoneContactsScreen(
                        contacts = caregiverViewModel.contactTiles().take(10),
                        onCall = { number ->
                            uiScope.launch {
                                val launched = container.emergencyActionHandler.callPhone(number)
                                if (!launched) {
                                    navController.navigate(Routes.SafeFallback.createRoute("Calling"))
                                }
                            }
                        },
                        onOpenDialer = {
                            uiScope.launch {
                                val launched = container.emergencyActionHandler.launchDialer(null)
                                if (!launched) {
                                    navController.navigate(Routes.SafeFallback.createRoute("Phone Dialer"))
                                }
                            }
                        },
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) },
                    )
                }
                composable(Routes.Messages.route) {
                    val app = PrimaryHomeAppRules.resolve(PrimaryHomeAppKind.MESSAGES, homeState.installedApps)
                    SeniorMessagesScreen(
                        onOpenMessages = {
                            navController.navigate(Routes.SafeHandoff.createRoute("Messages", app?.packageName, app?.activityName))
                        },
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) }
                    )
                }
                composable(Routes.Photos.route) {
                    val app = PrimaryHomeAppRules.resolve(PrimaryHomeAppKind.PHOTOS, homeState.installedApps)
                    SeniorPhotosScreen(
                        onOpenPhotos = {
                            navController.navigate(Routes.SafeHandoff.createRoute("Photos", app?.packageName, app?.activityName))
                        },
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) }
                    )
                }
                composable(Routes.Camera.route) {
                    val app = PrimaryHomeAppRules.resolve(PrimaryHomeAppKind.CAMERA, homeState.installedApps)
                    SeniorCameraScreen(
                        onOpenCamera = {
                            navController.navigate(Routes.SafeHandoff.createRoute("Camera", app?.packageName, app?.activityName))
                        },
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) }
                    )
                }
                composable(
                    route = Routes.SafeHandoff.route,
                    arguments = listOf(
                        androidx.navigation.navArgument("action") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("packageName") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("activityName") { type = androidx.navigation.NavType.StringType }
                    )
                ) { backStackEntry ->
                    val action = backStackEntry.arguments?.getString("action") ?: ""
                    val packageName = backStackEntry.arguments?.getString("packageName")?.takeIf { it != "none" }
                    val activityName = backStackEntry.arguments?.getString("activityName")?.takeIf { it != "none" }

                    SafeHandoffScreen(
                        actionTitle = action,
                        onContinue = {
                            uiScope.launch {
                                if (packageName != null && activityName != null) {
                                    val launched = container.appLauncher.launch(packageName, activityName)
                                    if (!launched) {
                                        navController.navigate(Routes.SafeFallback.createRoute(action)) {
                                            popUpTo(Routes.SafeHandoff.route) { inclusive = true }
                                        }
                                    } else {
                                        navController.popBackStack()
                                    }
                                } else {
                                    navController.navigate(Routes.SafeFallback.createRoute(action)) {
                                        popUpTo(Routes.SafeHandoff.route) { inclusive = true }
                                    }
                                }
                            }
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Routes.SafeFallback.route,
                    arguments = listOf(
                        androidx.navigation.navArgument("featureName") { type = androidx.navigation.NavType.StringType }
                    )
                ) { backStackEntry ->
                    val featureName = backStackEntry.arguments?.getString("featureName") ?: "Feature"
                    SafeFallbackScreen(
                        featureName = featureName,
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) },
                        onAlertCaregiver = { alertCaregiver() }
                    )
                }
                composable(Routes.EmergencyCall.route) {
                    EmergencyCallScreen(
                        numbers = appState.settings.emergencyNumbers,
                        onCall = { number ->
                            uiScope.launch {
                                val launched = homeViewModel.triggerDirectEmergencyCall(number)
                                if (!launched) {
                                    navController.navigate(Routes.SafeFallback.createRoute("Emergency Call"))
                                }
                            }
                        },
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) },
                    )
                }
                composable(Routes.HealthInfo.route) {
                    HealthInfoScreen(
                        healthInfo = appState.settings.healthInfo,
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) },
                    )
                }
                composable(Routes.AppList.route) {
                    AppListScreen(
                        query = appListState.query,
                        apps = appListState.apps,
                        emptyTitle = appListState.emptyTitle,
                        emptyBody = appListState.emptyBody,
                        onQueryChange = appListViewModel::updateQuery,
                        onAppClick = appListViewModel::launchApp,
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) },
                    )
                }
                composable(Routes.CaregiverTools.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        CaregiverToolsScreen(
                            protectionEnabled = caregiverState.settings.caregiverProtectionEnabled,
                            layoutLocked = caregiverState.settings.layoutLocked,
                            hasPinConfigured = caregiverState.settings.pinHashHex != null && caregiverState.settings.pinSaltHex != null,
                            allAppsVisible = caregiverState.allAppsVisible,
                            currentPageCount = caregiverViewModel.effectivePageCount(),
                            showBatteryInfo = caregiverState.settings.showBatteryInfo,
                            skinConfig = caregiverState.settings.skinConfig,
                            favoriteContactCount = caregiverViewModel.contactTiles().size,
                            allowedAppCount = caregiverViewModel.assignedAppPackages().size,
                            hiddenAppCount = caregiverState.hiddenPackages.size,
                            healthInfoConfigured = caregiverState.settings.healthInfo.hasAnyValue(),
                            emergencyPhoneNumber = caregiverState.settings.emergencyPhoneNumber,
                            sosNumberCount = caregiverState.settings.sosNumbers.size,
                            easyUiLockEnabled = caregiverState.settings.easyUiLockEnabled,
                            easyUiLockTimeoutSeconds = caregiverState.settings.easyUiLockTimeoutSeconds,
                            onSetupPin = { navController.navigate(Routes.PinSetup.route) },
                            onChangePin = {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.CHANGE_PIN))
                            },
                            onToggleProtection = {
                            if (caregiverState.settings.pinHashHex == null) {
                                navController.navigate(caregiverViewModel.requestCaregiverAccess(ProtectedAction.TOGGLE_PROTECTION))
                            } else {
                                caregiverViewModel.toggleProtectionEnabled()
                            }
                            },

                            onToggleLayoutLock = { caregiverViewModel.toggleLayoutLock() },
                            onToggleAllAppsVisible = { caregiverViewModel.setAllAppsVisible(it) },
                            onToggleBatteryInfo = caregiverViewModel::setBatteryInfoVisible,
                            onOpenLayoutPages = {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_LAYOUT_PAGES))
                            },
                            onOpenReadabilityPreset = {
                                navController.navigate(Routes.ReadabilityPreset.route)
                            },
                            onOpenThemeSelection = {
                                navController.navigate(Routes.ThemeSelection.route)
                            },
                            onOpenAllowedApps = {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_ALLOWED_APPS))
                            },
                            onManageFavoriteContacts = {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_FAVORITE_CONTACTS))
                            },
                            onOpenEmergencySettings = {
                                navController.navigate(Routes.EmergencySettings.route)
                            },
                            onOpenHealthInfo = {
                                navController.navigate(Routes.HealthInfoEditor.route)
                            },
                            onOpenBackupRestore = {
                                navController.navigate(Routes.BackupRestore.route)
                            },
                            onOpenHiddenApps = {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_HIDDEN_APPS))
                            },
                            onOpenGuardianSettings = {
                                navController.navigate(Routes.GuardianSettings.route)
                            },
                            onOpenLinkedDevices = {
                                navController.navigate(Routes.LinkedDevices.route)
                            },
                            onShareMyStatus = {
                                val packet = com.easyui.core.domain.model.RemoteStatusPacket(
                                    deviceName = android.os.Build.MODEL,
                                    healthState = homeState.healthState,
                                    setupCompleteness = caregiverState.setupCompleteness
                                )
                                val link = remoteLinkViewModel.generateShareLink(packet)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "EasyUI Remote Status for ${packet.deviceName}:\n$link")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Status Link"))
                            },
                            onFinishSetup = {
                                caregiverViewModel.endCaregiverSession()
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.CaregiverTools.route) { inclusive = true }
                                }
                            },
                            onResetLauncher = {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.RESET_LAUNCHER))
                            },
                            onRedoGuidedSetup = {
                                guidedSetupViewModel.setStep(1)
                                navController.navigate(Routes.GuidedSetup.route)
                            },
                            setupCompleteness = caregiverState.setupCompleteness
                        )
                    }
                }
                composable(Routes.LinkedDevices.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        LinkedDevicesScreen(
                            devices = remoteLinkDevices,
                            onViewDevice = { device ->
                                navController.navigate(Routes.RemoteDeviceDetail.createRoute(device.id))
                            },
                            onRemoveDevice = { remoteLinkViewModel.removeDevice(it) },
                            onDone = { navController.popBackStack() }
                        )
                    }
                }
                composable(
                    route = Routes.RemoteDeviceDetail.route,
                    arguments = listOf(androidx.navigation.navArgument("deviceId") { type = androidx.navigation.NavType.StringType })
                ) { backStackEntry ->
                    val deviceId = backStackEntry.arguments?.getString("deviceId")
                    val device = remoteLinkDevices.find { it.id == deviceId }
                    if (device != null) {
                        RemoteDeviceDetailScreen(
                            device = device,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(Routes.GuardianSettings.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        GuardianSettingsScreen(
                            settings = caregiverState.settings,
                            onUpdateBatteryLowCheck = {
                                uiScope.launch { container.launcherSettingsRepository.updateBatteryLowCheckEnabled(it) }
                            },
                            onUpdateBatteryLowThreshold = {
                                uiScope.launch { container.launcherSettingsRepository.updateBatteryLowThreshold(it) }
                            },
                            onUpdateBatteryCriticalThreshold = {
                                uiScope.launch { container.launcherSettingsRepository.updateBatteryCriticalThreshold(it) }
                            },
                            onUpdateInternetCheck = {
                                uiScope.launch { container.launcherSettingsRepository.updateInternetCheckEnabled(it) }
                            },
                            onUpdateNoInternetDelay = {
                                uiScope.launch { container.launcherSettingsRepository.updateNoInternetDelayMinutes(it) }
                            },
                            onUpdateDefaultLauncherCheck = {
                                uiScope.launch { container.launcherSettingsRepository.updateDefaultLauncherCheckEnabled(it) }
                            },
                            onUpdateEmergencyContactCheck = {
                                uiScope.launch { container.launcherSettingsRepository.updateEmergencyContactCheckEnabled(it) }
                            },
                            onUpdateLayoutLockCheck = {
                                uiScope.launch { container.launcherSettingsRepository.updateLayoutLockCheckEnabled(it) }
                            },
                            onUpdatePermissionCheck = {
                                uiScope.launch { container.launcherSettingsRepository.updatePermissionCheckEnabled(it) }
                            },
                            onDone = { navController.popBackStack() }
                        )
                    }
                }
                composable(Routes.LayoutPages.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        LayoutPagesScreen(
                            skinConfig = caregiverState.settings.skinConfig,
                            onOpenAllowedApps = {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_ALLOWED_APPS))
                            },
                            onSelectLayoutMode = caregiverViewModel::updateSkinLayoutMode,
                            onOpenThemeSelection = { navController.navigate(Routes.ThemeSelection.route) },
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                            onFinishSetup = {
                                caregiverViewModel.endCaregiverSession()
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.CaregiverTools.route) { inclusive = true }
                                }
                            },
                        )
                    }
                }
                composable(Routes.ReadabilityPreset.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        com.easyui.feature.caregiver.ReadabilityPresetScreen(
                            currentPreset = caregiverState.settings.skinConfig.readabilityPreset,
                            onPresetSelected = caregiverViewModel::updateHomeReadabilityPreset,
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                            onFinishSetup = {
                                caregiverViewModel.endCaregiverSession()
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.CaregiverTools.route) { inclusive = true }
                                }
                            },
                        )
                    }
                }
                composable(Routes.ThemeSelection.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                            Column(
                                modifier = androidx.compose.ui.Modifier
                                    .fillMaxSize()
                                    .padding(EasyUiSpacing.lg),
                                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
                            ) {
                                Text("Visual Theme", style = MaterialTheme.typography.headlineLarge)
                                Text(
                                    "Pick the senior home look. High contrast stays separate from the regular light, dark, and auto themes.",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                ThemeSelector(
                                    visualTheme = caregiverState.settings.skinConfig.visualTheme,
                                    accessibilityMode = caregiverState.settings.skinConfig.accessibilityMode,
                                    onThemeSelected = { theme, mode ->
                                        caregiverViewModel.updateSkinConfig(theme, mode)
                                    },
                                )
                                OutlinedButton(
                                    onClick = { navController.popBackStack() },
                                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                                ) {
                                    Text("Back")
                                }
                            }
                        }
                    }
                }
                composable(Routes.PinSetup.route) {
                    val hasPin = caregiverState.settings.pinHashHex != null && caregiverState.settings.pinSaltHex != null
                    val content: @Composable () -> Unit = {
                        PinEntryScreen(
                            title = if (caregiverState.settings.pinHashHex == null) "Set Caregiver PIN" else "Change Caregiver PIN",
                            description = "This PIN is a local barrier against accidental changes. It does not lock Android itself.",
                            pin = caregiverState.pinInput,
                            confirmPin = caregiverState.confirmPinInput,
                            errorMessage = caregiverState.pinError,
                            submitLabel = "Save PIN",
                            onPinChange = caregiverViewModel::updatePinInput,
                            onConfirmPinChange = caregiverViewModel::updateConfirmPinInput,
                            onSubmit = {
                                val destination = caregiverViewModel.submitPinSetup()
                                if (destination != null) {
                                    navController.navigate(destination) {
                                        popUpTo(Routes.PinSetup.route) { inclusive = true }
                                    }
                                }
                            },
                        )
                    }
                    if (!hasPin) {
                        content()
                    } else {
                        RequireCaregiverSession(
                            caregiverSessionActive = caregiverState.caregiverSessionActive,
                            caregiverViewModel = caregiverViewModel,
                            navController = navController,
                            content = content
                        )
                    }
                }
                composable(Routes.PinVerify.route) {
                    PinEntryScreen(
                        title = "Enter Caregiver PIN",
                        description = "Open caregiver settings with the local caregiver PIN.",
                        pin = caregiverState.pinInput,
                        confirmPin = null,
                        errorMessage = caregiverState.pinError,
                        submitLabel = "Verify",
                        onPinChange = caregiverViewModel::updatePinInput,
                        onConfirmPinChange = null,
                        onSubmit = {
                            val destination = caregiverViewModel.completePinVerification()
                            if (destination != null) {
                                navController.navigate(destination) {
                                    popUpTo(Routes.PinVerify.route) { inclusive = true }
                                }
                            }
                        },
                    )
                }
                composable(Routes.AllowedApps.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        AllowedAppsScreen(
                            pageCount = caregiverViewModel.effectivePageCount(),
                            pages = caregiverViewModel.homePages(),
                            installedApps = caregiverViewModel.installedAppsForAllowedApps(),
                            assignedAppPackages = caregiverViewModel.assignedAppPackages(),
                            onAssignApp = caregiverViewModel::assignAllowedApp,
                            onRemoveApp = caregiverViewModel::removeAllowedApp,
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                            onFinishSetup = {
                                caregiverViewModel.endCaregiverSession()
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.CaregiverTools.route) { inclusive = true }
                                }
                            },
                        )
                    }
                }
                composable(Routes.ManageContacts.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        FavoriteContactsScreen(
                            tiles = caregiverViewModel.contactTiles(),
                            onMoveUp = caregiverViewModel::moveTileUp,
                            onMoveDown = caregiverViewModel::moveTileDown,
                            onEdit = caregiverViewModel::saveContactTile,
                            onRemove = caregiverViewModel::removeTile,
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                            onFinishSetup = {
                                caregiverViewModel.endCaregiverSession()
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.CaregiverTools.route) { inclusive = true }
                                }
                            },
                        )
                    }
                }
                composable(Routes.ResetLauncher.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        ResetLauncherScreen(
                            onConfirm = {
                                caregiverViewModel.resetLauncher()
                                navController.popBackStack(Routes.CaregiverTools.route, false)
                            },
                            onCancel = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        )
                    }
                }
                composable(Routes.EmergencySettings.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        EmergencySettingsScreen(
                            currentEmergencyNumber = caregiverState.settings.emergencyPhoneNumber,
                            emergencyNumbers = caregiverState.settings.emergencyNumbers,
                            sosNumbers = caregiverState.settings.sosNumbers,
                            easyUiLockEnabled = caregiverState.settings.easyUiLockEnabled,
                            easyUiLockTimeoutSeconds = caregiverState.settings.easyUiLockTimeoutSeconds,
                            onSave = { number ->
                                caregiverViewModel.updateEmergencyNumber(number)
                                caregiverViewModel.updateEmergencyNumbers(
                                    listOf(
                                        com.easyui.core.domain.model.EmergencyNumber("Ambulance", number), // Assuming number is intended for emergency list
                                    )
                                )
                            },
                            onSaveEmergencyNumbers = caregiverViewModel::updateEmergencyNumbers,
                            onSaveSosNumbers = caregiverViewModel::updateSosNumbers,
                            onToggleEasyUiLock = caregiverViewModel::setEasyUiLockEnabled,
                            onSaveEasyUiLockTimeout = caregiverViewModel::updateEasyUiLockTimeoutSeconds,
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        )
                    }
                }
                composable(Routes.HealthInfoEditor.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        HealthInfoEditorScreen(
                            healthInfo = caregiverState.settings.healthInfo,
                            onSave = caregiverViewModel::updateHealthInfo,
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        )
                    }
                }
                composable(Routes.ManageHiddenApps.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        HiddenAppsScreen(
                            installedApps = caregiverViewModel.installedAppsForAllowedApps(),
                            hiddenPackages = caregiverState.hiddenPackages,
                            onToggleHidden = caregiverViewModel::toggleAppHidden,
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) }
                        )
                    }
                }
                composable(Routes.BackupRestore.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
                        val backupState by backupViewModel.state.collectAsState()
                        val context = androidx.compose.ui.platform.LocalContext.current

                        LaunchedEffect(backupViewModel) {
                            backupViewModel.messages.collect {
                                snackbarHostState.showSnackbar(it)
                            }
                        }

                        val importFilePicker = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.OpenDocument(),
                        ) { uri: Uri? ->
                            if (uri != null) {
                                backupViewModel.loadImportFromUri(context, uri)
                            }
                        }

                        BackupRestoreScreen(
                            isExporting = backupState.isExporting,
                            isImporting = backupState.isImporting,
                            lastResult = backupState.lastResult,
                            pendingImportConfirmation = backupState.pendingImportJson != null,
                            onExport = {
                                backupViewModel.exportBackup { json, filename ->
                                    // Share the JSON via Android's share sheet so the user can
                                    // save it to Files, Drive, email it, etc.
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/json"
                                        putExtra(Intent.EXTRA_TEXT, json)
                                        putExtra(Intent.EXTRA_SUBJECT, filename)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(shareIntent, "Save EasyUI Backup").apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        },
                                    )
                                }
                            },
                            onPickImportFile = {
                                importFilePicker.launch(arrayOf("application/json", "text/plain", "*/*"))
                            },
                            onConfirmImport = { backupViewModel.confirmImport() },
                            onCancelImport = { backupViewModel.cancelImport() },
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        )
                    }
                }
            }
            if (easyUiLocked && appState.settings.easyUiLockEnabled) {
                EasyUiLockOverlay(
                    pin = lockPinInput,
                    error = lockPinError,
                    onPinChange = {
                        lockPinInput = it
                        lockPinError = null
                    },
                    onUnlock = {
                        val credential = appState.settings.pinCredentialOrNull()
                        if (credential == null) {
                            lockPinError = "Set a caregiver PIN first."
                            return@EasyUiLockOverlay
                        }
                        if (PinHasher.verify(lockPinInput, credential)) {
                            easyUiLocked = false
                            lockPinInput = ""
                            lockPinError = null
                            lastInteractionAt = android.os.SystemClock.elapsedRealtime()
                        } else {
                            lockPinError = "Incorrect PIN."
                        }
                    },
                )
            }
        }
        }
    }
}

@Composable
private fun RequireCaregiverSession(
    caregiverSessionActive: Boolean,
    caregiverViewModel: CaregiverViewModel,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    var isChecking by remember { mutableStateOf(!caregiverSessionActive) }

    LaunchedEffect(caregiverSessionActive) {
        if (!caregiverSessionActive) {
            isChecking = true
            kotlinx.coroutines.delay(200) // wait for StateFlow combine to settle
            if (!caregiverViewModel.state.value.caregiverSessionActive) {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Home.route) { inclusive = false }
                }
            }
        } else {
            isChecking = false
        }
    }
    
    if (isChecking) {
        Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {}
        return
    }
    
    // Session timeout monitoring
    var showTimeoutWarning by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        while (caregiverSessionActive) {
            val timeoutState = caregiverViewModel.checkSessionTimeout()
            when (timeoutState) {
                CaregiverViewModel.SessionTimeoutState.WarningActive -> {
                    if (!showTimeoutWarning) {
                        showTimeoutWarning = true
                    }
                }
                CaregiverViewModel.SessionTimeoutState.TimedOut -> {
                    caregiverViewModel.endCaregiverSession()
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = false }
                    }
                    return@LaunchedEffect
                }
                else -> {
                    if (showTimeoutWarning) {
                        showTimeoutWarning = false
                    }
                }
            }
            kotlinx.coroutines.delay(1000)
        }
    }
    
    if (showTimeoutWarning) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Session Timeout Warning") },
            text = { Text("Your caregiver session will end in 2 minutes due to inactivity. Please interact with the screen to continue.") },
            confirmButton = {
                Button(
                    onClick = {
                        caregiverViewModel.updateSessionActivity()
                        showTimeoutWarning = false
                    },
                ) {
                    Text("OK")
                }
            },
        )
    }
    
    content()
}

@Composable
private fun LoadingScreen(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "EasyUI",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Box(modifier = androidx.compose.ui.Modifier.padding(top = EasyUiSpacing.lg)) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun EasyUiLockOverlay(
    pin: String,
    error: String?,
    onPinChange: (String) -> Unit,
    onUnlock: () -> Unit,
) {
    Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.97f),
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("EasyUI Locked", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Enter caregiver PIN to continue.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = androidx.compose.ui.Modifier.padding(top = EasyUiSpacing.sm),
            )
            androidx.compose.material3.OutlinedTextField(
                value = pin,
                onValueChange = onPinChange,
                label = { Text("Caregiver PIN") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword,
                ),
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(top = EasyUiSpacing.md),
            )
            if (!error.isNullOrBlank()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = androidx.compose.ui.Modifier.padding(top = EasyUiSpacing.xs),
                )
            }
            androidx.compose.material3.Button(
                onClick = onUnlock,
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(top = EasyUiSpacing.md),
            ) {
                Text("Unlock")
            }
        }
    }
}

private fun com.easyui.core.domain.model.LauncherSettings.pinCredentialOrNull(): PinCredential? {
    val salt = pinSaltHex ?: return null
    val hash = pinHashHex ?: return null
    return PinCredential(saltHex = salt, hashHex = hash)
}
