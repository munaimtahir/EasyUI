package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.printToLog
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

import androidx.compose.material3.ExperimentalMaterial3Api

class CaregiverQolSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun caregiverSettingsShowsNewSections() {
        composeRule.setContent {
            EasyUiTheme {
                CaregiverToolsScreen(
                    protectionEnabled = true,
                    layoutLocked = true,
                    hasPinConfigured = true,
                    allAppsVisible = true,
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
                    onToggleAllAppsVisible = {},
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
                    onRedoGuidedSetup = {},
                )
            }
        }

        composeRule.onNodeWithTag("caregiver_tools_screen").assertPresent()
        composeRule.onNodeWithText("Caregiver Settings").assertPresent()
        
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Appearance & Layout"))
        composeRule.onNodeWithText("Appearance & Layout").assertPresent()
        
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Home Apps"))
        composeRule.onNodeWithText("Home Apps").assertPresent()
        
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Contacts & Emergency"))
        composeRule.onNodeWithText("Contacts & Emergency").assertPresent()
        
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Security & Protection"))
        composeRule.onNodeWithText("Security & Protection").assertPresent()
        
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Device & Backup"))
        composeRule.onNodeWithText("Device & Backup").assertPresent()
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
                    onSelectTheme = { _, _ -> },
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

    @OptIn(ExperimentalMaterial3Api::class)
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
        composeRule.onNodeWithText("Home Layout Preview").assertPresent()
        composeRule.onNodeWithText("Phone").assertPresent()
        composeRule.onNodeWithText("Messages").assertPresent()
        
        // Select an empty slot (Slot 3) to show the Installed Apps list in the BottomSheet
        composeRule.onNodeWithTag("slot_select_3").performScrollTo().performClick()
        composeRule.waitForIdle()
        
        // Wait up to 5 seconds for the bottom sheet to display "Placed"
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithText("Placed").fetchSemanticsNodes().isNotEmpty()
        }

        // "Camera" appears in both the slot grid (as a HomeTile title) and the bottom sheet list.
        composeRule.onAllNodesWithText("Camera").assertCountEquals(2)
        composeRule.onNodeWithText("Placed").assertIsDisplayed()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun allowedAppsScreenPlacesIntoSelectedSlot() {
        var assignedPackage: String? = null
        var assignedPosition: Int? = null
        composeRule.setContent {
            EasyUiTheme {
                AllowedAppsScreen(
                    pageCount = 2,
                    pages = listOf(
                        listOf(
                            HomeTile("phone", 0, "Phone", HomeTileType.ACTION, action = HomeTileAction.OPEN_DIALER),
                            HomeTile("messages", 1, "Messages", HomeTileType.ACTION, action = HomeTileAction.OPEN_MESSAGES),
                            null,
                            null,
                            null,
                            HomeTile("emergency", 5, "Emergency", HomeTileType.ACTION, action = HomeTileAction.EMERGENCY),
                        ),
                        listOf(null, null, null, null, null, null),
                    ),
                    installedApps = listOf(
                        InstalledApp("com.maps", "MapsActivity", "Maps"),
                    ),
                    assignedAppPackages = emptySet(),
                    onAssignApp = { pkg, pos ->
                        assignedPackage = pkg
                        assignedPosition = pos
                    },
                    onRemoveApp = {},
                    onDone = {},
                    onFinishSetup = {},
                )
            }
        }

        // Select slot at absolute position 2 (Page 1, Slot 3) via its testTag.
        // Clicking the "slot_select_2" button triggers onSelect and opens the bottom sheet.
        composeRule.onNodeWithTag("slot_select_2").performScrollTo().performClick()
        composeRule.waitForIdle()
        
        try {
            // Wait up to 5 seconds for the bottom sheet to display "Place Here"
            composeRule.waitUntil(5000) {
                composeRule.onAllNodesWithText("Place Here").fetchSemanticsNodes().isNotEmpty()
            }
            
            // "Place Here" is now visible inside the bottom sheet; selectedPosition == 2 so the button is enabled.
            composeRule.onNodeWithText("Place Here").assertIsEnabled().performClick()
        } catch (e: Throwable) {
            try {
                composeRule.onRoot(useUnmergedTree = true).printToLog("SEMANTICS")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            throw e
        }

        assert(assignedPackage == "com.maps") { "Expected onAssignApp to receive com.maps, got $assignedPackage" }
        assert(assignedPosition == 2) { "Expected onAssignApp to receive position 2, got $assignedPosition" }
    }
}
