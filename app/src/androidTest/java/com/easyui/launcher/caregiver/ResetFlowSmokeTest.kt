package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.caregiver.ResetLauncherScreen
import org.junit.Rule
import org.junit.Test

class ResetFlowSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun resetScreenCanTriggerRestoreAction() {
        composeRule.setContent {
            var resetTriggered by remember { mutableStateOf(false) }
            EasyUiTheme {
                if (resetTriggered) {
                    androidx.compose.material3.Text("Starter layout restored")
                } else {
                    ResetLauncherScreen(
                        onConfirm = { resetTriggered = true },
                        onCancel = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Reset EasyUI").performClick()
        composeRule.onNodeWithText("Starter layout restored").assertIsDisplayed()
    }
}
