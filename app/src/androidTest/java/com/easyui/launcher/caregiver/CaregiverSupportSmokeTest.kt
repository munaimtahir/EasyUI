package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.easyui.launcher.assertPresent
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.caregiver.BackupRestoreScreen
import com.easyui.feature.caregiver.EmergencySettingsScreen
import com.easyui.feature.caregiver.HealthInfoEditorScreen
import com.easyui.feature.caregiver.HiddenAppsScreen
import org.junit.Rule
import org.junit.Test

class CaregiverSupportSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun backupRestoreScreenShowsPrimaryActions() {
        composeRule.setContent {
            EasyUiTheme {
                BackupRestoreScreen(
                    isExporting = false,
                    isImporting = false,
                    lastResult = "Backup saved",
                    pendingImportConfirmation = false,
                    onExport = {},
                    onPickImportFile = {},
                    onConfirmImport = {},
                    onCancelImport = {},
                    onDone = {},
                )
            }
        }

        composeRule.onNodeWithTag("backup_restore_screen").assertPresent()
        composeRule.onNodeWithText("Backup and Restore").assertPresent()
        
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasTestTag("export_button"))
        composeRule.onNodeWithTag("export_button").assertIsDisplayed()
        
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasTestTag("import_button"))
        composeRule.onNodeWithTag("import_button").assertIsDisplayed()
    }

    @Test
    fun hiddenAppsScreenShowsInstalledInventory() {
        composeRule.setContent {
            EasyUiTheme {
                HiddenAppsScreen(
                    installedApps = listOf(
                        InstalledApp("com.camera", "CameraActivity", "Camera"),
                        InstalledApp("com.maps", "MapsActivity", "Maps"),
                    ),
                    hiddenPackages = setOf("com.maps"),
                    onToggleHidden = {},
                    onDone = {},
                )
            }
        }

        composeRule.onNodeWithTag("hidden_apps_screen").assertPresent()
        composeRule.onNodeWithText("Hidden Apps").assertPresent()
        composeRule.onNodeWithText("Camera").assertPresent()
        composeRule.onNodeWithTag("hide_switch_com.maps").assertPresent()
    }

    @Test
    fun emergencySettingsScreenShowsExpandedSafetyFields() {
        composeRule.setContent {
            EasyUiTheme {
                EmergencySettingsScreen(
                    currentEmergencyNumber = "911",
                    emergencyNumbers = listOf(
                        EmergencyNumber("Ambulance", "911"),
                        EmergencyNumber("Police", "15"),
                        EmergencyNumber("Fire", "16"),
                    ),
                    sosNumbers = listOf("03001234567", "03007654321"),
                    easyUiLockEnabled = true,
                    easyUiLockTimeoutSeconds = 60,
                    onSave = {},
                    onSaveEmergencyNumbers = {},
                    onSaveSosNumbers = {},
                    onToggleEasyUiLock = {},
                    onSaveEasyUiLockTimeout = {},
                    onDone = {},
                )
            }
        }

        composeRule.onNodeWithTag("emergency_settings_screen").assertPresent()
        composeRule.onNodeWithTag("emergency_number_field").assertPresent()
        composeRule.onNodeWithText("Emergency quick numbers").assertPresent()
        composeRule.onNodeWithText("SOS numbers (up to 3)").assertPresent()
    }

    @Test
    fun healthInfoEditorScreenShowsMedicalFields() {
        composeRule.setContent {
            EasyUiTheme {
                HealthInfoEditorScreen(
                    healthInfo = HealthInfo(
                        fullName = "Test User",
                        age = "72",
                        bloodGroup = "O+",
                        allergies = "None",
                        medicalConditions = "Diabetes",
                        medicines = "Metformin",
                        doctorOrEmergencyContact = "Dr. Khan",
                        notes = "Carry glasses",
                    ),
                    onSave = {},
                    onDone = {},
                )
            }
        }

        composeRule.onNodeWithTag("health_info_editor_screen").assertPresent()
        composeRule.onNodeWithText("Health Info").assertPresent()
        composeRule.onNodeWithTag("health_info_editor_screen").performScrollToNode(hasText("Doctor / Emergency Contact"))
        composeRule.onNodeWithText("Doctor / Emergency Contact").assertPresent()
        composeRule.onNodeWithTag("health_info_editor_screen").performScrollToNode(hasText("Save Health Info"))
        composeRule.onNodeWithText("Save Health Info").assertIsDisplayed()
    }
}
