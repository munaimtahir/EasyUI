package com.easyui.launcher.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.easyui.feature.apps.AppListScreen
import com.easyui.feature.caregiver.CaregiverToolsScreen
import com.easyui.feature.caregiver.EditLayoutScreen
import com.easyui.feature.caregiver.FavoriteContactsScreen
import com.easyui.feature.caregiver.HiddenAppsScreen
import com.easyui.feature.caregiver.HomeDisplayScreen
import com.easyui.feature.caregiver.PinEntryScreen
import com.easyui.feature.caregiver.ResetLauncherScreen
import com.easyui.feature.home.HomeScreen
import com.easyui.feature.onboarding.CaregiverHelpScreen
import com.easyui.feature.onboarding.DefaultLauncherGuidanceScreen
import com.easyui.feature.onboarding.IntroScreen
import com.easyui.launcher.app.AppListViewModel
import com.easyui.launcher.app.AppViewModel
import com.easyui.launcher.app.HomeViewModel
import com.easyui.launcher.app.caregiver.CaregiverViewModel
import com.easyui.launcher.di.AppContainer
import com.easyui.launcher.ui.AppViewModelFactory
import com.easyui.core.domain.model.ProtectedAction

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
            Text("Loading EasyUI…", modifier = androidx.compose.ui.Modifier.padding(innerPadding))
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
                        tiles = homeState.tiles,
                        readabilityPreset = homeState.readabilityPreset,
                        verySimpleModeEnabled = homeState.verySimpleModeEnabled,
                        fallbackTitle = homeState.fallbackTitle,
                        fallbackBody = homeState.fallbackBody,
                        onTileClick = { tileId ->
                            homeViewModel.onTileClick(tileId) {
                                navController.navigate(Routes.AppList.route)
                            }
                        },
                        onCaregiverToolsClick = { navController.navigate(Routes.CaregiverTools.route) },
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
                    CaregiverToolsScreen(
                        protectionEnabled = caregiverState.settings.caregiverProtectionEnabled,
                        layoutLocked = caregiverState.settings.layoutLocked,
                        hasPinConfigured = caregiverState.settings.pinHashHex != null && caregiverState.settings.pinSaltHex != null,
                        currentPresetName = caregiverState.settings.appVisibilityPreset,
                        homeReadabilityPresetName = caregiverState.settings.homeReadabilityPreset,
                        verySimpleModeEnabled = caregiverState.settings.verySimpleModeEnabled,
                        favoriteContactCount = caregiverViewModel.contactTiles().size,
                        onSetupPin = { navController.navigate(Routes.PinSetup.route) },
                        onChangePin = {
                            val destination = caregiverViewModel.beginProtectedAction(ProtectedAction.CHANGE_PIN)
                            navController.navigate(destination)
                        },
                        onToggleProtection = {
                            if (caregiverState.settings.pinHashHex == null) {
                                navController.navigate(Routes.PinSetup.route)
                            } else if (!caregiverState.settings.caregiverProtectionEnabled) {
                                caregiverViewModel.toggleProtectionEnabled()
                            } else {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.TOGGLE_PROTECTION))
                            }
                        },
                        onToggleLayoutLock = {
                            if (!caregiverState.settings.caregiverProtectionEnabled) {
                                caregiverViewModel.toggleLayoutLock()
                            } else {
                                navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.TOGGLE_LAYOUT_LOCK))
                            }
                        },
                        onEditHome = {
                            navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.ENTER_EDIT_MODE))
                        },
                        onHomeDisplay = {
                            navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_HOME_DISPLAY))
                        },
                        onManageFavoriteContacts = {
                            navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_FAVORITE_CONTACTS))
                        },
                        onManageHiddenApps = {
                            navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.MANAGE_APP_VISIBILITY))
                        },
                        onFinishSetup = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.CaregiverTools.route) { inclusive = false }
                            }
                        },
                        onResetLauncher = {
                            navController.navigate(caregiverViewModel.beginProtectedAction(ProtectedAction.RESET_LAUNCHER))
                        },
                    )
                }
                composable(Routes.HomeDisplay.route) {
                    HomeDisplayScreen(
                        currentPresetName = caregiverState.settings.homeReadabilityPreset,
                        verySimpleModeEnabled = caregiverState.settings.verySimpleModeEnabled,
                        onSelectPreset = caregiverViewModel::updateHomeReadabilityPreset,
                        onToggleVerySimpleMode = caregiverViewModel::setVerySimpleModeEnabled,
                        onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        onFinishSetup = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.CaregiverTools.route) { inclusive = false }
                            }
                        },
                    )
                }
                composable(Routes.PinSetup.route) {
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
                composable(Routes.PinVerify.route) {
                    PinEntryScreen(
                        title = "Enter Caregiver PIN",
                        description = "This change needs the caregiver PIN.",
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
                composable(Routes.EditLayout.route) {
                    EditLayoutScreen(
                        tiles = caregiverViewModel.editableTiles(),
                        availableApps = caregiverViewModel.availableAppsForLayout(),
                        onMoveUp = caregiverViewModel::moveTileUp,
                        onMoveDown = caregiverViewModel::moveTileDown,
                        onRemove = caregiverViewModel::removeTile,
                        onAdd = caregiverViewModel::addAppTile,
                        onManageFavoriteContacts = { navController.navigate(Routes.ManageContacts.route) },
                        onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        onFinishSetup = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.CaregiverTools.route) { inclusive = false }
                            }
                        },
                    )
                }
                composable(Routes.ManageContacts.route) {
                    FavoriteContactsScreen(
                        tiles = caregiverViewModel.contactTiles(),
                        onMoveUp = caregiverViewModel::moveTileUp,
                        onMoveDown = caregiverViewModel::moveTileDown,
                        onEdit = caregiverViewModel::saveContactTile,
                        onRemove = caregiverViewModel::removeTile,
                        onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        onFinishSetup = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.CaregiverTools.route) { inclusive = false }
                            }
                        },
                    )
                }
                composable(Routes.HiddenApps.route) {
                    HiddenAppsScreen(
                        apps = caregiverViewModel.visibleAppsForHiddenSettings(),
                        hiddenPackages = caregiverState.hiddenPackages,
                        currentPresetName = caregiverState.settings.appVisibilityPreset,
                        onApplyPreset = caregiverViewModel::applyVisibilityPreset,
                        onToggleHidden = caregiverViewModel::setHidden,
                        onDone = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                        onFinishSetup = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.CaregiverTools.route) { inclusive = false }
                            }
                        },
                    )
                }
                composable(Routes.ResetLauncher.route) {
                    ResetLauncherScreen(
                        onConfirm = {
                            caregiverViewModel.resetLauncher()
                            navController.popBackStack(Routes.CaregiverTools.route, false)
                        },
                        onCancel = { navController.popBackStack(Routes.CaregiverTools.route, false) },
                    )
                }
            }
        }
    }
}
