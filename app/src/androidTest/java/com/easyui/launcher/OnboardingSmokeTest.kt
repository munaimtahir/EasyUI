package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.onboarding.CaregiverHelpScreen
import com.easyui.feature.onboarding.DefaultLauncherGuidanceScreen
import com.easyui.feature.onboarding.IntroScreen
import org.junit.Rule
import org.junit.Test

class OnboardingSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun introScreenRendersPrimaryCopy() {
        composeRule.setContent {
            EasyUiTheme {
                IntroScreen(onContinue = {})
            }
        }

        composeRule.onNodeWithTag("intro_screen").assertPresent()
        composeRule.onNodeWithText("EasyUI Senior Launcher").assertIsDisplayed()
        composeRule.onNodeWithText("Offline-first").assertIsDisplayed()
        composeRule.onNodeWithText("Large targets").assertIsDisplayed()
        composeRule.onNodeWithTag("setup_primary_action").assertIsDisplayed()
        composeRule.onNodeWithText("Start Setup").assertIsDisplayed()
    }

    @Test
    fun defaultLauncherScreenShowsActionButtonsWhenNotDefault() {
        composeRule.setContent {
            EasyUiTheme {
                DefaultLauncherGuidanceScreen(
                    isDefaultLauncher = false,
                    onOpenSettings = {},
                    onRefreshStatus = {},
                    onContinue = {},
                )
            }
        }

        composeRule.onNodeWithTag("default_launcher_screen").assertPresent()
        composeRule.onNodeWithText("Set EasyUI as Home").assertIsDisplayed()
        composeRule.onNodeWithTag("setup_secondary_action").assertIsDisplayed()
        composeRule.onNodeWithTag("setup_tertiary_action").assertIsDisplayed()
        composeRule.onNodeWithText("Continue Anyway").assertIsDisplayed()
    }

    @Test
    fun defaultLauncherScreenHidesExtraButtonsWhenAlreadyDefault() {
        composeRule.setContent {
            EasyUiTheme {
                DefaultLauncherGuidanceScreen(
                    isDefaultLauncher = true,
                    onOpenSettings = {},
                    onRefreshStatus = {},
                    onContinue = {},
                )
            }
        }

        composeRule.onNodeWithText("EasyUI is ready as Home").assertIsDisplayed()
        composeRule.onAllNodesWithTag("setup_secondary_action").assertCountEquals(0)
        composeRule.onAllNodesWithTag("setup_tertiary_action").assertCountEquals(0)
        composeRule.onNodeWithText("Continue").assertIsDisplayed()
    }

    @Test
    fun caregiverHelpScreenKeepsFinishActionVisible() {
        composeRule.setContent {
            EasyUiTheme {
                CaregiverHelpScreen(onContinue = {})
            }
        }

        composeRule.onNodeWithTag("caregiver_help_screen").assertPresent()
        composeRule.onNodeWithText("Keep the phone simple").assertIsDisplayed()
        composeRule.onNodeWithText("Local protection").assertIsDisplayed()
        composeRule.onNodeWithText("Finish Setup").assertIsDisplayed()
    }
}
