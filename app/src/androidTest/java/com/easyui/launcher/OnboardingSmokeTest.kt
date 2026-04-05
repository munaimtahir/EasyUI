package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.easyui.core.ui.theme.EasyUiTheme
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
        composeRule.onNodeWithText("EasyUI Senior Launcher").assertPresent()
        composeRule.onNodeWithText("Start Setup").assertPresent()
    }
}
