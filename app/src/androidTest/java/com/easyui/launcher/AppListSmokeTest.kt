package com.easyui.launcher

import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.rules.AppCatalogRules
import com.easyui.core.domain.rules.HiddenAppRules
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.apps.AppListScreen
import org.junit.Rule
import org.junit.Test

class AppListSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appListRendersAndFiltersBySearchQuery() {
        composeRule.setContent {
            var query by remember { mutableStateOf("") }
            val allApps = listOf(
                InstalledApp("com.camera", "CameraActivity", "Camera"),
                InstalledApp("com.phone", "PhoneActivity", "Phone"),
                InstalledApp("com.hidden", "HiddenActivity", "Hidden"),
            )
            val hiddenPackages = setOf("com.hidden")
            EasyUiTheme {
                AppListScreen(
                    query = query,
                    apps = AppCatalogRules.filterByQuery(
                        HiddenAppRules.visibleApps(allApps, hiddenPackages),
                        query,
                    ),
                    emptyTitle = null,
                    emptyBody = null,
                    onQueryChange = { query = it },
                    onAppClick = {},
                )
            }
        }

        composeRule.onNodeWithTag("app_list_screen").assertIsDisplayed()
        composeRule.onNodeWithText("Camera").assertIsDisplayed()
        composeRule.onNodeWithText("Phone").assertIsDisplayed()
        composeRule.onAllNodesWithText("Hidden").assertCountEquals(0)

        composeRule.onNodeWithTag("app_search_field").performTextInput("cam")

        composeRule.onNodeWithText("Camera").assertIsDisplayed()
        composeRule.onAllNodesWithText("Phone").assertCountEquals(0)
    }

    @Test
    fun appListShowsCalmEmptyStateWhenNothingIsVisible() {
        composeRule.setContent {
            EasyUiTheme {
                AppListScreen(
                    query = "",
                    apps = emptyList(),
                    emptyTitle = "No apps are shown here right now",
                    emptyBody = "A caregiver may have hidden some apps inside EasyUI.",
                    onQueryChange = {},
                    onAppClick = {},
                )
            }
        }

        composeRule.onNodeWithTag("app_list_empty_state").assertIsDisplayed()
        composeRule.onNodeWithText("No apps are shown here right now").assertIsDisplayed()
    }
}
