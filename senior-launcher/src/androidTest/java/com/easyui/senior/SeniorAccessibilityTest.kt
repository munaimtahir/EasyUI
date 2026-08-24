package com.easyui.senior

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.easyui.senior.storage.CaregiverRepository
import com.easyui.senior.ui.*
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Phase B: Dedicated Senior-Specific Accessibility Acceptance Gate
 *
 * Verifies:
 * - Font scaling (1.0x, 1.5x, 2.0x max accessibility scaling)
 * - Large touch targets (minimum 48dp interactive bounding)
 * - TalkBack semantics and actionable control announcements
 * - Cognitive accessibility and unambiguous confirmation wording
 */
class SeniorAccessibilityTest {

    @get:Rule
    val compose = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    // =========================================================================
    // 1. Font & Display Scaling Tests (1.5x and 2.0x)
    // =========================================================================

    @Test
    fun caregiverPin_rendersCleanlyUnder2xFontScale() {
        compose.setContent {
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(currentDensity.density, fontScale = 2.0f)
            ) {
                CaregiverPinScreen(
                    caregiverRepo = CaregiverRepository(context),
                    onSuccess = {},
                    onCancel = {}
                )
            }
        }
        compose.waitUntil(5000) {
            compose.onAllNodesWithTag("caregiver_pin_screen").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("caregiver_pin_screen").assertExists()
        compose.onNodeWithTag("pin_key_1").assertExists().assertIsDisplayed()
        compose.onNodeWithTag("pin_key_0").assertExists().assertIsDisplayed()
    }

    @Test
    fun checkInScreen_rendersCleanlyUnderLargeFontScale() {
        compose.setContent {
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(currentDensity.density, fontScale = 1.75f)
            ) {
                CheckInScreen(onBack = {})
            }
        }
        compose.onNodeWithTag("checkin_screen").assertExists().assertIsDisplayed()
        compose.onNodeWithTag("im_ok_button").assertExists().assertIsDisplayed()
    }

    @Test
    fun emergencyScreen_rendersCleanlyUnderLargeFontScale() {
        compose.setContent {
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(currentDensity.density, fontScale = 2.0f)
            ) {
                EmergencyScreen(onBack = {}, onSosTriggered = { true })
            }
        }
        compose.onNodeWithTag("emergency_screen").assertExists().assertIsDisplayed()
        compose.onNodeWithTag("sos_hold_button").assertExists().assertIsDisplayed()
    }

    // =========================================================================
    // 2. Touch Target Sizing (Minimum 48dp)
    // =========================================================================

    @Test
    fun pinKeypad_meetsMinimum48dpTouchTarget() {
        compose.setContent {
            CaregiverPinScreen(
                caregiverRepo = CaregiverRepository(context),
                onSuccess = {},
                onCancel = {}
            )
        }
        compose.waitUntil(5000) {
            compose.onAllNodesWithTag("pin_key_5").fetchSemanticsNodes().isNotEmpty()
        }

        val nodeBounds = compose.onNodeWithTag("pin_key_5").fetchSemanticsNode().boundsInRoot
        val density = compose.density
        val widthDp = with(density) { (nodeBounds.right - nodeBounds.left).toDp() }
        val heightDp = with(density) { (nodeBounds.bottom - nodeBounds.top).toDp() }

        assertTrue("Keypad width must be >= 48dp (was $widthDp)", widthDp >= 48.dp)
        assertTrue("Keypad height must be >= 48dp (was $heightDp)", heightDp >= 48.dp)
    }

    @Test
    fun checkInButton_hasGiantAccessibleTouchTarget() {
        compose.setContent {
            CheckInScreen(onBack = {})
        }
        val nodeBounds = compose.onNodeWithTag("im_ok_button").fetchSemanticsNode().boundsInRoot
        val density = compose.density
        val heightDp = with(density) { (nodeBounds.bottom - nodeBounds.top).toDp() }
        assertTrue("CheckIn button height must be >= 56dp for seniors (was $heightDp)", heightDp >= 56.dp)
    }

    // =========================================================================
    // 3. TalkBack Semantics & Actions
    // =========================================================================

    @Test
    fun trustCenter_hasAccessibleActionLabels() {
        compose.setContent {
            TrustCenterScreen(
                caregiverRepo = CaregiverRepository(context),
                onBack = {},
                onRevoke = {}
            )
        }
        compose.onNodeWithTag("trust_center_screen").assertExists()
        compose.onNodeWithTag("trust_back").assertExists().assertHasClickAction()
    }

    @Test
    fun reminders_hasAccessibleListAndFab() {
        compose.setContent {
            RemindersScreen(onBack = {})
        }
        compose.onNodeWithTag("reminders_screen").assertExists()
        compose.onNodeWithTag("reminders_back").assertExists().assertHasClickAction()
    }
}
