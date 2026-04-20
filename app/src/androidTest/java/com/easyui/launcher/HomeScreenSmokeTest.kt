package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.easyui.core.domain.model.SkinConfig
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
    fun homeScreenRendersFixedGridAndTopStatusBar() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    dateText = "Friday, March 20",
                    tiles = listOf(
                        TileDisplayModel("phone", "Phone", "Phone", true, TileDisplayKind.PHONE),
                        TileDisplayModel("messages", "Messages", "Messages", true, TileDisplayKind.MESSAGES),
                        TileDisplayModel("contacts", "Contacts", "Contacts", true, TileDisplayKind.CONTACTS),
                        TileDisplayModel("photos", "Photos", "Photos", true, TileDisplayKind.PHOTOS),
                        TileDisplayModel("camera", "Camera", "Camera", true, TileDisplayKind.CAMERA),
                        TileDisplayModel("emergency", "Emergency", "Emergency", true, TileDisplayKind.EMERGENCY),
                    ),
                    pages = listOf(
                        listOf(
                            TileDisplayModel("phone", "Phone", "Phone", true, TileDisplayKind.PHONE),
                            TileDisplayModel("messages", "Messages", "Messages", true, TileDisplayKind.MESSAGES),
                            TileDisplayModel("contacts", "Contacts", "Contacts", true, TileDisplayKind.CONTACTS),
                            TileDisplayModel("photos", "Photos", "Photos", true, TileDisplayKind.PHOTOS),
                            TileDisplayModel("camera", "Camera", "Camera", true, TileDisplayKind.CAMERA),
                            TileDisplayModel("emergency", "Emergency", "Emergency", true, TileDisplayKind.EMERGENCY),
                        ),
                        listOf(
                            TileDisplayModel("app-maps", "Maps", "Maps", true, TileDisplayKind.APP),
                            null,
                            null,
                            null,
                            null,
                            null,
                        ),
                    ),
                    skinConfig = SkinConfig(),
                    onTileClick = {},
                    onOpenAppList = {},
                    onStatusBarLongPress = {},
                    onClockTapped = {},
                )
            }
        }

        composeRule.onNodeWithTag("home_top_status_bar").assertIsDisplayed()
        composeRule.onNodeWithText("Phone").assertIsDisplayed()
        composeRule.onNodeWithText("Emergency").assertIsDisplayed()
        composeRule.onNodeWithTag("home_all_apps_button").assertIsDisplayed()
        composeRule.onNodeWithTag("home_page_indicator").assertIsDisplayed()
        composeRule.onNodeWithText("Friday, March 20").assertIsDisplayed()
    }
}
