package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.model.OptionalPermission
import com.easyui.core.domain.model.SetupProtectionLevel
import com.easyui.core.domain.model.VisualTheme
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.onboarding.PermissionsExplanationScreen
import com.easyui.feature.onboarding.ProtectionOptionsScreen
import com.easyui.feature.onboarding.ThemePickerScreen
import com.easyui.feature.onboarding.WelcomeScreen
import org.junit.Rule
import org.junit.Test

class GuidedSetupNewStepsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun welcomeScreenAllowsScrollingToFinalCopy() {
        composeRule.setContent {
            EasyUiTheme {
                WelcomeScreen(onNext = {})
            }
        }

        composeRule.onNodeWithTag("guided_setup_welcome").assertPresent()
        composeRule.onNodeWithText("Welcome to EasyUI").assertIsDisplayed()
        composeRule
            .onNodeWithText("This app does not lock the phone down. It gives you a clearer home screen and an easier setup path while keeping ownership of setup data on the device.")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithText("Start Setup").assertIsDisplayed()
    }

    @Test
    fun protectionOptionsScreenShowsAllChoices() {
        composeRule.setContent {
            EasyUiTheme {
                ProtectionOptionsScreen(
                    current = SetupProtectionLevel.RECOMMENDED,
                    onSelect = {},
                    onNext = {},
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithTag("guided_setup_protection_options").assertPresent()
        composeRule.onNodeWithText("Choose protection level").assertIsDisplayed()
        composeRule.onNodeWithText("Recommended").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Flexible").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Simple").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun themePickerScreenShowsAllThemeChoices() {
        composeRule.setContent {
            EasyUiTheme {
                ThemePickerScreen(
                    visualTheme = VisualTheme.DARK_COMFORT,
                    accessibilityMode = AccessibilityMode.NONE,
                    onSelectVisualTheme = {},
                    onSelectAccessibilityMode = {},
                    onNext = {},
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithTag("guided_setup_theme_picker").assertPresent()
        composeRule.onNodeWithText("Choose display style").assertIsDisplayed()
        composeRule.onNodeWithText("Midnight Indigo").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Calm Teal").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Soft Blue").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("High Contrast").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Warm Light").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun permissionsExplanationScreenShowsToggleRows() {
        val enabled = setOf(OptionalPermission.PHONE_DIALER, OptionalPermission.CONTACTS)
        composeRule.setContent {
            EasyUiTheme {
                PermissionsExplanationScreen(
                    enabledPermissions = enabled,
                    onSetEnabled = { _, _ -> },
                    onNext = {},
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithTag("guided_setup_permissions_explanation").assertPresent()
        composeRule.onNodeWithText("Allow helpful features").assertIsDisplayed()
        composeRule.onNodeWithText("Phone / Dialer").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Contacts").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Camera").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Backup / Restore files").performScrollTo().assertIsDisplayed()
    }
}

