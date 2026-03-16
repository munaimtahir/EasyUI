package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.TileDisplayKind
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.home.HomeScreen
import org.junit.Rule
import org.junit.Test

class HomeScreenSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun homeScreenRendersPagesAndBatterySummary() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Sunday, Mar 15",
                    batterySummary = "Battery 82% · Charging",
                    pages = listOf(
                        listOf(
                            TileDisplayModel("phone", "Phone", "Open the dialer", true, TileDisplayKind.DIALER),
                            TileDisplayModel("apps", "All Apps", "Browse every app", true, TileDisplayKind.APPS_LIST),
                        ),
                        listOf(
                            TileDisplayModel("camera", "Camera", "Open Camera", true, TileDisplayKind.APP),
                        ),
                    ),
                    readabilityPreset = HomeReadabilityPreset.STANDARD,
                    verySimpleModeEnabled = false,
                    fallbackTitle = null,
                    fallbackBody = null,
                    onTileClick = {},
                    onCaregiverAccessRequested = {},
                )
            }
        }

        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
        composeRule.onNodeWithText("9:41").assertIsDisplayed()
        composeRule.onNodeWithTag("battery_summary").assertIsDisplayed()
        composeRule.onNodeWithText("Phone").assertIsDisplayed()
        composeRule.onNodeWithTag("home_page_indicator").assertIsDisplayed()
        composeRule.onNodeWithText("Page 1 of 2").assertIsDisplayed()
    }

    @Test
    fun homeScreenUsesHiddenCaregiverAccessWithoutVisibleButton() {
        var caregiverOpenCount = 0

        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Sunday, Mar 15",
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

        composeRule.onAllNodesWithText("Caregiver Tools").assertCountEquals(0)
        composeRule.onAllNodesWithText("Caregiver Settings").assertCountEquals(0)

        repeat(4) {
            composeRule.onNodeWithTag("home_header").performTouchInput {
                down(center)
                up()
            }
        }
        composeRule.runOnIdle { assert(caregiverOpenCount == 0) }

        composeRule.onNodeWithTag("home_header").performTouchInput {
            down(center)
            up()
        }
        composeRule.runOnIdle { assert(caregiverOpenCount == 1) }
    }

    @Test
    fun homeScreenRendersContactFallbackTile() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Sunday, Mar 15",
                    batterySummary = null,
                    pages = listOf(
                        listOf(
                            TileDisplayModel(
                                id = "contact",
                                title = "Grace Hopper",
                                subtitle = "555-0100",
                                enabled = true,
                                kind = TileDisplayKind.CONTACT,
                                avatarFallback = "GH",
                            ),
                        ),
                    ),
                    readabilityPreset = HomeReadabilityPreset.STANDARD,
                    verySimpleModeEnabled = false,
                    fallbackTitle = null,
                    fallbackBody = null,
                    onTileClick = {},
                    onCaregiverAccessRequested = {},
                )
            }
        }

        composeRule.onNodeWithText("Grace Hopper").assertIsDisplayed()
        composeRule.onNodeWithText("GH").assertIsDisplayed()
    }
}
