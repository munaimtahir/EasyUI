package com.easyui.senior

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.easyui.senior.ui.EmergencyScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression coverage for the SOS failure path: a failed alert POST or a failed
 * dialer launch must surface a visible warning instead of silently resetting the
 * hold button as if the alert succeeded.
 */
@RunWith(AndroidJUnit4::class)
class EmergencyScreenTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun sosHold_whenAlertFails_showsFailureWarning() {
        compose.setContent {
            EmergencyScreen(onBack = {}, onSosTriggered = { false })
        }

        compose.onNodeWithTag("sos_hold_button").performTouchInput {
            down(center)
        }
        // The hold gesture requires a real 2-second elapsed wall-clock hold
        // (progress is computed from System.currentTimeMillis(), not virtual frame time).
        Thread.sleep(2200)
        compose.onNodeWithTag("sos_hold_button").performTouchInput {
            up()
        }

        compose.onNodeWithTag("sos_failure_warning").assertExists()
    }

    @Test
    fun sosHold_whenAlertSucceeds_showsNoFailureWarning() {
        compose.setContent {
            EmergencyScreen(onBack = {}, onSosTriggered = { true })
        }

        compose.onNodeWithTag("sos_hold_button").performTouchInput {
            down(center)
        }
        Thread.sleep(2200)
        compose.onNodeWithTag("sos_hold_button").performTouchInput {
            up()
        }

        compose.onNodeWithTag("sos_failure_warning").assertDoesNotExist()
    }
}
