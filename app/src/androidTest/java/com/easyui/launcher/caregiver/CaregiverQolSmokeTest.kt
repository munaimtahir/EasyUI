package com.easyui.launcher.caregiver

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.easyui.core.domain.model.AppVisibilityPreset
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.rules.AppCatalogRules
import com.easyui.core.domain.rules.AppVisibilityPresetRules
import com.easyui.core.domain.rules.HiddenAppRules
import com.easyui.core.ui.theme.EasyUiTheme
import com.easyui.feature.apps.AppListScreen
import com.easyui.feature.caregiver.CaregiverToolsScreen
import com.easyui.feature.caregiver.FavoriteContactsScreen
import com.easyui.feature.caregiver.HiddenAppsScreen
import com.easyui.feature.caregiver.HomeDisplayScreen
import com.easyui.feature.home.HomeScreen
import org.junit.Rule
import org.junit.Test

class CaregiverQolSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun caregiverHubCanOpenFavoriteContactsManager() {
        composeRule.setContent {
            var showContacts by remember { mutableStateOf(false) }
            EasyUiTheme {
                if (showContacts) {
                    FavoriteContactsScreen(
                        tiles = emptyList(),
                        onMoveUp = {},
                        onMoveDown = {},
                        onEdit = { _, _, _, _ -> null },
                        onRemove = {},
                        onDone = {},
                        onFinishSetup = {},
                    )
                } else {
                    CaregiverToolsScreen(
                        protectionEnabled = true,
                        layoutLocked = true,
                        hasPinConfigured = true,
                        currentPresetName = "CUSTOM",
                        homeReadabilityPresetName = "STANDARD",
                        verySimpleModeEnabled = false,
                        favoriteContactCount = 1,
                        onSetupPin = {},
                        onChangePin = {},
                        onToggleProtection = {},
                        onToggleLayoutLock = {},
                        onEditHome = {},
                        onHomeDisplay = {},
                        onManageFavoriteContacts = { showContacts = true },
                        onManageHiddenApps = {},
                        onFinishSetup = {},
                        onResetLauncher = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Manage Favorite Contacts").performClick()
        composeRule.onNodeWithTag("favorite_contacts_screen").assertIsDisplayed()
    }

    @Test
    fun favoriteContactsScreenCanAddAndRemoveContactTile() {
        composeRule.setContent {
            val tiles = remember { mutableStateListOf<HomeTile>() }
            EasyUiTheme {
                FavoriteContactsScreen(
                    tiles = tiles,
                    onMoveUp = {},
                    onMoveDown = {},
                    onEdit = { tileId, name, phone, photoUri ->
                        val id = tileId ?: "contact-1"
                        tiles.removeAll { it.id == id }
                        tiles.add(HomeTile(id, tiles.size, name, HomeTileType.CONTACT, phoneNumber = phone, photoUri = photoUri))
                        null
                    },
                    onRemove = { tileId -> tiles.removeAll { it.id == tileId } },
                    onDone = {},
                    onFinishSetup = {},
                )
            }
        }

        composeRule.onNodeWithText("Name").performTextInput("Grace Hopper")
        composeRule.onNodeWithText("Phone number").performTextInput("5550100")
        composeRule.onNodeWithText("Add Contact Tile").performClick()

        composeRule.onNodeWithText("Grace Hopper").assertIsDisplayed()
        composeRule.onNodeWithText("5550100").assertIsDisplayed()

        composeRule.onNodeWithText("Remove").performClick()
        composeRule.onAllNodesWithText("Grace Hopper").assertCountEquals(0)
    }

    @Test
    fun hiddenAppsScreenAppliesPresetAndHidesAppsFromLauncherList() {
        composeRule.setContent {
            var query by remember { mutableStateOf("") }
            var preset by remember { mutableStateOf(AppVisibilityPreset.CUSTOM) }
            val apps = listOf(
                InstalledApp("com.android.dialer", "PhoneActivity", "Phone"),
                InstalledApp("com.google.android.apps.messaging", "MessagesActivity", "Messages"),
                InstalledApp("com.fun.game", "GameActivity", "Game"),
            )
            val hiddenPackages = AppVisibilityPresetRules.hiddenPackagesForPreset(apps, preset)
            EasyUiTheme {
                if (preset == AppVisibilityPreset.CUSTOM) {
                    HiddenAppsScreen(
                        apps = apps,
                        hiddenPackages = hiddenPackages,
                        currentPresetName = preset.name,
                        onApplyPreset = { preset = it },
                        onToggleHidden = { _, _ -> },
                        onDone = {},
                        onFinishSetup = {},
                    )
                } else {
                    AppListScreen(
                        query = query,
                        apps = AppCatalogRules.filterByQuery(HiddenAppRules.visibleApps(apps, hiddenPackages), query),
                        emptyTitle = null,
                        emptyBody = null,
                        onQueryChange = { query = it },
                        onAppClick = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Essentials Only").performClick()
        composeRule.onNodeWithTag("app_list_screen").assertIsDisplayed()
        composeRule.onNodeWithText("Phone").assertIsDisplayed()
        composeRule.onNodeWithText("Messages").assertIsDisplayed()
        composeRule.onAllNodesWithText("Game").assertCountEquals(0)
    }

    @Test
    fun caregiverCanApplyReadabilityPreset() {
        composeRule.setContent {
            var preset by remember { mutableStateOf(HomeReadabilityPreset.STANDARD) }
            EasyUiTheme {
                HomeDisplayScreen(
                    currentPresetName = preset.name,
                    verySimpleModeEnabled = false,
                    onSelectPreset = { preset = it },
                    onToggleVerySimpleMode = {},
                    onDone = {},
                    onFinishSetup = {},
                )
            }
        }

        composeRule.onNodeWithText("Use Larger Text").performClick()
        composeRule.onNodeWithText("Current choice").assertIsDisplayed()
    }

    @Test
    fun finishSetupCanReturnToHome() {
        composeRule.setContent {
            var finished by remember { mutableStateOf(false) }
            EasyUiTheme {
                if (finished) {
                    HomeScreen(
                        timeText = "9:41",
                        dateText = "Sunday, Mar 15",
                        tiles = emptyList(),
                        readabilityPreset = HomeReadabilityPreset.STANDARD,
                        verySimpleModeEnabled = false,
                        fallbackTitle = "Home is ready",
                        fallbackBody = "Use All Apps to see everything else.",
                        onTileClick = {},
                        onCaregiverToolsClick = {},
                    )
                } else {
                    CaregiverToolsScreen(
                        protectionEnabled = true,
                        layoutLocked = true,
                        hasPinConfigured = true,
                        currentPresetName = "CUSTOM",
                        homeReadabilityPresetName = "STANDARD",
                        verySimpleModeEnabled = false,
                        favoriteContactCount = 1,
                        onSetupPin = {},
                        onChangePin = {},
                        onToggleProtection = {},
                        onToggleLayoutLock = {},
                        onEditHome = {},
                        onHomeDisplay = {},
                        onManageFavoriteContacts = {},
                        onManageHiddenApps = {},
                        onFinishSetup = { finished = true },
                        onResetLauncher = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Finish Setup").performClick()
        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }
}
