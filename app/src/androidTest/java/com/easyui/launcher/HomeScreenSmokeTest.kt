package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
                    caregiverAccessVisible = false,
                    flashlightTriggerProgress = 0,
                    sosTriggerProgress = 0,
                    onTileClick = {},
                    onCaregiverAccessTap = {},
                )
            }
        }

        composeRule.onNodeWithTag("home_top_status_bar").assertIsDisplayed()
        composeRule.onNodeWithText("Phone").assertIsDisplayed()
        composeRule.onNodeWithText("SOS").assertIsDisplayed()
    }

    @Test
    fun homeScreenShowsHiddenCaregiverAccessAndProgress() {
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
                    caregiverAccessVisible = true,
                    flashlightTriggerProgress = 6,
                    sosTriggerProgress = 2,
                    onTileClick = {},
                    onCaregiverAccessTap = {},
                )
            }
        }

        composeRule.onNodeWithTag("caregiver_access_reveal").assertIsDisplayed()
        composeRule.onNodeWithTag("sos_trigger_progress").assertIsDisplayed()
    }
}
