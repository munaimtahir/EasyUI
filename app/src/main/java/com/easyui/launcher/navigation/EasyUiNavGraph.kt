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
import com.easyui.feature.home.HealthInfoScreen
import com.easyui.feature.home.EmergencyCallScreen
import com.easyui.feature.home.HomeScreen
import com.easyui.feature.home.PhoneContactsScreen
import com.easyui.feature.onboarding.CaregiverHelpScreen
import com.easyui.feature.onboarding.DefaultLauncherGuidanceScreen
import com.easyui.feature.onboarding.IntroScreen
import com.easyui.feature.onboarding.WelcomeScreen
import com.easyui.feature.onboarding.LauncherActivationScreen
import com.easyui.feature.onboarding.PermissionsExplanationScreen
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
import com.easyui.launcher.di.AppContainer
import com.easyui.launcher.ui.AppViewModelFactory
import com.easyui.core.domain.model.ProtectedAction
import com.easyui.core.domain.model.PinCredential
import com.easyui.core.domain.security.PinHasher
import com.easyui.core.ui.theme.EasyUiSpacing
import com.easyui.core.ui.theme.EasyUiTheme
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlinx.coroutines.launch

@Composable
fun EasyUiNavGraph(
    container: AppContainer,
    navController: NavHostController = rememberNavController(),
) {
    val factory = remember(container) { AppViewModelFactory(container) }
    val appViewModel: AppViewModel = viewModel(factory = factory)
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val appListViewModel: AppListViewModel = viewModel(factory = factory)
    val caregiverViewModel: CaregiverViewModel = viewModel(factory = factory)
    val backupViewModel: BackupViewModel = viewModel(factory = factory)
    val guidedSetupViewModel: GuidedSetupViewModel = viewModel(factory = factory)
    val appState by appViewModel.state.collectAsState()
    val homeState by homeViewModel.state.collectAsState()
    val appListState by appListViewModel.state.collectAsState()
    val caregiverState by caregiverViewModel.state.collectAsState()
    val guidedSetupState by guidedSetupViewModel.state.collectAsState()
    val uiScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    var launcherStatusVersion by remember { mutableIntStateOf(0) }
    var lastInteractionAt by remember { mutableLongStateOf(android.os.SystemClock.elapsedRealtime()) }
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
            if (event == Lifecycle.Event.ON_RESUME && appState.settings.easyUiLockEnabled) {
                easyUiLocked = true
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
                    
                    when (guidedSetupState.guidedSetupStep) {
                        1 -> LauncherActivationScreen(
                            isDefaultLauncher = guidedSetupState.isDefaultLauncher,
                            onOpenSettings = { guidedSetupViewModel.openLauncherSettings() },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        2 -> WelcomeScreen(onNext = { guidedSetupViewModel.nextStep() })
                        3 -> ProtectionOptionsScreen(
                            current = guidedSetupState.setupProtectionLevel,
                            onSelect = { level ->
                                guidedSetupViewModel.updateSetupProtectionLevel(level)
                                guidedSetupViewModel.updateLayoutLocked(level == com.easyui.core.domain.model.SetupProtectionLevel.RECOMMENDED)
                            },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() },
                        )
                        4 -> SecuritySetupScreen(
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
                        5 -> ThemePickerScreen(
                            visualTheme = appState.settings.skinConfig.visualTheme,
                            accessibilityMode = appState.settings.skinConfig.accessibilityMode,
                            onSelectVisualTheme = guidedSetupViewModel::updateVisualTheme,
                            onSelectAccessibilityMode = guidedSetupViewModel::updateAccessibilityMode,
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() },
                        )
                        6 -> ReadabilityPresetScreen(
                            currentPreset = guidedSetupState.homeReadabilityPreset,
                            onPresetSelected = { guidedSetupViewModel.updateReadabilityPreset(it) },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        7 -> HomeLayoutSetupScreen(
                            homePageCount = guidedSetupState.homePageCount,
                            onPageCountChange = { caregiverViewModel.updateHomePageCount(it) },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        8 -> AllowedAppsSetupScreen(
                            pageCount = caregiverViewModel.effectivePageCount(),
                            pages = caregiverViewModel.homePages(),
                            installedApps = caregiverViewModel.installedAppsForAllowedApps(),
                            assignedAppPackages = caregiverViewModel.assignedAppPackages(),
                            onAssignApp = caregiverViewModel::assignAllowedApp,
                            onRemoveApp = caregiverViewModel::removeAllowedApp,
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        9 -> ContactsSetupScreen(
                            tiles = caregiverViewModel.contactTiles(),
                            onMoveUp = caregiverViewModel::moveTileUp,
                            onMoveDown = caregiverViewModel::moveTileDown,
                            onEdit = caregiverViewModel::saveContactTile,
                            onRemove = caregiverViewModel::removeTile,
                            emergencyMode = guidedSetupState.emergencyMode,
                            onEmergencyModeChange = { guidedSetupViewModel.updateEmergencyMode(it) },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        10 -> PermissionsExplanationScreen(
                            enabledPermissions = guidedSetupState.setupOptionalPermissions,
                            onSetEnabled = guidedSetupViewModel::setOptionalPermission,
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() },
                        )
                        11 -> DeviceSupportScreen(
                            showBattery = caregiverState.settings.showBatteryInfo,
                            onToggleBattery = { caregiverViewModel.setBatteryInfoVisible(it) },
                            onNext = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() }
                        )
                        12 -> ReviewConfirmScreen(
                            onConfirm = { guidedSetupViewModel.nextStep() },
                            onBack = { guidedSetupViewModel.previousStep() },
                            readability = guidedSetupState.homeReadabilityPreset.name.replace("_", " "),
                            pageCount = caregiverViewModel.effectivePageCount(),
                            allowedAppCount = caregiverViewModel.assignedAppPackages().size,
                            emergencyMode = guidedSetupState.emergencyMode,
                            layoutLocked = guidedSetupState.layoutLocked,
                            hasPin = guidedSetupState.hasPinConfigured
                        )
                        13 -> CompletionScreen(
                            onFinish = {
                                guidedSetupViewModel.completeSetup()
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.GuidedSetup.route) { inclusive = true }
                                }
                            }
                        )
                        else -> WelcomeScreen(onNext = { guidedSetupViewModel.setStep(1) })
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
                        onTileClick = { tileId ->
                            homeViewModel.onTileClick(
                                tileId = tileId,
                                onOpenPhoneContacts = { navController.navigate(Routes.PhoneContacts.route) },
                                onOpenEmergency = { navController.navigate(Routes.EmergencyCall.route) },
                            )
                        },
                        onOpenAppList = { navController.navigate(Routes.AppList.route) },
                        onStatusBarLongPress = {
                            homeViewModel.onTopBarLongPressCaregiverAccess(openCaregiverAccess)
                        },
                        onClockTapped = {
                            homeViewModel.onClockTappedCaregiverAccess(openCaregiverAccess)
                        },
                    )
                }
                composable(Routes.PhoneContacts.route) {
                    PhoneContactsScreen(
                        contacts = caregiverViewModel.contactTiles().take(10),
                        onCall = { number ->
                            uiScope.launch {
                                val launched = container.emergencyActionHandler.callPhone(number)
                                if (!launched) {
                                    snackbarHostState.showSnackbar("Calling is not available on this device.")
                                }
                            }
                        },
                        onBackHome = { navController.popBackStack(Routes.Home.route, false) },
                    )
                }
                composable(Routes.EmergencyCall.route) {
                    EmergencyCallScreen(
                        numbers = appState.settings.emergencyNumbers,
                        onCall = { number ->
                            uiScope.launch {
                                val launched = homeViewModel.triggerDirectEmergencyCall(number)
                                if (!launched) {
                                    snackbarHostState.showSnackbar("Emergency calling is not available on this device.")
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
                                    navController.navigate(Routes.PinSetup.route)
                                } else {
                                    caregiverViewModel.toggleProtectionEnabled()
                                }
                            },
                            onToggleLayoutLock = { caregiverViewModel.toggleLayoutLock() },
                            onToggleBatteryInfo = caregiverViewModel::setBatteryInfoVisible,
                            onOpenLayoutPages = {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_LAYOUT_PAGES))
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
                            }
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
                            currentPageCount = caregiverViewModel.effectivePageCount(),
                            skinConfig = caregiverState.settings.skinConfig,
                            onIncreasePageCount = {
                                caregiverViewModel.updateHomePageCount(caregiverViewModel.effectivePageCount() + 1)
                            },
                            onDecreasePageCount = {
                                caregiverViewModel.updateHomePageCount(caregiverViewModel.effectivePageCount() - 1)
                            },
                            onSelectLayoutMode = caregiverViewModel::updateSkinLayoutMode,
                            onSelectVisualTheme = caregiverViewModel::updateSkinVisualTheme,
                            onSelectAccessibilityMode = caregiverViewModel::updateSkinAccessibilityMode,
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
                composable(Routes.PinSetup.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        caregiverViewModel = caregiverViewModel,
                        navController = navController,
                    ) {
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
                                if (caregiverViewModel.submitPinSetup()) {
                                    navController.popBackStack(Routes.CaregiverTools.route, false)
                                }
                            },
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
