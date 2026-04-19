package com.easyui.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.ui.components.WizardShell
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun WelcomeScreen(
    onNext: () -> Unit
) {
    WizardShell(
        title = "Welcome to EasyUI",
        subtitle = "We'll help you set up this phone for a senior. It takes about 5 minutes.",
        onNext = onNext,
        nextLabel = "Start Setup",
        showProgress = false
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)) {
            TrustBullet(
                icon = Icons.Outlined.PhoneAndroid,
                title = "Minimal by default",
                body = "Only the most important actions stay visible so daily use feels calm and predictable.",
            )
            TrustBullet(
                icon = Icons.Outlined.CloudOff,
                title = "Offline and device-first",
                body = "EasyUI works without an account. Your setup stays on this phone unless you export it.",
            )
            Text(
                "This app does not lock the phone down. It gives you a clearer home screen and an easier setup path while keeping ownership of setup data on the device.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun LauncherActivationScreen(
    isDefaultLauncher: Boolean,
    onOpenSettings: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    WizardShell(
        title = "Set EasyUI as Home",
        subtitle = "EasyUI works best when it's the default home app. This keeps the senior from accidentally leaving the simple interface.",
        onNext = onNext,
        onBack = onBack,
        currentStep = 2,
        nextLabel = if (isDefaultLauncher) "Next" else "Waiting for EasyUI...",
        isNextEnabled = isDefaultLauncher
    ) {
        if (!isDefaultLauncher) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(EasyUiSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)
                ) {
                    Text("The phone is currently using another home app.", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                        Text("Open Default App Settings")
                    }
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Text("EasyUI is the default launcher.", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
fun ReadabilityPresetScreen(
    currentPreset: HomeReadabilityPreset,
    onPresetSelected: (HomeReadabilityPreset) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    WizardShell(
        title = "Choose Readability",
        subtitle = "How large should tiles and text be? This controls the senior home's look and feel.",
        onNext = onNext,
        onBack = onBack,
        currentStep = 3
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
            items(HomeReadabilityPreset.entries) { preset ->
                val selected = preset == currentPreset
                Card(
                    onClick = { onPresetSelected(preset) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (selected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors(),
                    border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Column(modifier = Modifier.padding(EasyUiSpacing.md)) {
                        Text(preset.name.replace("_", " "), style = MaterialTheme.typography.titleLarge)
                        Text(
                            when (preset) {
                                HomeReadabilityPreset.STANDARD -> "Standard size for users with good vision."
                                HomeReadabilityPreset.LARGER_TEXT -> "Larger text for better legibility."
                                HomeReadabilityPreset.LARGER_TILES -> "Larger buttons for easier targets."
                                HomeReadabilityPreset.EXTRA_SIMPLE_SPACING -> "Simplified layout with maximum clarity."
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeLayoutSetupScreen(
    homePageCount: Int,
    onPageCountChange: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    WizardShell(
        title = "Home Layout",
        subtitle = "EasyUI uses a fixed 2x3 home for essentials. You can add extra pages for more apps.",
        onNext = onNext,
        onBack = onBack,
        currentStep = 4
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(EasyUiSpacing.md), verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                    Text("Number of Pages", style = MaterialTheme.typography.titleLarge)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            val pageNum = index + 1
                            val selected = pageNum == homePageCount
                            if (selected) {
                                Button(onClick = { onPageCountChange(pageNum) }, modifier = Modifier.weight(1f)) {
                                    Text("$pageNum")
                                }
                            } else {
                                OutlinedButton(onClick = { onPageCountChange(pageNum) }, modifier = Modifier.weight(1f)) {
                                    Text("$pageNum")
                                }
                            }
                        }
                    }
                    Text(
                        "Each page adds 6 more app slots. Most seniors prefer 1 or 2 pages.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            // Visualization of 2x3 grid
            Text("Senior Home Preview", style = MaterialTheme.typography.titleMedium)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(180.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false
            ) {
                val mockTiles = listOf("Phone", "Messages", "Contacts", "Photos", "Camera", "Emergency")
                items(6) { index ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().aspectRatio(1.5f)) {
                            Text(mockTiles[index], style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllowedAppsSetupScreen(
    pageCount: Int,
    pages: List<List<HomeTile?>>,
    installedApps: List<InstalledApp>,
    assignedAppPackages: Set<String>,
    onAssignApp: (String, Int) -> Unit,
    onRemoveApp: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var selectedPageIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedPosition by remember { mutableStateOf<Int?>(null) }
    val currentPage = pages.getOrElse(selectedPageIndex) { List(6) { null } }

    WizardShell(
        title = "Apps on Home",
        subtitle = "Select an empty slot on a page, then pick an app to place there.",
        onNext = onNext,
        onBack = onBack,
        currentStep = 5
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
            // Page selection
            Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                repeat(pageCount) { i ->
                    val selected = i == selectedPageIndex
                    if (selected) {
                        Button(onClick = { selectedPageIndex = i; selectedPosition = null }, modifier = Modifier.weight(1f)) {
                            Text("Page ${i + 1}")
                        }
                    } else {
                        OutlinedButton(onClick = { selectedPageIndex = i; selectedPosition = null }, modifier = Modifier.weight(1f)) {
                            Text("Page ${i + 1}")
                        }
                    }
                }
            }

            // Grid of slots
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(180.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false
            ) {
                itemsIndexed(currentPage) { slotIndex, tile ->
                    val position = (selectedPageIndex * 6) + slotIndex
                    val isSelected = selectedPosition == position
                    Card(
                        onClick = { selectedPosition = position },
                        colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors(),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().aspectRatio(1.5f)) {
                            Text(tile?.title ?: "Empty Slot", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // App list
            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(modifier = Modifier.padding(EasyUiSpacing.md)) {
                    Text("Pick an App", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                        items(installedApps) { app ->
                            val isAssigned = app.packageName in assignedAppPackages
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(app.label, style = MaterialTheme.typography.bodyMedium)
                                if (isAssigned) {
                                    Text("Placed", style = MaterialTheme.typography.labelSmall)
                                } else {
                                    Button(
                                        onClick = { selectedPosition?.let { onAssignApp(app.packageName, it) } },
                                        enabled = selectedPosition != null,
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Place")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecuritySetupScreen(
    pin: String,
    confirmPin: String,
    errorMessage: String?,
    layoutLocked: Boolean,
    onLayoutLockedChange: (Boolean) -> Unit,
    onPinChange: (String) -> Unit,
    onConfirmPinChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    WizardShell(
        title = "Security & Layout Lock",
        subtitle = "Protect the setup by setting a local PIN and locking the home layout from accidental moves.",
        onNext = onNext,
        onBack = onBack,
        onSkip = onSkip,
        currentStep = 7,
        nextLabel = "Apply Security"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(EasyUiSpacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Lock Home Layout", style = MaterialTheme.typography.titleLarge)
                        Text("Prevents the senior from accidentally moving or removing tiles.", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(checked = layoutLocked, onCheckedChange = onLayoutLockedChange)
                }
            }

            Text("Caregiver PIN", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = pin,
                onValueChange = onPinChange,
                label = { Text("Caregiver PIN (4+ digits)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPin,
                onValueChange = onConfirmPinChange,
                label = { Text("Confirm PIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
            Text(
                "If you skip this, caregiver settings will be open to anyone. You can always set a PIN later.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DeviceSupportScreen(
    showBattery: Boolean,
    onToggleBattery: (Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    WizardShell(
        title = "Home Details",
        subtitle = "Fine-tune what appears on the senior home screen.",
        onNext = onNext,
        onBack = onBack,
        currentStep = 8
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(EasyUiSpacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show Battery Info", style = MaterialTheme.typography.titleLarge)
                        Text("Adds a battery percentage indicator to the home screen.", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(checked = showBattery, onCheckedChange = onToggleBattery)
                }
            }
        }
    }
}

@Composable
fun ContactsSetupScreen(
    tiles: List<HomeTile>,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onEdit: (String?, String, String, String?) -> String?,
    onRemove: (String) -> Unit,
    emergencyMode: String,
    onEmergencyModeChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var displayName by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val hasNoContacts = tiles.isEmpty()

    WizardShell(
        title = "Call Shortcuts & Emergency",
        subtitle = if (hasNoContacts) {
            "Add the people the senior calls most often and decide what the 'Emergency' tile does. At least one shortcut is recommended."
        } else {
            "Add the people the senior calls most often and decide what the 'Emergency' tile does."
        },
        onNext = onNext,
        onBack = onBack,
        currentStep = 6
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(EasyUiSpacing.md), verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                    Text("Emergency Button Mode", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                        val modes = listOf("MENU", "SOS")
                        modes.forEach { mode ->
                            val selected = mode == emergencyMode
                            if (selected) {
                                Button(onClick = { onEmergencyModeChange(mode) }, modifier = Modifier.weight(1f)) {
                                    Text(if (mode == "MENU") "Choice Menu" else "Direct Dial")
                                }
                            } else {
                                OutlinedButton(onClick = { onEmergencyModeChange(mode) }, modifier = Modifier.weight(1f)) {
                                    Text(if (mode == "MENU") "Choice Menu" else "Direct Dial")
                                }
                            }
                        }
                    }
                    Text(
                        if (emergencyMode == "MENU") "Opens a menu with Ambulance, Police, and Fire." else "Dials the primary emergency contact immediately.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(EasyUiSpacing.md), verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                    Text("Add Shortcut", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it; errorMessage = null },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it; errorMessage = null },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = {
                            errorMessage = onEdit(null, displayName, phoneNumber, null)
                            if (errorMessage == null) {
                                displayName = ""
                                phoneNumber = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add to Home")
                    }
                }
            }

            Text("Current Shortcuts", style = MaterialTheme.typography.titleMedium)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs), modifier = Modifier.weight(1f)) {
                items(tiles) { tile ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(EasyUiSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tile.title, style = MaterialTheme.typography.bodyLarge)
                                Text(tile.phoneNumber ?: "", style = MaterialTheme.typography.bodyMedium)
                            }
                            Row {
                                OutlinedButton(onClick = { onRemove(tile.id) }) {
                                    Text("Remove")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewConfirmScreen(
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    readability: String,
    pageCount: Int,
    allowedAppCount: Int,
    emergencyMode: String,
    layoutLocked: Boolean,
    hasPin: Boolean
) {
    WizardShell(
        title = "Review Setup",
        subtitle = "Double check your choices before finishing. If everything looks good, tap 'Looks Good' to start using EasyUI.",
        onNext = onConfirm,
        onBack = onBack,
        nextLabel = "Looks Good",
        currentStep = 9
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
            item {
                ReviewCard(title = "Readability", value = readability)
            }
            item {
                ReviewCard(title = "Home Pages", value = "$pageCount page(s) configured")
            }
            item {
                ReviewCard(title = "Emergency Mode", value = if (emergencyMode == "SOS") "Direct Dial" else "Choice Menu")
            }
            item {
                ReviewCard(title = "Home Apps", value = "$allowedAppCount apps placed on home")
            }
            item {
                ReviewCard(
                    title = "Security",
                    value = (if (hasPin) "PIN set" else "No PIN") + (if (layoutLocked) ", Layout locked" else ", Layout open")
                )
            }
            item {
                Spacer(modifier = Modifier.height(EasyUiSpacing.md))
            }
        }
    }
}

@Composable
private fun ReviewCard(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(EasyUiSpacing.md)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun CompletionScreen(
    onFinish: () -> Unit
) {
    WizardShell(
        title = "Setup Complete",
        subtitle = "EasyUI is now ready for use. You've made this phone safer and easier for your senior.",
        onNext = onFinish,
        nextLabel = "Go to Home",
        showProgress = false
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.lg)
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )
            Text(
                "Setup data is stored locally. Remember your caregiver PIN if you set one.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TrustBullet(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyUiSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
