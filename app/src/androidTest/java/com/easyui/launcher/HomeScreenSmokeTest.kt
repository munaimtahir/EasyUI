package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
    fun homeScreenRendersClockAndTiles() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Sunday, Mar 15",
                    tiles = listOf(
                        TileDisplayModel("apps", "All Apps", "Browse every app", true, TileDisplayKind.APPS_LIST),
                        TileDisplayModel("flashlight", "Flashlight", "Turn light on or off", true, TileDisplayKind.FLASHLIGHT),
                    ),
                    readabilityPreset = HomeReadabilityPreset.STANDARD,
                    verySimpleModeEnabled = false,
                    fallbackTitle = null,
                    fallbackBody = null,
                    onTileClick = {},
                    onCaregiverToolsClick = {},
                )
            }
        }

        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
        composeRule.onNodeWithText("9:41").assertIsDisplayed()
        composeRule.onNodeWithText("All Apps").assertIsDisplayed()
        composeRule.onNodeWithText("Flashlight").assertIsDisplayed()
    }

    @Test
    fun homeScreenRendersContactFallbackTile() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Sunday, Mar 15",
                    tiles = listOf(
                        TileDisplayModel(
                            id = "contact",
                            title = "Grace Hopper",
                            subtitle = "555-0100",
                            enabled = true,
                            kind = TileDisplayKind.CONTACT,
                            avatarFallback = "GH",
                        ),
                    ),
                    readabilityPreset = HomeReadabilityPreset.STANDARD,
                    verySimpleModeEnabled = false,
                    fallbackTitle = null,
                    fallbackBody = null,
                    onTileClick = {},
                    onCaregiverToolsClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Grace Hopper").assertIsDisplayed()
        composeRule.onNodeWithText("GH").assertIsDisplayed()
    }

    @Test
    fun homeScreenShowsFallbackCardForVerySimpleMode() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Sunday, Mar 15",
                    tiles = listOf(TileDisplayModel("apps", "All Apps", "Browse every app", true, TileDisplayKind.APPS_LIST)),
                    readabilityPreset = HomeReadabilityPreset.STANDARD,
                    verySimpleModeEnabled = true,
                    fallbackTitle = "Very simple home is on",
                    fallbackBody = "Favorite contacts and a few essentials stay easy to reach.",
                    onTileClick = {},
                    onCaregiverToolsClick = {},
                )
            }
        }

        composeRule.onNodeWithTag("home_fallback_card").assertIsDisplayed()
        composeRule.onNodeWithText("Very simple home is on").assertIsDisplayed()
    }
}
