package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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

class LockedHomeSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun longPressDoesNotOpenEditModeFromHome() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Monday, Mar 16",
                    tiles = listOf(
                        TileDisplayModel("apps", "All Apps", "Browse every app", true, TileDisplayKind.APPS_LIST),
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

        composeRule.onNodeWithText("All Apps").performTouchInput {
            down(center)
            advanceEventTime(1_000)
            up()
        }
        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }
}
