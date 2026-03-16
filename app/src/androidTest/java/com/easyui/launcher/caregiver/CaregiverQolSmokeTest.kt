package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.caregiver.AllowedAppsScreen
import com.easyui.feature.caregiver.CaregiverToolsScreen
import com.easyui.feature.caregiver.LayoutPagesScreen
import org.junit.Rule
import org.junit.Test

class CaregiverQolSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun caregiverSettingsShowsNewSections() {
        composeRule.setContent {
            EasyUiTheme {
                CaregiverToolsScreen(
                    protectionEnabled = true,
                    layoutLocked = true,
                    hasPinConfigured = true,
                    currentPageCount = 2,
                    showBatteryInfo = true,
                    homeReadabilityPresetName = "STANDARD",
                    verySimpleModeEnabled = false,
                    favoriteContactCount = 2,
                    allowedAppCount = 4,
                    onSetupPin = {},
                    onChangePin = {},
                    onToggleProtection = {},
                    onToggleLayoutLock = {},
                    onToggleBatteryInfo = {},
                    onOpenLayoutPages = {},
                    onOpenAllowedApps = {},
                    onManageFavoriteContacts = {},
                    onFinishSetup = {},
                    onResetLauncher = {},
                )
            }
        }

        composeRule.onNodeWithTag("caregiver_tools_screen").assertIsDisplayed()
        composeRule.onNodeWithText("Layout / Pages").assertIsDisplayed()
        composeRule.onNodeWithText("Allowed Apps").assertIsDisplayed()
        composeRule.onNodeWithText("Call Shortcuts").assertIsDisplayed()
        composeRule.onNodeWithText("Lock / Protection").assertIsDisplayed()
        composeRule.onNodeWithText("Battery Display").assertIsDisplayed()
    }

    @Test
    fun layoutPagesScreenShowsPageControls() {
        composeRule.setContent {
            EasyUiTheme {
                LayoutPagesScreen(
                    currentPageCount = 2,
                    currentPresetName = HomeReadabilityPreset.STANDARD.name,
                    verySimpleModeEnabled = false,
                    onIncreasePageCount = {},
                    onDecreasePageCount = {},
                    onSelectPreset = {},
                    onToggleVerySimpleMode = {},
                    onDone = {},
                    onFinishSetup = {},
                )
            }
        }

        composeRule.onNodeWithTag("layout_pages_screen").assertIsDisplayed()
        composeRule.onNodeWithText("Add Page").assertIsDisplayed()
        composeRule.onNodeWithText("Use Fewer").assertIsDisplayed()
        composeRule.onNodeWithText("Very simple home mode").assertIsDisplayed()
    }

    @Test
    fun allowedAppsScreenKeepsFixedAndAllowedAppsSeparate() {
        composeRule.setContent {
            EasyUiTheme {
                AllowedAppsScreen(
                    pageCount = 2,
                    pages = listOf(
                        listOf(
                            HomeTile("phone", 0, "Phone", HomeTileType.ACTION, action = HomeTileAction.OPEN_DIALER),
                            HomeTile("apps-list", 1, "All Apps", HomeTileType.ACTION, action = HomeTileAction.OPEN_APP_LIST),
                            HomeTile("app-camera", 2, "Camera", HomeTileType.APP, packageName = "com.camera"),
                            null,
                            null,
                            HomeTile("flashlight", 5, "Flashlight", HomeTileType.ACTION, action = HomeTileAction.FLASHLIGHT),
                        ),
                        listOf(null, null, null, null, null, null),
                    ),
                    installedApps = listOf(
                        InstalledApp("com.camera", "CameraActivity", "Camera"),
                        InstalledApp("com.maps", "MapsActivity", "Maps"),
                    ),
                    assignedAppPackages = setOf("com.camera"),
                    onAssignApp = { _, _ -> },
                    onRemoveApp = {},
                    onDone = {},
                    onFinishSetup = {},
                )
            }
        }

        composeRule.onNodeWithTag("allowed_apps_screen").assertIsDisplayed()
        composeRule.onNodeWithText("Allowed Apps").assertIsDisplayed()
        composeRule.onNodeWithText("Installed Apps").assertIsDisplayed()
        composeRule.onNodeWithText("Phone").assertIsDisplayed()
        composeRule.onNodeWithText("All Apps").assertIsDisplayed()
        composeRule.onNodeWithText("Maps").assertIsDisplayed()
    }
}
