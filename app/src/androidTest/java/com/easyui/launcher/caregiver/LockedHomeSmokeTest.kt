package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.TileDisplayKind
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.home.HomeScreen
import org.junit.Rule
import org.junit.Test

class LockedHomeSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun longPressDoesNotTriggerHiddenCaregiverAccess() {
        var caregiverOpenCount = 0

        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Monday, Mar 16",
                    batterySummary = null,
                    pages = listOf(
                        listOf(
                            TileDisplayModel("phone", "Phone", "Open the dialer", true, TileDisplayKind.DIALER),
                        ),
                    ),
                    readabilityPreset = HomeReadabilityPreset.STANDARD,
                    verySimpleModeEnabled = false,
                    fallbackTitle = null,
                    fallbackBody = null,
                    onTileClick = {},
                    onCaregiverAccessRequested = { caregiverOpenCount += 1 },
                )
            }
        }

        composeRule.onNodeWithTag("home_header").performTouchInput {
            down(center)
            advanceEventTime(1_500)
            up()
        }
        composeRule.runOnIdle { assert(caregiverOpenCount == 0) }
        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }
}
