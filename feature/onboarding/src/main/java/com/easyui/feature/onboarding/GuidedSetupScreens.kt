package com.easyui.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Surface
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.sp
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.model.OptionalPermission
import com.easyui.core.domain.model.SetupProtectionLevel
import com.easyui.core.domain.model.VisualTheme
import com.easyui.core.ui.components.ThemeSelector
import com.easyui.core.ui.components.ReadabilityPresetSelector
import com.easyui.core.ui.components.AppSelectionGrid
import com.easyui.core.ui.components.WizardScrollMode
import com.easyui.core.ui.components.WizardShell

import com.easyui.core.ui.components.WizardShell
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun WelcomeScreen(
    onNext: () -> Unit
) {
    WizardShell(
        modifier = Modifier.testTag("guided_setup_welcome"),
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
fun ProtectionOptionsScreen(
    currentStep: Int,
    totalSteps: Int,
    current: SetupProtectionLevel,
    onSelect: (SetupProtectionLevel) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    WizardShell(
        modifier = Modifier.testTag("guided_setup_protection_options"),
        title = "Choose protection level",
        subtitle = "Pick how strongly EasyUI should protect setup and home layout from accidental changes.",
        onNext = onNext,
        onBack = onBack,
        currentStep = currentStep,
        totalSteps = totalSteps,
        nextLabel = "Continue",
    ) {
        Text(
            text = "EasyUI protects its own layout and caregiver settings. Android system security and screen lock remain controlled by the phone settings.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(EasyUiSpacing.sm))

        ProtectionChoiceCard(
            title = "Recommended",
            subtitle = "Caregiver PIN required for editing. Layout locks after setup. Hidden caregiver entry stays available. This is the safest choice for most seniors.",
            selected = current == SetupProtectionLevel.RECOMMENDED,
            onClick = { onSelect(SetupProtectionLevel.RECOMMENDED) },
        )
        ProtectionChoiceCard(
            title = "Flexible",
            subtitle = "Caregiver PIN enabled, but layout can remain unlocked initially. It is easier to manage, but less protected from accidental changes.",
            selected = current == SetupProtectionLevel.FLEXIBLE,
            onClick = { onSelect(SetupProtectionLevel.FLEXIBLE) },
        )
        ProtectionChoiceCard(
            title = "Simple",
            subtitle = "Fastest setup with fewer caregiver protections. Layout can be changed more easily by accident.",
            selected = current == SetupProtectionLevel.SIMPLE,
            onClick = { onSelect(SetupProtectionLevel.SIMPLE) },
        )
    }
}

@Composable
private fun ProtectionChoiceCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = if (selected) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    }
    val border = if (selected) {
        androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        null
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("protection_choice_${title.lowercase().replace(' ', '_')}"),
        colors = colors,
        border = border,
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(EasyUiSpacing.md), verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ThemePickerScreen(
    currentStep: Int,
    totalSteps: Int,
    visualTheme: VisualTheme,
    accessibilityMode: AccessibilityMode,
    onThemeSelected: (VisualTheme, AccessibilityMode) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    WizardShell(
        modifier = Modifier.testTag("guided_setup_theme_picker"),
        title = "Choose display style",
        subtitle = "Pick a style that stays readable in everyday lighting.",
        onNext = onNext,
        onBack = onBack,
        currentStep = currentStep,
        totalSteps = totalSteps,
        nextLabel = "Continue",
    ) {
        ThemeSelector(
            visualTheme = visualTheme,
            accessibilityMode = accessibilityMode,
            onThemeSelected = onThemeSelected,
        )
    }
}


@Composable
fun LauncherActivationScreen(
    currentStep: Int,
    totalSteps: Int,
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
        currentStep = currentStep,
        totalSteps = totalSteps,
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
    currentStep: Int,
    totalSteps: Int,
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
        currentStep = currentStep,
        totalSteps = totalSteps,
        scrollMode = WizardScrollMode.ParentScroll
    ) {
        ReadabilityPresetSelector(
            currentPreset = currentPreset,
            onPresetSelected = onPresetSelected,
        )
    }
}

@Composable
fun HomeLayoutSetupScreen(
    currentStep: Int,
    totalSteps: Int,
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
        currentStep = currentStep,
        totalSteps = totalSteps,
        scrollMode = WizardScrollMode.ParentScroll
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
            Text("Senior Home Preview", style = MaterialTheme.typography.titleMedium)
            // Compact Preview: Show only the selected page preview or dots
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(EasyUiSpacing.md), horizontalAlignment = Alignment.CenterHorizontally) {
                    val mockTiles = listOf("Phone", "Messages", "Contacts", "Photos", "Camera", "Emergency")
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Page 1 (Essentials)", style = MaterialTheme.typography.labelMedium)
                        repeat(3) { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(0.6f), // Make it smaller
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(2) { col ->
                                    val index = row * 2 + col
                                    Card(
                                        modifier = Modifier.weight(1f).aspectRatio(1.5f),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Text(mockTiles.getOrElse(index) { "" }, style = MaterialTheme.typography.labelSmall, fontSize = 8.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (homePageCount > 1) {
                        Spacer(modifier = Modifier.height(EasyUiSpacing.sm))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(homePageCount) { i ->
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (i == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                            }
                        }
                        Text("+ ${homePageCount - 1} extra page(s) enabled", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun AllowedAppsSetupScreen(
    currentStep: Int,
    totalSteps: Int,
    pageCount: Int,
    pages: List<List<HomeTile?>>,
    installedApps: List<InstalledApp>,
    assignedAppPackages: Set<String>,
    onAssignApp: (String, Int) -> Unit,
    onRemoveApp: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    WizardShell(
        title = "Apps on Home",
        subtitle = "Tap an empty slot on a page to pick an app for it, or tap a placed app to remove it.",
        onNext = onNext,
        onBack = onBack,
        currentStep = currentStep,
        totalSteps = totalSteps,
        scrollMode = WizardScrollMode.ParentScroll
    ) {
        AppSelectionGrid(
            pageCount = pageCount,
            pages = pages,
            installedApps = installedApps,
            assignedAppPackages = assignedAppPackages,
            onAssignApp = onAssignApp,
            onRemoveApp = onRemoveApp,
        )
    }
}

@Composable
fun SecuritySetupScreen(
    currentStep: Int,
    totalSteps: Int,
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
        currentStep = currentStep,
        totalSteps = totalSteps,
        nextLabel = "Continue"
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
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            OutlinedTextField(
                value = confirmPin,
                onValueChange = onConfirmPinChange,
                label = { Text("Confirm PIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
            
            Text(
                "If you skip this, caregiver settings will be open to anyone. You can always set a PIN later.",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                androidx.compose.material3.TextButton(onClick = onSkip) {
                    Text("Skip PIN for now")
                }
            }
        }
    }
}

@Composable
fun DeviceSupportScreen(
    currentStep: Int,
    totalSteps: Int,
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
        currentStep = currentStep,
        totalSteps = totalSteps,
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
    currentStep: Int,
    totalSteps: Int,
    tiles: List<HomeTile>,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onEdit: (String?, String, String, String?) -> String?,
    onRemove: (String) -> Unit,
    emergencyMode: String,
    onEmergencyModeChange: (String) -> Unit,
    emergencyPhoneNumber: String,
    onEmergencyPhoneNumberChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var displayName by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val hasNoContacts = tiles.isEmpty()
    val isEmergencyNumberValid = emergencyPhoneNumber.length >= 3

    WizardShell(
        title = "Call Shortcuts & Emergency",
        subtitle = if (hasNoContacts) {
            "Add the people the senior calls most often and decide what the 'Emergency' tile does. At least one shortcut is recommended."
        } else {
            "Add the people the senior calls most often and decide what the 'Emergency' tile does."
        },
        onNext = onNext,
        onBack = onBack,
        currentStep = currentStep,
        totalSteps = totalSteps,
        scrollMode = WizardScrollMode.ParentScroll
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(EasyUiSpacing.md), verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                    Text("Emergency Button Mode", style = MaterialTheme.typography.titleMedium)
                    
                    Text("Primary Emergency Number", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = emergencyPhoneNumber,
                        onValueChange = onEmergencyPhoneNumberChange,
                        label = { Text("e.g. 911 or a family member") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(EasyUiSpacing.xs))

                    Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                        val modes = listOf("MENU", "SOS")
                        modes.forEach { mode ->
                            val selected = mode == emergencyMode
                            val enabled = mode == "MENU" || isEmergencyNumberValid
                            if (selected) {
                                Button(
                                    onClick = { onEmergencyModeChange(mode) }, 
                                    modifier = Modifier.weight(1f),
                                    enabled = enabled
                                ) {
                                    Text(if (mode == "MENU") "Choice Menu" else "Direct Dial")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onEmergencyModeChange(mode) }, 
                                    modifier = Modifier.weight(1f),
                                    enabled = enabled
                                ) {
                                    Text(if (mode == "MENU") "Choice Menu" else "Direct Dial")
                                }
                            }
                        }
                    }
                    Text(
                        if (emergencyMode == "MENU") "Opens a menu with Ambulance, Police, and Fire." else "Dials $emergencyPhoneNumber immediately when tapped.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (emergencyMode == "SOS" && !isEmergencyNumberValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    if (!isEmergencyNumberValid && emergencyMode == "SOS") {
                        Text("Enter a valid number to use Direct Dial.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
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
            Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                tiles.forEach { tile ->
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
    currentStep: Int,
    totalSteps: Int,
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
        currentStep = currentStep,
        totalSteps = totalSteps,
        scrollMode = WizardScrollMode.ParentScroll
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
            ReviewCard(title = "Readability", value = readability)
            ReviewCard(title = "Home Pages", value = "$pageCount page(s) configured")
            ReviewCard(title = "Emergency Mode", value = if (emergencyMode == "SOS") "Direct Dial" else "Choice Menu")
            ReviewCard(
                title = "Home Apps", 
                value = "Essential tiles: 6 enabled\nExtra apps: $allowedAppCount added"
            )
            ReviewCard(
                title = "Security",
                value = (if (hasPin) "PIN configured" else "No PIN") + (if (layoutLocked) "\nLayout locked" else "\nLayout open")
            )
            Spacer(modifier = Modifier.height(EasyUiSpacing.md))
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
            
            Spacer(modifier = Modifier.height(EasyUiSpacing.md))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(EasyUiSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)
                ) {
                    Text(
                        text = "Change settings later",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "You can change these settings later.\n\nOn the home screen, tap the clock 5 times to open Caregiver Settings. If you set a PIN, you'll need it before making changes.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
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
