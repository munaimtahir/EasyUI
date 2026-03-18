package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
                    batteryPercent = "82%",
                    chargingLabel = "Charging",
                    signalLabel = "Signal good",
                    simLabel = "SIM One",
                    wifiLabel = "Wi-Fi connected",
                    tiles = listOf(
                        TileDisplayModel("phone", "Phone", "Open caregiver contacts", true, TileDisplayKind.PHONE_CONTACTS),
                    ),
                    caregiverAccessVisible = false,
                    flashlightTriggerProgress = 0,
                    sosTriggerProgress = 0,
                    onTileClick = {},
                    onCaregiverAccessTap = {},
                )
            }
        }

        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("caregiver_access_reveal").assertDoesNotExist()
    }
}
