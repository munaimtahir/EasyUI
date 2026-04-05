package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.easyui.launcher.assertPresent
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.caregiver.CaregiverToolsScreen
import com.easyui.feature.caregiver.PinEntryScreen
import org.junit.Rule
import org.junit.Test

class CaregiverProtectionSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun caregiverSettingsCanOpenPinScreen() {
        composeRule.setContent {
            var showPin by remember { mutableStateOf(false) }
            EasyUiTheme {
                if (showPin) {
                    PinEntryScreen(
                        title = "Enter Caregiver PIN",
                        description = "Open caregiver settings with the local caregiver PIN.",
                        pin = "",
                        confirmPin = null,
                        errorMessage = "PIN did not match. Try again.",
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
                        currentPageCount = 2,
                        showBatteryInfo = false,
                        skinConfig = SkinConfig(),
                        favoriteContactCount = 1,
                        allowedAppCount = 3,
                        hiddenAppCount = 0,
                        healthInfoConfigured = false,
                        emergencyPhoneNumber = "911",
                        sosNumberCount = 0,
                        easyUiLockEnabled = false,
                        easyUiLockTimeoutSeconds = 60,
                        onSetupPin = {},
                        onChangePin = { showPin = true },
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
        }

        composeRule.onNodeWithText("Security & Lock").performScrollTo().performClick()
        composeRule.onNodeWithTag("pin_entry_screen").assertPresent()
        composeRule.onNodeWithText("Enter Caregiver PIN").assertIsDisplayed()
    }
}
