package com.easyui.feature.caregiver

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.LayoutMode
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.VisualTheme
import com.easyui.core.domain.rules.ContactTileRules
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.ui.components.AvatarBadge
import com.easyui.core.ui.theme.EasyUiSpacing

@Suppress("UNUSED_PARAMETER")
@Composable
fun CaregiverToolsScreen(
    protectionEnabled: Boolean,
    layoutLocked: Boolean,
    hasPinConfigured: Boolean,
    currentPageCount: Int,
    showBatteryInfo: Boolean,
    skinConfig: SkinConfig,
    favoriteContactCount: Int,
    allowedAppCount: Int,
    hiddenAppCount: Int,
    healthInfoConfigured: Boolean,
    emergencyPhoneNumber: String,
    sosNumberCount: Int,
    easyUiLockEnabled: Boolean,
    easyUiLockTimeoutSeconds: Int,
    onSetupPin: () -> Unit,
    onChangePin: () -> Unit,
    onToggleProtection: () -> Unit,
    onToggleLayoutLock: () -> Unit,
    onToggleBatteryInfo: (Boolean) -> Unit,
    onOpenLayoutPages: () -> Unit,
    onOpenAllowedApps: () -> Unit,
    onManageFavoriteContacts: () -> Unit,
    onOpenEmergencySettings: () -> Unit,
    onOpenHealthInfo: () -> Unit,
    onOpenBackupRestore: () -> Unit,
    onOpenHiddenApps: () -> Unit,
    onFinishSetup: () -> Unit,
    onResetLauncher: () -> Unit,
    onRedoGuidedSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CaregiverDashboardScreen(
        protectionEnabled = protectionEnabled,
        layoutLocked = layoutLocked,
        hasPinConfigured = hasPinConfigured,
        currentPageCount = currentPageCount,
        showBatteryInfo = showBatteryInfo,
        favoriteContactCount = favoriteContactCount,
        allowedAppCount = allowedAppCount,
        hiddenAppCount = hiddenAppCount,
        healthInfoConfigured = healthInfoConfigured,
        emergencyPhoneNumber = emergencyPhoneNumber,
        sosNumberCount = sosNumberCount,
        easyUiLockEnabled = easyUiLockEnabled,
        easyUiLockTimeoutSeconds = easyUiLockTimeoutSeconds,
        onSetupPin = onSetupPin,
        onChangePin = onChangePin,
        onToggleProtection = onToggleProtection,
        onToggleLayoutLock = onToggleLayoutLock,
        onToggleBatteryInfo = onToggleBatteryInfo,
        onOpenLayoutPages = onOpenLayoutPages,
        onOpenAllowedApps = onOpenAllowedApps,
        onManageFavoriteContacts = onManageFavoriteContacts,
        onOpenEmergencySettings = onOpenEmergencySettings,
        onOpenHealthInfo = onOpenHealthInfo,
        onOpenBackupRestore = onOpenBackupRestore,
        onOpenHiddenApps = onOpenHiddenApps,
        onFinishSetup = onFinishSetup,
        onResetLauncher = onResetLauncher,
        onRedoGuidedSetup = onRedoGuidedSetup,
        modifier = modifier,
    )
}

@Composable
fun LayoutPagesScreen(
    currentPageCount: Int,
    skinConfig: SkinConfig,
    onIncreasePageCount: () -> Unit,
    onDecreasePageCount: () -> Unit,
    onSelectLayoutMode: (LayoutMode) -> Unit,
    onSelectTheme: (VisualTheme, AccessibilityMode) -> Unit,
    
    onDone: () -> Unit,
    onFinishSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("layout_pages_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            item {
                Text("Layout and Pages", style = MaterialTheme.typography.headlineLarge)
            }
            item {
                Text(
                    "Keep home simple. Use one to three fixed pages with large slots that do not move during daily use.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(EasyUiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                    ) {
                        Text("Home Pages", style = MaterialTheme.typography.titleLarge)
                        Text("Current pages: $currentPageCount", style = MaterialTheme.typography.bodyLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                            OutlinedButton(
                                onClick = onDecreasePageCount,
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("Use Fewer")
                            }
                            Button(
                                onClick = onIncreasePageCount,
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("Add Page")
                            }
                        }
                        Text(
                            "The senior home keeps its fixed essentials. These pages control caregiver-managed Home Apps slots.",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
            item {
                Text("Layout Behavior", style = MaterialTheme.typography.titleLarge)
            }
            items(LayoutMode.entries) { mode ->
                val selected = mode == skinConfig.layoutMode
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(EasyUiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                    ) {
                        Text(layoutModeLabel(mode), style = MaterialTheme.typography.titleLarge)
                        Text(layoutModeBody(mode), style = MaterialTheme.typography.bodyLarge)
                        if (selected) {
                            Text("Current choice", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Button(onClick = { onSelectLayoutMode(mode) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Use ${layoutModeLabel(mode)}")
                            }
                        }
                    }
                }
            }
            item {
                Text("Visual Theme", style = MaterialTheme.typography.titleLarge)
            }
            items(VisualTheme.entries) { theme ->
                val selected = theme == skinConfig.visualTheme
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(EasyUiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                    ) {
                        Text(visualThemeLabel(theme), style = MaterialTheme.typography.titleLarge)
                        if (selected) {
                            Text("Current choice", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Button(onClick = { onSelectTheme(theme, AccessibilityMode.NONE) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Use ${visualThemeLabel(theme)}")
                            }
                        }
                    }
                }
            }
            item {
                Text("Accessibility", style = MaterialTheme.typography.titleLarge)
            }
            items(AccessibilityMode.entries) { mode ->
                val selected = mode == skinConfig.accessibilityMode
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(EasyUiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                    ) {
                        Text(accessibilityModeLabel(mode), style = MaterialTheme.typography.titleLarge)
                        if (selected) {
                            Text("Current choice", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Button(onClick = { onSelectTheme(skinConfig.visualTheme, mode) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Use ${accessibilityModeLabel(mode)}")
                            }
                        }
                    }
                }
            }
            item {
                Button(onClick = onFinishSetup, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to Home")
                }
            }
            item {
                OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to Caregiver Settings")
                }
            }
        }
    }
}

@Composable
@androidx.compose.material3.ExperimentalMaterial3Api
fun AllowedAppsScreen(
    pageCount: Int,
    pages: List<List<HomeTile?>>,
    installedApps: List<InstalledApp>,
    assignedAppPackages: Set<String>,
    onAssignApp: (String, Int) -> Unit,
    onRemoveApp: (String) -> Unit,
    onDone: () -> Unit,
    onFinishSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPageIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedPosition by rememberSaveable { mutableStateOf<Int?>(null) }
    val currentPage = pages.getOrElse(selectedPageIndex) { List(HomeLayoutRules.SLOTS_PER_PAGE) { null } }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(EasyUiSpacing.lg)
                .testTag("allowed_apps_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Home Apps", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Choose which apps appear in EasyUI's caregiver-managed Home Apps area and place each app into a fixed slot.",
                style = MaterialTheme.typography.bodyLarge,
            )

            // Add preview of the home layout
            HomeLayoutPreviewCard(
                pages = pages,
                currentPageIndex = selectedPageIndex,
                pageCount = pageCount,
                layoutLocked = false,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                repeat(pageCount) { pageIndex ->
                    val selected = pageIndex == selectedPageIndex
                    if (selected) {
                        Button(onClick = {
                            selectedPageIndex = pageIndex
                            selectedPosition = null
                        }, modifier = Modifier.weight(1f)) {
                            Text("Page ${pageIndex + 1}")
                        }
                    } else {
                        OutlinedButton(onClick = {
                            selectedPageIndex = pageIndex
                            selectedPosition = null
                        }, modifier = Modifier.weight(1f)) {
                            Text("Page ${pageIndex + 1}")
                        }
                    }
                }
            }

            // Non-lazy slot grid: 2 columns rendered as Row pairs so the scrollable Column works correctly.
            // (LazyVerticalGrid cannot be nested inside a vertically scrollable Column.)
            val slotsPerRow = 2
            // Ceiling division: number of rows needed to display all slots in pairs
            val numRows = (currentPage.size + slotsPerRow - 1) / slotsPerRow
            Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                repeat(numRows) { rowIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                    ) {
                        repeat(slotsPerRow) { colIndex ->
                            val slotIndex = rowIndex * slotsPerRow + colIndex
                            if (slotIndex < currentPage.size) {
                                val position = (selectedPageIndex * HomeLayoutRules.SLOTS_PER_PAGE) + slotIndex
                                val tile = currentPage.getOrNull(slotIndex)
                                AllowedAppSlotCard(
                                    tile = tile,
                                    position = position,
                                    selected = selectedPosition == position,
                                    onSelect = { selectedPosition = position },
                                    onRemove = {
                                        tile?.packageName?.let(onRemoveApp)
                                        if (selectedPosition == position) selectedPosition = null
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            if (selectedPosition == null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(EasyUiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                    ) {
                        Text("Installed Apps", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Select an empty home slot above to pick an app to place there.",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
    
    if (selectedPosition != null) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { selectedPosition = null },
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = EasyUiSpacing.md, vertical = EasyUiSpacing.sm)
                    .testTag("allowed_apps_installed_list"),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)
            ) {
                Text(
                    text = "Select an app for this slot",
                    style = MaterialTheme.typography.titleLarge
                )
                
                val currentPkg = pages.flatten().firstOrNull { 
                    it?.position == selectedPosition 
                }?.packageName
                
                if (currentPkg != null) {
                    OutlinedButton(
                        onClick = {
                            onRemoveApp(currentPkg)
                            selectedPosition = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Remove Current App")
                    }
                }
                
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)
                ) {
                    items(installedApps.size) { index ->
                        val app = installedApps[index]
                        val isAssigned = app.packageName in assignedAppPackages
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = EasyUiSpacing.xs),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(app.label, style = MaterialTheme.typography.bodyLarge)
                            if (isAssigned) {
                                Text("Placed", style = MaterialTheme.typography.labelMedium)
                            } else {
                                Button(
                                    onClick = { 
                                        onAssignApp(app.packageName, selectedPosition!!)
                                        selectedPosition = null 
                                    }
                                ) {
                                    Text("Place Here")
                                }
                            }
                        }
                    }
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(EasyUiSpacing.lg))
            }
        }

            Button(onClick = onFinishSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Home")
            }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Caregiver Settings")
            }
        }
    }
}

@Composable
private fun AllowedAppSlotCard(
    tile: HomeTile?,
    position: Int,
    selected: Boolean,
    onSelect: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FeaturePanelCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(EasyUiSpacing.md),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                Text(slotLabel(position), style = MaterialTheme.typography.labelLarge)
                when {
                    tile == null -> {
                        Text("Empty slot", style = MaterialTheme.typography.titleLarge)
                        Text("Ready for a home app.", style = MaterialTheme.typography.bodyLarge)
                    }
                    tile.action != null -> {
                        Text(tile.title, style = MaterialTheme.typography.titleLarge)
                        Text("Fixed for daily use.", style = MaterialTheme.typography.bodyLarge)
                    }
                    tile.type == HomeTileType.CONTACT -> {
                        Text(tile.title, style = MaterialTheme.typography.titleLarge)
                        Text("Managed in Call Shortcuts.", style = MaterialTheme.typography.bodyLarge)
                    }
                    else -> {
                        Text(tile.title, style = MaterialTheme.typography.titleLarge)
                        Text("Allowed home app.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                if (tile == null || tile.type == HomeTileType.APP) {
                    if (selected) {
                        Button(
                            onClick = onSelect,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("slot_select_$position"),
                        ) {
                            Text("Selected")
                        }
                    } else {
                        OutlinedButton(
                            onClick = onSelect,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("slot_select_$position"),
                        ) {
                            Text("Use This Slot")
                        }
                    }
                }
                if (tile?.type == HomeTileType.APP) {
                    OutlinedButton(onClick = onRemove, modifier = Modifier.fillMaxWidth()) {
                        Text("Remove App")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    body: List<String>,
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
) {
    FeaturePanelCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(EasyUiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
        ) {
            Text(
                "Caregiver tool",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(title, style = MaterialTheme.typography.titleLarge)
            body.forEach { line ->
                Text(
                    line,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(onClick = onPrimaryClick, modifier = Modifier.fillMaxWidth()) {
                Text(primaryLabel)
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(horizontal = EasyUiSpacing.md, vertical = EasyUiSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun FeaturePanelCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        content()
    }
}

@Composable
private fun CaregiverHeroCard(
    title: String,
    body: String,
    eyebrow: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(EasyUiSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = eyebrow,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(title, style = MaterialTheme.typography.headlineLarge)
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
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
                modifier = Modifier.fillMaxWidth().testTag("pin_input"),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            )
            if (onConfirmPinChange != null && confirmPin != null) {
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = onConfirmPinChange,
                    label = { Text("Confirm PIN") },
                    modifier = Modifier.fillMaxWidth().testTag("pin_confirm_input"),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
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
            Text("Call Shortcuts", style = MaterialTheme.typography.headlineLarge)
            Text(
                "These shortcuts stay on EasyUI home and open the dialer with the saved number.",
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
                    title = "No call shortcuts yet",
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
                Text("Back to Home")
            }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Caregiver Settings")
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
            Text(if (editing) "Edit Call Shortcut" else "Add Call Shortcut", style = MaterialTheme.typography.titleLarge)
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
                    Text(if (editing) "Save Shortcut" else "Add Call Shortcut")
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
                Text(slotLabel(tile.position), style = MaterialTheme.typography.bodyMedium)
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
                "This resets the home layout to the starter EasyUI tiles and removes call shortcuts from home. It does not change Android system settings.",
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

private fun slotLabel(position: Int): String {
    val page = (position / HomeLayoutRules.SLOTS_PER_PAGE) + 1
    val slot = (position % HomeLayoutRules.SLOTS_PER_PAGE) + 1
    return "Page $page, Slot $slot"
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
        HomeReadabilityPreset.STANDARD -> "Uses the locked senior home layout with balanced spacing."
        HomeReadabilityPreset.LARGER_TEXT -> "Keeps the same home layout while favoring larger text where supported."
        HomeReadabilityPreset.LARGER_TILES -> "Keeps the same 2x3 layout while favoring roomier tile spacing where supported."
        HomeReadabilityPreset.EXTRA_SIMPLE_SPACING -> "Keeps the home calm with extra breathing room where supported."
    }

private fun layoutModeLabel(mode: LayoutMode): String =
    when (mode) {
        LayoutMode.SIMPLE_CLASSIC -> "Simple Classic"
        LayoutMode.VERY_SIMPLE -> "Very Simple"
        LayoutMode.CARE_MODE -> "Care Mode"
        LayoutMode.COMMUNICATION_MODE -> "Communication Mode"
    }

private fun layoutModeBody(mode: LayoutMode): String =
    when (mode) {
        LayoutMode.SIMPLE_CLASSIC -> "Uses the fixed senior home layout."
        LayoutMode.VERY_SIMPLE -> "Keeps the same fixed layout while favoring maximum readability."
        LayoutMode.CARE_MODE -> "Keeps the same fixed layout while prioritizing urgent caregiver-friendly actions."
        LayoutMode.COMMUNICATION_MODE -> "Keeps the same fixed layout while prioritizing calling and contacts."
    }

private fun visualThemeLabel(theme: VisualTheme): String =
    when (theme) {
        VisualTheme.LIGHT_PREMIUM -> "Warm Light"
        VisualTheme.DARK_COMFORT -> "Dark Comfort"
        VisualTheme.CLINICAL_PROFESSIONAL -> "Clinical Professional"
        VisualTheme.SOFT_CALM -> "Soft Calm"
    }

private fun accessibilityModeLabel(mode: AccessibilityMode): String =
    when (mode) {
        AccessibilityMode.NONE -> "None"
        AccessibilityMode.HIGH_CONTRAST -> "High Contrast"
        AccessibilityMode.BOLD_ACCESSIBILITY -> "Bold Accessibility"
    }

@Composable
fun EmergencySettingsScreen(
    currentEmergencyNumber: String,
    emergencyNumbers: List<com.easyui.core.domain.model.EmergencyNumber>,
    sosNumbers: List<String>,
    easyUiLockEnabled: Boolean,
    easyUiLockTimeoutSeconds: Int,
    onSave: (String) -> Unit,
    onSaveEmergencyNumbers: (List<com.easyui.core.domain.model.EmergencyNumber>) -> Unit,
    onSaveSosNumbers: (List<String>) -> Unit,
    onToggleEasyUiLock: (Boolean) -> Unit,
    onSaveEasyUiLockTimeout: (Int) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var number by rememberSaveable { mutableStateOf(currentEmergencyNumber) }
    var ambulance by rememberSaveable { mutableStateOf(emergencyNumbers.getOrNull(0)?.phoneNumber ?: "911") }
    var police by rememberSaveable { mutableStateOf(emergencyNumbers.getOrNull(1)?.phoneNumber ?: "911") }
    var fire by rememberSaveable { mutableStateOf(emergencyNumbers.getOrNull(2)?.phoneNumber ?: "911") }
    var custom1Label by rememberSaveable { mutableStateOf(emergencyNumbers.getOrNull(3)?.label ?: "") }
    var custom1Number by rememberSaveable { mutableStateOf(emergencyNumbers.getOrNull(3)?.phoneNumber ?: "") }
    var custom2Label by rememberSaveable { mutableStateOf(emergencyNumbers.getOrNull(4)?.label ?: "") }
    var custom2Number by rememberSaveable { mutableStateOf(emergencyNumbers.getOrNull(4)?.phoneNumber ?: "") }
    var sos1 by rememberSaveable { mutableStateOf(sosNumbers.getOrNull(0).orEmpty()) }
    var sos2 by rememberSaveable { mutableStateOf(sosNumbers.getOrNull(1).orEmpty()) }
    var sos3 by rememberSaveable { mutableStateOf(sosNumbers.getOrNull(2).orEmpty()) }
    var lockEnabled by rememberSaveable { mutableStateOf(easyUiLockEnabled) }
    var timeoutSeconds by rememberSaveable { mutableStateOf(easyUiLockTimeoutSeconds.toString()) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("emergency_settings_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Emergency Number", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Set the phone number the Emergency tile will dial. This only changes EasyUI and does not affect the system dialer or emergency services.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "Leave as 911 if you want the tile to open the dialer without pre-filling a number.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = number,
                onValueChange = {
                    number = it
                    error = null
                },
                label = { Text("Emergency phone number") },
                modifier = Modifier.fillMaxWidth().testTag("emergency_number_field"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )
            if (!error.isNullOrBlank()) {
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = {
                    val trimmed = number.trim()
                    if (trimmed.isBlank()) {
                        error = "Enter a phone number or use 911 as the default."
                    } else {
                        onSave(trimmed)
                        onSaveEmergencyNumbers(
                            listOf(
                                com.easyui.core.domain.model.EmergencyNumber("Ambulance", ambulance.trim()),
                                com.easyui.core.domain.model.EmergencyNumber("Police", police.trim()),
                                com.easyui.core.domain.model.EmergencyNumber("Fire", fire.trim()),
                            ) + listOfNotNull(
                                custom1Label.trim().takeIf { it.isNotBlank() }?.let {
                                    com.easyui.core.domain.model.EmergencyNumber(it, custom1Number.trim())
                                },
                                custom2Label.trim().takeIf { it.isNotBlank() }?.let {
                                    com.easyui.core.domain.model.EmergencyNumber(it, custom2Number.trim())
                                },
                            ),
                        )
                        onSaveSosNumbers(listOf(sos1, sos2, sos3))
                        onToggleEasyUiLock(lockEnabled)
                        val timeout = timeoutSeconds.toIntOrNull()?.coerceIn(15, 300)
                        if (timeout != null) {
                            onSaveEasyUiLockTimeout(timeout)
                        }
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save")
            }
            Text("Emergency quick numbers", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(value = ambulance, onValueChange = { ambulance = it }, label = { Text("Ambulance") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = police, onValueChange = { police = it }, label = { Text("Police") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = fire, onValueChange = { fire = it }, label = { Text("Fire") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = custom1Label, onValueChange = { custom1Label = it }, label = { Text("Custom label 1") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = custom1Number, onValueChange = { custom1Number = it }, label = { Text("Custom number 1") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = custom2Label, onValueChange = { custom2Label = it }, label = { Text("Custom label 2") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = custom2Number, onValueChange = { custom2Number = it }, label = { Text("Custom number 2") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Text("SOS numbers (up to 3)", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(value = sos1, onValueChange = { sos1 = it }, label = { Text("SOS number 1") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = sos2, onValueChange = { sos2 = it }, label = { Text("SOS number 2") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = sos3, onValueChange = { sos3 = it }, label = { Text("SOS number 3") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            SettingToggleRow(
                label = "Enable EasyUI lock overlay",
                checked = lockEnabled,
                onCheckedChange = { lockEnabled = it },
            )
            OutlinedTextField(
                value = timeoutSeconds,
                onValueChange = { timeoutSeconds = it },
                label = { Text("Lock timeout (15-300 sec)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun HealthInfoEditorScreen(
    healthInfo: HealthInfo,
    onSave: (HealthInfo) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var fullName by rememberSaveable { mutableStateOf(healthInfo.fullName) }
    var age by rememberSaveable { mutableStateOf(healthInfo.age) }
    var bloodGroup by rememberSaveable { mutableStateOf(healthInfo.bloodGroup) }
    var allergies by rememberSaveable { mutableStateOf(healthInfo.allergies) }
    var conditions by rememberSaveable { mutableStateOf(healthInfo.medicalConditions) }
    var medicines by rememberSaveable { mutableStateOf(healthInfo.medicines) }
    var doctorContact by rememberSaveable { mutableStateOf(healthInfo.doctorOrEmergencyContact) }
    var notes by rememberSaveable { mutableStateOf(healthInfo.notes) }

    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("health_info_editor_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            item {
                Text("Health Info", style = MaterialTheme.typography.headlineLarge)
            }
            item {
                Text(
                    "Save simple health details that can be opened from the home screen.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    singleLine = true,
                )
            }
            item {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Age") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            item {
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = { bloodGroup = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Blood Group") },
                    singleLine = true,
                )
            }
            item {
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Allergies") },
                )
            }
            item {
                OutlinedTextField(
                    value = conditions,
                    onValueChange = { conditions = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Medical Conditions") },
                )
            }
            item {
                OutlinedTextField(
                    value = medicines,
                    onValueChange = { medicines = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Medicines") },
                )
            }
            item {
                OutlinedTextField(
                    value = doctorContact,
                    onValueChange = { doctorContact = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Doctor / Emergency Contact") },
                )
            }
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Notes") },
                    minLines = 3,
                )
            }
            item {
                Button(
                    onClick = {
                        onSave(
                            HealthInfo(
                                fullName = fullName.trim(),
                                age = age.trim(),
                                bloodGroup = bloodGroup.trim(),
                                allergies = allergies.trim(),
                                medicalConditions = conditions.trim(),
                                medicines = medicines.trim(),
                                doctorOrEmergencyContact = doctorContact.trim(),
                                notes = notes.trim(),
                            ),
                        )
                        onDone()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Save Health Info")
                }
            }
            item {
                OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to Caregiver Settings")
                }
            }
        }
    }
}
