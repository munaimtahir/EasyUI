package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.caregiver.CaregiverToolsScreen
import com.easyui.feature.caregiver.PinEntryScreen
import org.junit.Rule
import org.junit.Test

class CaregiverProtectionSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun protectedFlowCanRequestPinScreen() {
        composeRule.setContent {
            var showPin by remember { mutableStateOf(false) }
            EasyUiTheme {
                if (showPin) {
                    PinEntryScreen(
                        title = "Enter Caregiver PIN",
                        description = "This change needs the caregiver PIN.",
                        pin = "",
                        confirmPin = null,
                        errorMessage = null,
                        submitLabel = "Verify",
                        onPinChange = {},
                        onConfirmPinChange = null,
                        onSubmit = {},
                    )
                } else {
                    CaregiverToolsScreen(
                        protectionEnabled = true,
                        layoutLocked = true,
                        hasPinConfigured = true,
                        currentPresetName = "CUSTOM",
                        homeReadabilityPresetName = "STANDARD",
                        verySimpleModeEnabled = false,
                        favoriteContactCount = 0,
                        onSetupPin = {},
                        onChangePin = {},
                        onToggleProtection = {},
                        onToggleLayoutLock = {},
                        onEditHome = { showPin = true },
                        onHomeDisplay = {},
                        onManageFavoriteContacts = {},
                        onManageHiddenApps = {},
                        onFinishSetup = {},
                        onResetLauncher = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Edit Home Screen").performClick()
        composeRule.onNodeWithTag("pin_entry_screen").assertIsDisplayed()
        composeRule.onNodeWithText("Enter Caregiver PIN").assertIsDisplayed()
    }
}
