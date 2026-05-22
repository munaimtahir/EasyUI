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

        composeRule.onNodeWithTag("guided_setup_welcome").assertExists()
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
                    currentStep = 1,
                    totalSteps = 10,
                    current = SetupProtectionLevel.RECOMMENDED,
                    onSelect = {},
                    onNext = {},
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithTag("guided_setup_protection_options").assertExists()
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
                    currentStep = 2,
                    totalSteps = 10,
                    visualTheme = VisualTheme.DARK_COMFORT,
                    accessibilityMode = AccessibilityMode.NONE,
                    onSelectVisualTheme = {},
                    onSelectAccessibilityMode = {},
                    onNext = {},
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithTag("guided_setup_theme_picker").assertExists()
        composeRule.onNodeWithText("Choose display style").assertIsDisplayed()
        composeRule.onNodeWithText("Dark").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Light").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("High Contrast").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Auto").performScrollTo().assertIsDisplayed()
    }
}

