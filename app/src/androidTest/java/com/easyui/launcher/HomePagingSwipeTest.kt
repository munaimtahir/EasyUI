package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.TileDisplayKind
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.home.HomeScreen
import org.junit.Rule
import org.junit.Test

class HomePagingSwipeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun homePagerSwipesAndUpdatesButtons() {
        val tiles = buildList {
            repeat(12) { index ->
                add(
                    TileDisplayModel(
                        id = "tile-$index",
                        title = "Tile $index",
                        subtitle = "Tile $index",
                        enabled = true,
                        kind = TileDisplayKind.APP,
                    ),
                )
            }
        }

        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Friday, March 20",
                    tiles = tiles,
                    skinConfig = SkinConfig(),
                    onTileClick = {},
                    onOpenAppList = {},
                    onStatusBarLongPress = {},
                    onClockTapped = {},
                    pageCount = 2,
                )
            }
        }

        composeRule.onNodeWithTag("home_page_indicator_0").assertWidthIsEqualTo(12.dp)
        composeRule.onNodeWithTag("home_page_indicator_1").assertWidthIsEqualTo(8.dp)

        composeRule.onNodeWithTag("home_pager").performTouchInput { swipeLeft() }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("home_page_indicator_0").assertWidthIsEqualTo(8.dp)
        composeRule.onNodeWithTag("home_page_indicator_1").assertWidthIsEqualTo(12.dp)
    }
}

