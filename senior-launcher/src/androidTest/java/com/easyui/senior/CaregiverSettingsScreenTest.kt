package com.easyui.senior

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.easyui.senior.storage.CaregiverRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Regression coverage for the caregiver PIN gate:
 * - Trust Center must require the PIN exactly like Caregiver Settings does
 *   (it used to bypass the gate entirely).
 * - "Change PIN" must require the current PIN before a new one can be set,
 *   and must never clear the old PIN before a new one is confirmed.
 */
class CaregiverSettingsScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        runBlocking { CaregiverRepository(ctx).clearPin() }
    }

    private fun continuePastOnboardingIfShown() {
        repeat(3) {
            val nodes = rule.onAllNodesWithTag("onboarding_next").fetchSemanticsNodes()
            if (nodes.isEmpty()) return
            rule.onNodeWithTag("onboarding_next").performClick()
            rule.waitForIdle()
        }
    }

    private fun enterPin(pin: String) {
        for (digit in pin) {
            rule.onNodeWithTag("pin_key_$digit").performClick()
        }
        rule.waitForIdle()
    }

    /** Drives the initial "no PIN yet" set-up flow from Caregiver Settings. */
    private fun setInitialPinViaCaregiverSettings(pin: String) {
        rule.onNodeWithTag("home_caregiver_button").performClick()
        rule.onNodeWithTag("caregiver_pin_screen").assertExists()
        enterPin(pin) // first entry
        enterPin(pin) // confirmation
        rule.onNodeWithTag("caregiver_settings_screen").assertExists()
        rule.onNodeWithTag("caregiver_settings_back").performClick()
        rule.waitForIdle()
    }

    @Test
    fun trustCenter_requiresPin_whenPinIsSet() {
        continuePastOnboardingIfShown()
        setInitialPinViaCaregiverSettings("1234")

        rule.onNodeWithTag("home_trust_button").performClick()
        // Must land on the PIN gate, not straight on Trust Center.
        rule.onNodeWithTag("caregiver_pin_screen").assertExists()
        rule.onNodeWithTag("trust_center_screen").assertDoesNotExist()

        enterPin("1234")
        rule.onNodeWithTag("trust_center_screen").assertExists()
    }

    @Test
    fun changePin_requiresCurrentPin_beforeAcceptingNewOne() {
        continuePastOnboardingIfShown()
        setInitialPinViaCaregiverSettings("1111")

        rule.onNodeWithTag("home_caregiver_button").performClick()
        rule.onNodeWithTag("caregiver_pin_screen").assertExists()
        enterPin("1111")
        rule.onNodeWithTag("caregiver_settings_screen").assertExists()

        rule.onNodeWithTag("change_pin_button").performClick()
        rule.onNodeWithTag("confirm_set_pin").performClick()
        rule.onNodeWithTag("caregiver_pin_screen").assertExists()

        // Wrong current PIN must be rejected, and must not advance to "set a new PIN".
        enterPin("0000")
        rule.onNodeWithTag("pin_error_message").assertExists()

        // Correct current PIN, then choose and confirm a new one.
        enterPin("1111")
        enterPin("2222")
        enterPin("2222")
        rule.onNodeWithTag("caregiver_settings_screen").assertExists()
        rule.onNodeWithTag("caregiver_settings_back").performClick()
        rule.waitForIdle()

        // The new PIN — not the old one — must now be required.
        rule.onNodeWithTag("home_trust_button").performClick()
        rule.onNodeWithTag("caregiver_pin_screen").assertExists()
        enterPin("2222")
        rule.onNodeWithTag("trust_center_screen").assertExists()
    }

    @Test
    fun changePin_cancelledPartway_leavesOriginalPinIntact() {
        continuePastOnboardingIfShown()
        setInitialPinViaCaregiverSettings("3333")

        rule.onNodeWithTag("home_caregiver_button").performClick()
        enterPin("3333")
        rule.onNodeWithTag("caregiver_settings_screen").assertExists()

        rule.onNodeWithTag("change_pin_button").performClick()
        rule.onNodeWithTag("confirm_set_pin").performClick()
        rule.onNodeWithTag("caregiver_pin_screen").assertExists()
        rule.onNodeWithTag("pin_key_Cancel").performClick()
        rule.waitForIdle()

        // The original PIN must still gate access — it was never cleared.
        rule.onNodeWithTag("home_trust_button").performClick()
        rule.onNodeWithTag("caregiver_pin_screen").assertExists()
        enterPin("3333")
        rule.onNodeWithTag("trust_center_screen").assertExists()
    }
}
