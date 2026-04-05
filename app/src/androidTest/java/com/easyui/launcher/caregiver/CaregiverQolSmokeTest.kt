package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import com.easyui.launcher.assertPresent
import com.easyui.core.domain.model.SkinConfig
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
                    skinConfig = SkinConfig(),
                    favoriteContactCount = 2,
                    allowedAppCount = 4,
                    hiddenAppCount = 1,
                    healthInfoConfigured = true,
                    emergencyPhoneNumber = "911",
                    sosNumberCount = 2,
                    easyUiLockEnabled = true,
                    easyUiLockTimeoutSeconds = 60,
                    onSetupPin = {},
                    onChangePin = {},
                    onToggleProtection = {},
                    onToggleLayoutLock = {},
                    onToggleBatteryInfo = {},
                    onOpenLayoutPages = {},
                    onOpenAllowedApps = {},
                    onManageFavoriteContacts = {},
                    onOpenEmergencySettings = {},
                    onOpenHealthInfo = {},
                    onOpenBackupRestore = {},
                    onOpenHiddenApps = {},
                    onFinishSetup = {},
                    onResetLauncher = {},
                )
            }
        }

        composeRule.onNodeWithTag("caregiver_tools_screen").assertPresent()
        composeRule.onNodeWithText("Caregiver").assertPresent()
        composeRule.onNodeWithText("Home Layout").assertPresent()
        composeRule.onNodeWithText("Allowed Apps").assertPresent()
        composeRule.onNodeWithText("Contacts & Emergency").assertPresent()
        composeRule.onNodeWithText("Security & Lock").assertPresent()
        composeRule.onAllNodesWithText("Protected").assertCountEquals(2)
    }

    @Test
    fun layoutPagesScreenShowsPageControls() {
        composeRule.setContent {
            EasyUiTheme {
                LayoutPagesScreen(
                    currentPageCount = 2,
                    skinConfig = SkinConfig(),
                    onIncreasePageCount = {},
                    onDecreasePageCount = {},
                    onSelectLayoutMode = {},
                    onSelectVisualTheme = {},
                    onSelectAccessibilityMode = {},
                    onDone = {},
                    onFinishSetup = {},
                )
            }
        }

        composeRule.onNodeWithTag("layout_pages_screen").assertPresent()
        composeRule.onNodeWithText("Add Page").assertIsDisplayed()
        composeRule.onNodeWithText("Use Fewer").assertIsDisplayed()
        composeRule.onNodeWithTag("layout_pages_screen").performScrollToNode(hasText("Visual Theme"))
        composeRule.onNodeWithText("Visual Theme").assertIsDisplayed()
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
                            HomeTile("messages", 1, "Messages", HomeTileType.ACTION, action = HomeTileAction.OPEN_MESSAGES),
                            HomeTile("app-camera", 2, "Camera", HomeTileType.APP, packageName = "com.camera"),
                            null,
                            null,
                            HomeTile("emergency", 5, "Emergency", HomeTileType.ACTION, action = HomeTileAction.EMERGENCY),
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

        composeRule.onNodeWithTag("allowed_apps_screen").assertPresent()
        composeRule.onNodeWithText("Home Apps").assertPresent()
        composeRule.onNodeWithText("Installed Apps").assertPresent()
        composeRule.onNodeWithText("Phone").assertPresent()
        composeRule.onNodeWithText("Messages").assertPresent()
        composeRule.onAllNodesWithText("Camera").assertCountEquals(2)
        composeRule.onNodeWithText("Already on home").assertPresent()
    }
}
