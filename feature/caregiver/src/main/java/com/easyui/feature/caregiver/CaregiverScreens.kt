package com.easyui.feature.caregiver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.easyui.core.domain.model.AppVisibilityPreset
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.rules.ContactTileRules
import com.easyui.core.ui.components.AvatarBadge
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun CaregiverToolsScreen(
    protectionEnabled: Boolean,
    layoutLocked: Boolean,
    hasPinConfigured: Boolean,
    currentPresetName: String,
    homeReadabilityPresetName: String,
    verySimpleModeEnabled: Boolean,
    favoriteContactCount: Int,
    onSetupPin: () -> Unit,
    onChangePin: () -> Unit,
    onToggleProtection: () -> Unit,
    onToggleLayoutLock: () -> Unit,
    onEditHome: () -> Unit,
    onHomeDisplay: () -> Unit,
    onManageFavoriteContacts: () -> Unit,
    onManageHiddenApps: () -> Unit,
    onFinishSetup: () -> Unit,
    onResetLauncher: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("caregiver_tools_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Caregiver Tools", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Set up EasyUI without changing how Android works outside this launcher.",
                style = MaterialTheme.typography.bodyLarge,
            )

            StatusCard(
                title = "Protection",
                lines = listOf(
                    if (hasPinConfigured) "Caregiver PIN is ready." else "No caregiver PIN is set yet.",
                    if (protectionEnabled) "Protected changes ask for the PIN." else "Protected changes are off until you turn them on.",
                ),
            )
            StatusCard(
                title = "Home stability",
                lines = listOf(
                    if (layoutLocked) "Home layout is locked for daily use." else "Home layout can be edited right now.",
                    "Favorite contacts: $favoriteContactCount",
                    "App visibility preset: ${presetLabel(currentPresetName)}",
                    "Home readability: ${readabilityLabel(runCatching { HomeReadabilityPreset.valueOf(homeReadabilityPresetName) }.getOrDefault(HomeReadabilityPreset.STANDARD))}",
                    if (verySimpleModeEnabled) "Very simple home mode is on." else "Very simple home mode is off.",
                ),
            )

            Button(onClick = if (hasPinConfigured) onChangePin else onSetupPin, modifier = Modifier.fillMaxWidth()) {
                Text(if (hasPinConfigured) "Change Caregiver PIN" else "Set Caregiver PIN")
            }

            SettingToggleRow(
                label = "Require PIN for caregiver changes",
                checked = protectionEnabled,
                onToggle = onToggleProtection,
            )
            SettingToggleRow(
                label = "Lock home layout",
                checked = layoutLocked,
                onToggle = onToggleLayoutLock,
            )

            Button(onClick = onEditHome, modifier = Modifier.fillMaxWidth()) {
                Text("Edit Home Screen")
            }
            Button(onClick = onHomeDisplay, modifier = Modifier.fillMaxWidth()) {
                Text("Home Readability")
            }
            Button(onClick = onManageFavoriteContacts, modifier = Modifier.fillMaxWidth()) {
                Text("Manage Favorite Contacts")
            }
            Button(onClick = onManageHiddenApps, modifier = Modifier.fillMaxWidth()) {
                Text("Show or Hide Apps")
            }
            Button(onClick = onFinishSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Finish Setup")
            }
            OutlinedButton(onClick = onResetLauncher, modifier = Modifier.fillMaxWidth()) {
                Text("Reset to Safe Default")
            }
        }
    }
}

@Composable
fun HomeDisplayScreen(
    currentPresetName: String,
    verySimpleModeEnabled: Boolean,
    onSelectPreset: (HomeReadabilityPreset) -> Unit,
    onToggleVerySimpleMode: (Boolean) -> Unit,
    onDone: () -> Unit,
    onFinishSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentPreset = remember(currentPresetName) {
        runCatching { HomeReadabilityPreset.valueOf(currentPresetName) }.getOrDefault(HomeReadabilityPreset.STANDARD)
    }
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("home_display_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Home Readability", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Choose one simple display style for the EasyUI home screen.",
                style = MaterialTheme.typography.bodyLarge,
            )
            HomeReadabilityPreset.entries.forEach { preset ->
                val selected = preset == currentPreset
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(EasyUiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                    ) {
                        Text(readabilityLabel(preset), style = MaterialTheme.typography.titleLarge)
                        Text(readabilityBody(preset), style = MaterialTheme.typography.bodyLarge)
                        if (selected) {
                            Text("Current choice", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Button(onClick = { onSelectPreset(preset) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Use ${readabilityLabel(preset)}")
                            }
                        }
                    }
                }
            }
            SettingToggleRow(
                label = "Very simple home mode",
                checked = verySimpleModeEnabled,
                onToggle = { onToggleVerySimpleMode(!verySimpleModeEnabled) },
            )
            Text(
                "Very simple mode only changes the EasyUI home view. It does not simplify Android outside this launcher.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(onClick = onFinishSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Finish Setup")
            }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Caregiver Tools")
            }
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    lines: List<String>,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(EasyUiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            lines.forEach { line ->
                Text(line, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}

@Composable
fun PinEntryScreen(
    title: String,
    description: String,
    pin: String,
    confirmPin: String?,
    errorMessage: String?,
    submitLabel: String,
    onPinChange: (String) -> Unit,
    onConfirmPinChange: ((String) -> Unit)?,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("pin_entry_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text(title, style = MaterialTheme.typography.headlineLarge)
            Text(description, style = MaterialTheme.typography.bodyLarge)
            OutlinedTextField(
                value = pin,
                onValueChange = onPinChange,
                label = { Text("PIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            if (onConfirmPinChange != null && confirmPin != null) {
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = onConfirmPinChange,
                    label = { Text("Confirm PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
            if (!errorMessage.isNullOrBlank()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(EasyUiSpacing.sm))
            Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) {
                Text(submitLabel)
            }
        }
    }
}

@Composable
fun EditLayoutScreen(
    tiles: List<HomeTile>,
    availableApps: List<InstalledApp>,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onRemove: (String) -> Unit,
    onAdd: (InstalledApp) -> Unit,
    onManageFavoriteContacts: () -> Unit,
    onDone: () -> Unit,
    onFinishSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primaryContainer) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("edit_layout_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Edit Home Screen", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Caregiver mode is active. Move tiles earlier or later and keep the home screen simple.",
                style = MaterialTheme.typography.bodyLarge,
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(EasyUiSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                ) {
                    Text("Add Tiles", style = MaterialTheme.typography.titleLarge)
                    Text("App tiles and favorite contact tiles are added separately so setup stays clear.", style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                        Button(onClick = onManageFavoriteContacts, modifier = Modifier.weight(1f)) {
                            Text("Favorite Contacts")
                        }
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (tiles.isEmpty()) {
                EmptyState(
                    title = "Home is empty",
                    body = "Add a favorite contact or app tile to rebuild the home screen.",
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
            ) {
                items(tiles, key = { it.id }) { tile ->
                    EditableTileRow(
                        tile = tile,
                        onMoveUp = { onMoveUp(tile.id) },
                        onMoveDown = { onMoveDown(tile.id) },
                        onRemove = if (tile.action == null) ({ onRemove(tile.id) }) else null,
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(EasyUiSpacing.lg))
                    Text("Add App Tile", style = MaterialTheme.typography.titleLarge)
                    if (availableApps.isEmpty()) {
                        Text("All visible apps already appear on the home screen.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                items(availableApps, key = { it.packageName }) { app ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(app.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Button(onClick = { onAdd(app) }) { Text("Add") }
                    }
                }
            }

            Button(onClick = onFinishSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Finish Setup")
            }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Caregiver Tools")
            }
        }
    }
}

@Composable
fun FavoriteContactsScreen(
    tiles: List<HomeTile>,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onEdit: (String?, String, String, String?) -> String?,
    onRemove: (String) -> Unit,
    onDone: () -> Unit,
    onFinishSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var editingTileId by rememberSaveable { mutableStateOf<String?>(null) }
    var displayName by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val openDocumentLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri: Uri? ->
        uri?.let {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            photoUri = it.toString()
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primaryContainer) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("favorite_contacts_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Favorite Contacts", style = MaterialTheme.typography.headlineLarge)
            Text(
                "These tiles stay on EasyUI home and open the dialer. They do not place a direct call.",
                style = MaterialTheme.typography.bodyLarge,
            )

            ContactEditorCard(
                displayName = displayName,
                phoneNumber = phoneNumber,
                photoUri = photoUri,
                errorMessage = errorMessage,
                editing = editingTileId != null,
                onDisplayNameChange = {
                    displayName = it
                    errorMessage = null
                },
                onPhoneNumberChange = {
                    phoneNumber = it
                    errorMessage = null
                },
                onPickPhoto = { openDocumentLauncher.launch(arrayOf("image/*")) },
                onClearPhoto = { photoUri = null },
                onSave = {
                    errorMessage = onEdit(editingTileId, displayName, phoneNumber, photoUri)
                    if (errorMessage == null) {
                        editingTileId = null
                        displayName = ""
                        phoneNumber = ""
                        photoUri = null
                    }
                },
                onCancelEdit = {
                    editingTileId = null
                    displayName = ""
                    phoneNumber = ""
                    photoUri = null
                    errorMessage = null
                },
            )

            if (tiles.isEmpty()) {
                EmptyState(
                    title = "No favorite contacts yet",
                    body = "Add one or two people the senior may call often.",
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
            ) {
                items(tiles, key = { it.id }) { tile ->
                    ContactTileRow(
                        tile = tile,
                        onMoveUp = { onMoveUp(tile.id) },
                        onMoveDown = { onMoveDown(tile.id) },
                        onEdit = {
                            editingTileId = tile.id
                            displayName = tile.title
                            phoneNumber = tile.phoneNumber.orEmpty()
                            photoUri = tile.photoUri
                            errorMessage = null
                        },
                        onRemove = { onRemove(tile.id) },
                    )
                }
            }

            Button(onClick = onFinishSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Finish Setup")
            }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Caregiver Tools")
            }
        }
    }
}

@Composable
private fun ContactEditorCard(
    displayName: String,
    phoneNumber: String,
    photoUri: String?,
    errorMessage: String?,
    editing: Boolean,
    onDisplayNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onPickPhoto: () -> Unit,
    onClearPhoto: () -> Unit,
    onSave: () -> Unit,
    onCancelEdit: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(EasyUiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
        ) {
            Text(if (editing) "Edit Favorite Contact" else "Add Favorite Contact", style = MaterialTheme.typography.titleLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AvatarBadge(
                    imageUri = photoUri,
                    fallbackText = ContactTileRules.photoFallback(photoUri, displayName.ifBlank { "?" }),
                )
                Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                    Button(onClick = onPickPhoto) {
                        Text(if (photoUri == null) "Choose Photo" else "Change Photo")
                    }
                    if (photoUri != null) {
                        OutlinedButton(onClick = onClearPhoto) {
                            Text("Remove Photo")
                        }
                    }
                }
            }
            OutlinedTextField(
                value = displayName,
                onValueChange = onDisplayNameChange,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("Phone number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            if (!errorMessage.isNullOrBlank()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                    Text(if (editing) "Save Contact" else "Add Contact Tile")
                }
                if (editing) {
                    OutlinedButton(onClick = onCancelEdit, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableTileRow(
    tile: HomeTile,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: (() -> Unit)?,
) {
    val phoneNumber = tile.phoneNumber
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(EasyUiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
        ) {
            Text(tileTypeLabel(tile), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(tile.title, style = MaterialTheme.typography.titleLarge)
            if (!phoneNumber.isNullOrBlank()) {
                Text(phoneNumber, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                OutlinedButton(onClick = onMoveUp, modifier = Modifier.weight(1f)) { Text("Earlier") }
                OutlinedButton(onClick = onMoveDown, modifier = Modifier.weight(1f)) { Text("Later") }
            }
            onRemove?.let {
                OutlinedButton(onClick = it, modifier = Modifier.fillMaxWidth()) { Text("Remove Tile") }
            }
        }
    }
}

@Composable
private fun ContactTileRow(
    tile: HomeTile,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    val phoneNumber = tile.phoneNumber.orEmpty()
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(EasyUiSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarBadge(
                imageUri = tile.photoUri,
                fallbackText = ContactTileRules.photoFallback(tile.photoUri, tile.title),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
            ) {
                Text(tile.title, style = MaterialTheme.typography.titleLarge)
                Text(phoneNumber, style = MaterialTheme.typography.bodyLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                    OutlinedButton(onClick = onMoveUp) { Text("Earlier") }
                    OutlinedButton(onClick = onMoveDown) { Text("Later") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                    OutlinedButton(onClick = onEdit) { Text("Edit") }
                    OutlinedButton(onClick = onRemove) { Text("Remove") }
                }
            }
        }
    }
}

@Composable
fun HiddenAppsScreen(
    apps: List<InstalledApp>,
    hiddenPackages: Set<String>,
    currentPresetName: String,
    onApplyPreset: (AppVisibilityPreset) -> Unit,
    onToggleHidden: (String, Boolean) -> Unit,
    onDone: () -> Unit,
    onFinishSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("hidden_apps_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Show or Hide Apps", style = MaterialTheme.typography.headlineLarge)
            Text("App visibility changes only what appears inside EasyUI. They do not block apps on the phone.", style = MaterialTheme.typography.bodyLarge)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(EasyUiSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                ) {
                    Text("Quick setup presets", style = MaterialTheme.typography.titleLarge)
                    Text("Current preset: ${presetLabel(currentPresetName)}", style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                        Button(onClick = { onApplyPreset(AppVisibilityPreset.ESSENTIALS_ONLY) }, modifier = Modifier.weight(1f)) {
                            Text("Essentials Only")
                        }
                        Button(onClick = { onApplyPreset(AppVisibilityPreset.MINIMAL_COMMON_APPS) }, modifier = Modifier.weight(1f)) {
                            Text("Minimal Common Apps")
                        }
                    }
                    OutlinedButton(onClick = { onApplyPreset(AppVisibilityPreset.CUSTOM) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Keep Custom Current Setup")
                    }
                }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                items(apps, key = { it.packageName }) { app ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(app.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Switch(
                            checked = app.packageName in hiddenPackages,
                            onCheckedChange = { checked -> onToggleHidden(app.packageName, checked) },
                        )
                    }
                }
            }
            Button(onClick = onFinishSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Finish Setup")
            }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Caregiver Tools")
            }
        }
    }
}

@Composable
fun ResetLauncherScreen(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("reset_launcher_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Reset to Safe Default", style = MaterialTheme.typography.headlineLarge)
            Text(
                "This resets the home layout to the starter tiles, removes favorite contact tiles, and shows hidden apps again inside EasyUI. It does not change Android system settings.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth()) {
                Text("Reset EasyUI")
            }
            OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    body: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(EasyUiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(body, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun tileTypeLabel(tile: HomeTile): String =
    when {
        tile.type == HomeTileType.CONTACT -> "Favorite Contact Tile"
        tile.type == HomeTileType.APP -> "App Tile"
        else -> "Action Tile"
    }

private fun presetLabel(presetName: String): String =
    when (runCatching { AppVisibilityPreset.valueOf(presetName) }.getOrDefault(AppVisibilityPreset.CUSTOM)) {
        AppVisibilityPreset.CUSTOM -> "Custom"
        AppVisibilityPreset.ESSENTIALS_ONLY -> "Essentials Only"
        AppVisibilityPreset.MINIMAL_COMMON_APPS -> "Minimal Common Apps"
    }

private fun readabilityLabel(preset: HomeReadabilityPreset): String =
    when (preset) {
        HomeReadabilityPreset.STANDARD -> "Standard"
        HomeReadabilityPreset.LARGER_TEXT -> "Larger Text"
        HomeReadabilityPreset.LARGER_TILES -> "Larger Tiles"
        HomeReadabilityPreset.EXTRA_SIMPLE_SPACING -> "Extra Simple Spacing"
    }

private fun readabilityBody(preset: HomeReadabilityPreset): String =
    when (preset) {
        HomeReadabilityPreset.STANDARD -> "Balanced spacing and size for everyday use."
        HomeReadabilityPreset.LARGER_TEXT -> "Makes labels easier to read without changing the layout too much."
        HomeReadabilityPreset.LARGER_TILES -> "Shows fewer, larger tiles on the home screen."
        HomeReadabilityPreset.EXTRA_SIMPLE_SPACING -> "Adds extra breathing room between home elements."
    }
