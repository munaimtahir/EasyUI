package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import com.easyui.core.domain.model.SkinConfig
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
    fun homeShowsNoVisibleCaregiverAccessByDefault() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Friday, March 20",
                    tiles = listOf(
                        TileDisplayModel("phone", "Phone", "Phone", true, TileDisplayKind.PHONE),
                    ),
                    skinConfig = SkinConfig(),
                    onTileClick = {},
                    onStatusBarLongPress = {},
                    onClockTapped = {},
                )
            }
        }

        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("home_top_status_bar").assertIsDisplayed()
        composeRule.onNodeWithTag("home_clock_text").assertIsDisplayed()
        composeRule.onAllNodesWithText("Caregiver Access").assertCountEquals(0)
    }
}
