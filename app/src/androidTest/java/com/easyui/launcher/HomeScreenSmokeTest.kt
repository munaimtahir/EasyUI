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
                    batteryPercent = "82%",
                    chargingLabel = "Charging",
                    signalLabel = "Signal good",
                    simLabel = "SIM One",
                    wifiLabel = "Wi-Fi connected",
                    tiles = listOf(
                        TileDisplayModel("phone", "Phone", "Open caregiver contacts", true, TileDisplayKind.PHONE_CONTACTS),
                        TileDisplayModel("flashlight", "Flashlight", "Toggle light", true, TileDisplayKind.FLASHLIGHT),
                        TileDisplayModel("camera", "Camera", "Open camera now", true, TileDisplayKind.CAMERA),
                        TileDisplayModel("emergency", "Emergency", "Emergency call options", true, TileDisplayKind.EMERGENCY),
                        TileDisplayModel("health-info", "Health Info", "View medical card", true, TileDisplayKind.HEALTH_INFO),
                        TileDisplayModel("sos", "SOS", "Tap 3x quickly", true, TileDisplayKind.SOS),
                    ),
                    sosTriggerProgress = 0,
                    skinConfig = SkinConfig(),
                    onTileClick = {},
                    onStatusBarLongPress = {},
                    onClockTapped = {},
                )
            }
        }

        composeRule.onNodeWithTag("home_top_status_bar").assertIsDisplayed()
        composeRule.onNodeWithText("Phone").assertIsDisplayed()
        composeRule.onNodeWithText("SOS").assertIsDisplayed()
    }

    @Test
    fun homeScreenShowsSosProgressState() {
        composeRule.setContent {
            EasyUiTheme {
                HomeScreen(
                    timeText = "9:41",
                    batteryPercent = "82%",
                    chargingLabel = "Charging",
                    signalLabel = "Signal good",
                    simLabel = "SIM One",
                    wifiLabel = "Wi-Fi connected",
                    tiles = emptyList(),
                    sosTriggerProgress = 2,
                    skinConfig = SkinConfig(),
                    onTileClick = {},
                    onStatusBarLongPress = {},
                    onClockTapped = {},
                )
            }
        }

        composeRule.onNodeWithTag("sos_trigger_progress").assertIsDisplayed()
    }
}
