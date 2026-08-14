package com.easyui.senior

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit4.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.easyui.senior.storage.CaregiverRepository
import com.easyui.senior.ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation smoke tests for all new Phase B-D screens.
 *
 * Tests verify that key UI nodes render and navigation callbacks fire.
 * testTag IDs match what is declared in each screen composable.
 */
@RunWith(AndroidJUnit4::class)
class ProductScreenSmokeTest {

    @get:Rule
    val compose = createComposeRule()

    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    // ─────────────────────────────────────────────────────────────────────────
    // EmergencyScreen
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun emergencyScreen_showsSosHoldButton() {
        compose.setContent {
            EmergencyScreen(onBack = {}, onSosTriggered = {})
        }
        compose.onNodeWithTag("sos_hold_button").assertExists()
    }

    @Test
    fun emergencyScreen_backButtonInvokesCallback() {
        var backCalled = false
        compose.setContent {
            EmergencyScreen(onBack = { backCalled = true }, onSosTriggered = {})
        }
        compose.onNodeWithTag("emergency_back").performClick()
        assert(backCalled) { "Back callback was not invoked" }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NotificationScreen
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun notificationScreen_rendersBackButton() {
        compose.setContent {
            NotificationScreen(onBack = {})
        }
        compose.onNodeWithTag("notification_back").assertExists()
    }

    @Test
    fun notificationScreen_backButtonInvokesCallback() {
        var backCalled = false
        compose.setContent {
            NotificationScreen(onBack = { backCalled = true })
        }
        compose.onNodeWithTag("notification_back").performClick()
        assert(backCalled) { "Back callback was not invoked" }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RemindersScreen
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun remindersScreen_rendersScreen() {
        compose.setContent {
            RemindersScreen(onBack = {})
        }
        compose.onNodeWithTag("reminders_screen").assertExists()
    }

    @Test
    fun remindersScreen_backButtonInvokesCallback() {
        var backCalled = false
        compose.setContent {
            RemindersScreen(onBack = { backCalled = true })
        }
        compose.onNodeWithTag("reminders_back").performClick()
        assert(backCalled)
    }

    @Test
    fun remindersScreen_addButtonOpensDialog() {
        compose.setContent {
            RemindersScreen(onBack = {})
        }
        // Tag is "add_reminder_button" as declared in RemindersScreen
        compose.onNodeWithTag("add_reminder_button").performClick()
        // Dialog opens; title field should appear
        compose.onNodeWithTag("reminder_title_field").assertExists()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CaregiverPinScreen
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun caregiverPinScreen_rendersScreen() {
        compose.setContent {
            CaregiverPinScreen(
                caregiverRepo = CaregiverRepository(ctx),
                onSuccess = {},
                onCancel = {}
            )
        }
        compose.onNodeWithTag("caregiver_pin_screen").assertExists()
    }

    @Test
    fun caregiverPinScreen_numericKeyRendered() {
        compose.setContent {
            CaregiverPinScreen(
                caregiverRepo = CaregiverRepository(ctx),
                onSuccess = {},
                onCancel = {}
            )
        }
        // PIN keypad keys are tagged "pin_key_1" through "pin_key_9"
        compose.onNodeWithTag("pin_key_1").assertExists()
        compose.onNodeWithTag("pin_key_5").assertExists()
        compose.onNodeWithTag("pin_key_9").assertExists()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CaregiverSettingsScreen
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun caregiverSettingsScreen_rendersScreen() {
        compose.setContent {
            CaregiverSettingsScreen(
                caregiverRepo = CaregiverRepository(ctx),
                onBack = {}
            )
        }
        compose.onNodeWithTag("caregiver_settings_screen").assertExists()
    }

    @Test
    fun caregiverSettingsScreen_backButtonInvokesCallback() {
        var backCalled = false
        compose.setContent {
            CaregiverSettingsScreen(
                caregiverRepo = CaregiverRepository(ctx),
                onBack = { backCalled = true }
            )
        }
        compose.onNodeWithTag("caregiver_settings_back").performClick()
        assert(backCalled)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CheckInScreen
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun checkInScreen_rendersScreen() {
        compose.setContent {
            CheckInScreen(onBack = {})
        }
        compose.onNodeWithTag("checkin_screen").assertExists()
    }

    @Test
    fun checkInScreen_backButtonInvokesCallback() {
        var backCalled = false
        compose.setContent {
            CheckInScreen(onBack = { backCalled = true })
        }
        compose.onNodeWithTag("checkin_back").performClick()
        assert(backCalled)
    }
}
