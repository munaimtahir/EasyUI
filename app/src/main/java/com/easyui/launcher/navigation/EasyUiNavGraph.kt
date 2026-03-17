package com.easyui.launcher.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.easyui.feature.caregiver.LayoutPagesScreen
import com.easyui.feature.caregiver.PinEntryScreen
import com.easyui.feature.caregiver.ResetLauncherScreen
import com.easyui.feature.home.HomeScreen
import com.easyui.feature.onboarding.CaregiverHelpScreen
import com.easyui.feature.onboarding.DefaultLauncherGuidanceScreen
import com.easyui.feature.onboarding.IntroScreen
import com.easyui.launcher.app.AppListViewModel
import com.easyui.launcher.app.AppViewModel
import com.easyui.launcher.app.HomeViewModel
import com.easyui.launcher.app.caregiver.BackupViewModel
import com.easyui.launcher.app.caregiver.CaregiverViewModel
import com.easyui.launcher.di.AppContainer
import com.easyui.launcher.ui.AppViewModelFactory
import com.easyui.core.domain.model.ProtectedAction
import com.easyui.core.ui.theme.EasyUiSpacing
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
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
    val appState by appViewModel.state.collectAsState()
    val homeState by homeViewModel.state.collectAsState()
    val appListState by appListViewModel.state.collectAsState()
    val caregiverState by caregiverViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var launcherStatusVersion by remember { mutableIntStateOf(0) }

    LaunchedEffect(homeViewModel) {
        homeViewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(appListViewModel) {
        appListViewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(caregiverViewModel) {
        caregiverViewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        if (!appState.settingsLoaded || !appState.starterLayoutReady) {
            LoadingScreen(modifier = androidx.compose.ui.Modifier.padding(innerPadding))
            return@Scaffold
        }
        val startDestination =
            if (appState.settings.onboardingComplete) Routes.Home.route else Routes.Intro.route
        androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable(Routes.Intro.route) {
                    IntroScreen(onContinue = { navController.navigate(Routes.LauncherGuidance.route) })
                }
                composable(Routes.LauncherGuidance.route) {
                    val isDefaultLauncher = remember(launcherStatusVersion) {
                        container.defaultLauncherManager.isDefaultLauncher()
                    }
                    DefaultLauncherGuidanceScreen(
                        isDefaultLauncher = isDefaultLauncher,
                        onOpenSettings = { container.defaultLauncherManager.openDefaultLauncherSettings() },
                        onRefreshStatus = { launcherStatusVersion += 1 },
                        onContinue = { navController.navigate(Routes.CaregiverHelp.route) },
                    )
                }
                composable(Routes.CaregiverHelp.route) {
                    CaregiverHelpScreen(
                        onContinue = {
                            appViewModel.completeOnboarding()
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Intro.route) { inclusive = true }
                            }
                        },
                    )
                }
                composable(Routes.Home.route) {
                    HomeScreen(
                        timeText = homeState.timeText,
                        dateText = homeState.dateText,
                        batterySummary = homeState.batterySummary,
                        pages = homeState.pages,
                        readabilityPreset = homeState.readabilityPreset,
                        verySimpleModeEnabled = homeState.verySimpleModeEnabled,
                        fallbackTitle = homeState.fallbackTitle,
                        fallbackBody = homeState.fallbackBody,
                        onTileClick = { tileId ->
                            homeViewModel.onTileClick(tileId) {
                                navController.navigate(Routes.AppList.route)
                            }
                        },
                        onCaregiverAccessRequested = {
                            navController.navigate(caregiverViewModel.requestCaregiverAccess())
                        },
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
                    )
                }
                composable(Routes.CaregiverTools.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        navController = navController,
                    ) {
                        CaregiverToolsScreen(
                            protectionEnabled = caregiverState.settings.caregiverProtectionEnabled,
                            layoutLocked = caregiverState.settings.layoutLocked,
                            hasPinConfigured = caregiverState.settings.pinHashHex != null && caregiverState.settings.pinSaltHex != null,
                            currentPageCount = caregiverViewModel.effectivePageCount(),
                            showBatteryInfo = caregiverState.settings.showBatteryInfo,
                            homeReadabilityPresetName = caregiverState.settings.homeReadabilityPreset,
                            verySimpleModeEnabled = caregiverState.settings.verySimpleModeEnabled,
                            favoriteContactCount = caregiverViewModel.contactTiles().size,
                            allowedAppCount = caregiverViewModel.assignedAppPackages().size,
                            hiddenAppCount = caregiverState.hiddenPackages.size,
                            emergencyPhoneNumber = caregiverState.settings.emergencyPhoneNumber,
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
                        )
                    }
                }
                composable(Routes.LayoutPages.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
                        navController = navController,
                    ) {
                        LayoutPagesScreen(
                            currentPageCount = caregiverViewModel.effectivePageCount(),
                            currentPresetName = caregiverState.settings.homeReadabilityPreset,
                            verySimpleModeEnabled = caregiverState.settings.verySimpleModeEnabled,
                            onIncreasePageCount = {
                                caregiverViewModel.updateHomePageCount(caregiverViewModel.effectivePageCount() + 1)
                            },
                            onDecreasePageCount = {
                                caregiverViewModel.updateHomePageCount(caregiverViewModel.effectivePageCount() - 1)
                            },
                            onSelectPreset = caregiverViewModel::updateHomeReadabilityPreset,
                            onToggleVerySimpleMode = caregiverViewModel::setVerySimpleModeEnabled,
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
                        navController = navController,
                    ) {
                        EmergencySettingsScreen(
                            currentEmergencyNumber = caregiverState.settings.emergencyPhoneNumber,
                            onSave = { number ->
                                caregiverViewModel.updateEmergencyNumber(number)
                            },
                            onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        )
                    }
                }
                composable(Routes.ManageHiddenApps.route) {
                    RequireCaregiverSession(
                        caregiverSessionActive = caregiverState.caregiverSessionActive,
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
                        navController = navController,
                    ) {
                        val backupState by backupViewModel.state.collectAsState()
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val scope = rememberCoroutineScope()

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
        }
    }
}

@Composable
private fun RequireCaregiverSession(
    caregiverSessionActive: Boolean,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    if (!caregiverSessionActive) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Home.route) { inclusive = false }
            }
        }
        Text("Returning home…")
        return
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
