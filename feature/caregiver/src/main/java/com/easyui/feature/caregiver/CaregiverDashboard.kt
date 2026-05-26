package com.easyui.feature.caregiver

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun CaregiverDashboardScreen(
    protectionEnabled: Boolean,
    layoutLocked: Boolean,
    hasPinConfigured: Boolean,
    allAppsVisible: Boolean,
    currentPageCount: Int,
    showBatteryInfo: Boolean,
    skinConfig: com.easyui.core.domain.model.SkinConfig,
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
    onToggleAllAppsVisible: (Boolean) -> Unit,
    onToggleBatteryInfo: (Boolean) -> Unit,
    onOpenLayoutPages: () -> Unit,
    onOpenReadabilityPreset: () -> Unit,
    onOpenThemeSelection: () -> Unit,
    onOpenAllowedApps: () -> Unit,
    onManageFavoriteContacts: () -> Unit,
    onOpenEmergencySettings: () -> Unit,
    onOpenHealthInfo: () -> Unit,
    onOpenBackupRestore: () -> Unit,
    onOpenHiddenApps: () -> Unit,
    onOpenGuardianSettings: () -> Unit,
    onOpenLinkedDevices: () -> Unit,
    onShareMyStatus: () -> Unit,
    onFinishSetup: () -> Unit,
    onResetLauncher: () -> Unit,
    onRedoGuidedSetup: () -> Unit,
    setupCompleteness: com.easyui.core.domain.model.SetupCompleteness = com.easyui.core.domain.model.SetupCompleteness(emptyList(), 0f),
    modifier: Modifier = Modifier,
) {
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = CaregiverDashboardTokens.backgroundBottom,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CaregiverDashboardTokens.backgroundBrush)
                .testTag("caregiver_tools_screen"),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = CaregiverDashboardTokens.pageHorizontalPadding,
                        end = CaregiverDashboardTokens.pageHorizontalPadding,
                        top = safeDrawingPadding.calculateTopPadding() + CaregiverDashboardTokens.pageHorizontalPadding,
                        bottom = safeDrawingPadding.calculateBottomPadding() + CaregiverDashboardTokens.pageHorizontalPadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(CaregiverDashboardTokens.sectionGap),
            ) {
                item {
                    Text("Caregiver Settings", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = CaregiverDashboardTokens.textPrimary)
                }

                item {
                    DashboardSurface(elevated = true) {
                        SectionHeader(title = "Setup Status", subtitle = "Verify if EasyUI is correctly configured.")
                        setupCompleteness.items.forEach { item ->
                            DashboardStatusRow(
                                label = item.label,
                                value = if (item.isComplete) "Complete" else if (item.isRequired) "Action needed" else "Optional",
                                chipLabel = if (item.isComplete) "Done" else "Fix",
                                accent = if (item.isComplete) Color(0xFF2D6A4F) else if (item.isRequired) CaregiverDashboardTokens.accentDanger else CaregiverDashboardTokens.accentWarning,
                                onClick = if (!item.isComplete) {
                                    when (item.id) {
                                        "default_launcher" -> onRedoGuidedSetup
                                        "caregiver_pin" -> onSetupPin
                                        "layout_locked" -> onToggleLayoutLock
                                        "emergency_contact" -> onOpenEmergencySettings
                                        "favorite_contacts" -> onManageFavoriteContacts
                                        "allowed_apps" -> onOpenAllowedApps
                                        else -> null
                                    }
                                } else null
                            )
                        }
                    }
                }
                
                item {
                    DashboardSurface(elevated = true) {
                        SectionHeader(title = "Appearance & Layout", subtitle = "Manage pages, themes, and basic layout.")
                        DashboardActionRow(
                            title = "Readability",
                            subtitle = readabilitySummary(skinConfig),
                            detail = "Edit",
                            icon = Icons.Filled.DashboardCustomize,
                            accent = CaregiverDashboardTokens.accentPrimary,
                            onClick = onOpenReadabilityPreset,
                        )
                        DashboardActionRow(
                            title = "Visual Theme",
                            subtitle = themeSummary(skinConfig),
                            detail = "Edit",
                            icon = Icons.Filled.DashboardCustomize,
                            accent = CaregiverDashboardTokens.accentPrimary,
                            onClick = onOpenThemeSelection
                        )
                        DashboardActionRow(
                            title = "Theme & Pages",
                            subtitle = "$currentPageCount page(s)",
                            detail = "Edit",
                            icon = Icons.Filled.DashboardCustomize,
                            accent = CaregiverDashboardTokens.accentPrimary,
                            onClick = onOpenLayoutPages
                        )
                    }
                }
                
                item {
                    DashboardSurface(elevated = true) {
                        SectionHeader(title = "Home Apps", subtitle = "Choose which apps are visible on home pages.")
                        DashboardActionRow(
                            title = "Allowed Apps",
                            subtitle = "$allowedAppCount apps placed",
                            detail = "Edit",
                            icon = Icons.Filled.Widgets,
                            accent = CaregiverDashboardTokens.accentInfo,
                            onClick = onOpenAllowedApps
                        )
                        DashboardActionRow(
                            title = "Hidden Apps",
                            subtitle = "$hiddenAppCount hidden apps",
                            detail = "Manage",
                            icon = Icons.Filled.VisibilityOff,
                            accent = CaregiverDashboardTokens.textTertiary,
                            onClick = onOpenHiddenApps
                        )
                    }
                }
                
                item {
                    DashboardSurface(elevated = true) {
                        SectionHeader(title = "Contacts & Emergency", subtitle = "Favorites, emergency numbers, and health info.")
                        DashboardActionRow(
                            title = "Call Shortcuts",
                            subtitle = "$favoriteContactCount favorites",
                            detail = "Edit",
                            icon = Icons.Filled.Favorite,
                            accent = CaregiverDashboardTokens.accentWarning,
                            onClick = onManageFavoriteContacts
                        )
                        DashboardActionRow(
                            title = "Emergency Settings",
                            subtitle = "Emergency button and SOS",
                            detail = "Edit",
                            icon = Icons.Filled.Warning,
                            accent = CaregiverDashboardTokens.accentDanger,
                            onClick = onOpenEmergencySettings
                        )
                        DashboardActionRow(
                            title = "Health Information",
                            subtitle = if (healthInfoConfigured) "Configured" else "Not setup",
                            detail = "Edit",
                            icon = Icons.Filled.MedicalServices,
                            accent = Color(0xFF2D6A4F),
                            onClick = onOpenHealthInfo
                        )
                    }
                }
                
                item {
                    DashboardSurface(elevated = true) {
                        SectionHeader(title = "Security & Protection", subtitle = "Lock down the home screen or require PINs.")
                        DashboardToggleRow(
                            title = "Layout Lock",
                            subtitle = "Prevent accidental moves or deletes on home screen",
                            checked = layoutLocked,
                            onCheckedChange = { onToggleLayoutLock() }
                        )
                        DashboardToggleRow(
                            title = "Show 'All Apps'",
                            subtitle = "Allow the senior to see and search all installed apps",
                            checked = allAppsVisible,
                            onCheckedChange = onToggleAllAppsVisible
                        )
                        DashboardActionRow(
                            title = "Caregiver PIN",
                            subtitle = if (hasPinConfigured) "PIN configured" else "No PIN set",
                            detail = if (hasPinConfigured) "Change" else "Setup",
                            icon = Icons.Filled.Security,
                            accent = CaregiverDashboardTokens.accentPrimary,
                            onClick = if (hasPinConfigured) onChangePin else onSetupPin
                        )
                    }
                }
                
                item {
                    DashboardSurface(elevated = true) {
                        SectionHeader(title = "Device & Backup", subtitle = "Battery, backups, and reset options.")
                        DashboardToggleRow(
                            title = "Show Battery Info",
                            subtitle = "Display battery percentage on home screen",
                            checked = showBatteryInfo,
                            onCheckedChange = { onToggleBatteryInfo(it) }
                        )
                        DashboardActionRow(
                            title = "Backup & Restore",
                            subtitle = "Save layout and settings to file",
                            detail = "Open",
                            icon = Icons.Filled.Backup,
                            accent = CaregiverDashboardTokens.textSecondary,
                            onClick = onOpenBackupRestore
                        )
                        DashboardActionRow(
                            title = "Guardian Checks",
                            subtitle = "Local monitoring & phone health",
                            detail = "Manage",
                            icon = Icons.Filled.HealthAndSafety,
                            accent = Color(0xFF2D6A4F),
                            onClick = onOpenGuardianSettings
                        )
                        DashboardActionRow(
                            title = "Linked Phones",
                            subtitle = "View status of other EasyUI devices",
                            detail = "Open",
                            icon = Icons.Filled.Phonelink,
                            accent = CaregiverDashboardTokens.accentInfo,
                            onClick = onOpenLinkedDevices
                        )
                        DashboardActionRow(
                            title = "Share My Status",
                            subtitle = "Send health link to another caregiver",
                            detail = "Share",
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            accent = CaregiverDashboardTokens.accentPrimary,
                            onClick = onShareMyStatus
                        )
                    }
                }
                
                item {
                    DashboardDangerButton(text = "Reset Launcher to Defaults", onClick = onResetLauncher, modifier = Modifier.fillMaxWidth())
                    OutlinedButton(onClick = onFinishSetup, modifier = Modifier.fillMaxWidth()) {
                        Text("Exit to Senior Home")
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSurface(
    modifier: Modifier = Modifier,
    elevated: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(CaregiverDashboardTokens.radius),
        colors = CardDefaults.cardColors(
            containerColor = if (elevated) {
                CaregiverDashboardTokens.surfaceElevated
            } else {
                CaregiverDashboardTokens.surfacePrimary
            },
        ),
        border = BorderStroke(1.dp, CaregiverDashboardTokens.outlineSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CaregiverDashboardTokens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
) {

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            color = CaregiverDashboardTokens.textPrimary,
            fontSize = CaregiverDashboardTokens.sectionTitleSize,
            fontWeight = FontWeight.SemiBold,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = CaregiverDashboardTokens.textSecondary,
                fontSize = CaregiverDashboardTokens.bodySize,
            )
        }
    }
}

@Composable
private fun DashboardStatusRow(
    label: String,
    value: String,
    chipLabel: String,
    accent: Color,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                color = CaregiverDashboardTokens.textPrimary,
                fontSize = CaregiverDashboardTokens.bodySize,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = value,
                color = CaregiverDashboardTokens.textTertiary,
                fontSize = CaregiverDashboardTokens.helperSize,
            )
        }
        StatusChip(label = chipLabel, accent = accent)
    }
}

@Composable
private fun DashboardToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                color = CaregiverDashboardTokens.textPrimary,
                fontSize = CaregiverDashboardTokens.bodySize,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = CaregiverDashboardTokens.textTertiary,
                fontSize = CaregiverDashboardTokens.helperSize,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CaregiverDashboardTokens.textPrimary,
                checkedTrackColor = CaregiverDashboardTokens.accentPrimary,
                uncheckedThumbColor = CaregiverDashboardTokens.textSecondary,
                uncheckedTrackColor = CaregiverDashboardTokens.surfaceSecondary,
                uncheckedBorderColor = CaregiverDashboardTokens.outlineSubtle,
            ),
        )
    }
}

@Composable
private fun DashboardActionRow(
    title: String,
    subtitle: String,
    detail: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(accent.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accent)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                color = CaregiverDashboardTokens.textPrimary,
                fontSize = CaregiverDashboardTokens.bodySize,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = CaregiverDashboardTokens.textTertiary,
                fontSize = CaregiverDashboardTokens.helperSize,
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = detail,
                color = accent,
                fontSize = CaregiverDashboardTokens.helperSize,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = CaregiverDashboardTokens.textTertiary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun StatusChip(
    label: String,
    accent: Color,
) {
    Box(
        modifier = Modifier
            .background(accent.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(
                horizontal = CaregiverDashboardTokens.chipHorizontalPadding,
                vertical = CaregiverDashboardTokens.chipVerticalPadding,
            ),
    ) {
        Text(
            text = label,
            color = accent,
            fontSize = CaregiverDashboardTokens.helperSize,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun DashboardPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(CaregiverDashboardTokens.buttonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = CaregiverDashboardTokens.accentPrimary,
            contentColor = CaregiverDashboardTokens.textPrimary,
        ),
    ) {
        Text(text)
    }
}

@Composable
private fun DashboardSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(CaregiverDashboardTokens.buttonRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CaregiverDashboardTokens.buttonSecondary,
            contentColor = CaregiverDashboardTokens.textPrimary,
        ),
        border = BorderStroke(1.dp, CaregiverDashboardTokens.outlineSubtle),
    ) {
        Text(text)
    }
}

@Composable
private fun DashboardDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(CaregiverDashboardTokens.buttonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = CaregiverDashboardTokens.accentDanger,
            contentColor = CaregiverDashboardTokens.textPrimary,
        ),
    ) {
        Text(text)
    }
}

private fun securitySummary(
    hasPinConfigured: Boolean,
    protectionEnabled: Boolean,
    layoutLocked: Boolean,
    easyUiLockEnabled: Boolean,
): String =
    buildList {
        if (hasPinConfigured) add("PIN ready") else add("PIN needed")
        if (protectionEnabled) add("protected")
        if (layoutLocked) add("layout locked")
        if (easyUiLockEnabled) add("overlay on")
    }.joinToString(", ")

private fun sosStatusLabel(sosNumberCount: Int): String =
    if (sosNumberCount > 0) "$sosNumberCount SOS numbers" else "SOS needs setup"

private fun themeSummary(skinConfig: com.easyui.core.domain.model.SkinConfig): String =
    when {
        skinConfig.accessibilityMode == com.easyui.core.domain.model.AccessibilityMode.HIGH_CONTRAST -> "High contrast"
        skinConfig.visualTheme == com.easyui.core.domain.model.VisualTheme.DARK_COMFORT -> "Dark comfort"
        skinConfig.visualTheme == com.easyui.core.domain.model.VisualTheme.LIGHT_PREMIUM -> "Light premium"
        else -> "Auto theme"
    }

private fun readabilitySummary(skinConfig: com.easyui.core.domain.model.SkinConfig): String =
    when (skinConfig.readabilityPreset) {
        com.easyui.core.domain.model.HomeReadabilityPreset.STANDARD -> "Standard"
        com.easyui.core.domain.model.HomeReadabilityPreset.LARGER_TEXT -> "Larger text"
        com.easyui.core.domain.model.HomeReadabilityPreset.LARGER_TILES -> "Larger tiles"
        com.easyui.core.domain.model.HomeReadabilityPreset.EXTRA_SIMPLE_SPACING -> "Extra simple spacing"
    }

@Preview(showBackground = true, backgroundColor = 0xFF0D1238)
@Composable
private fun CaregiverDashboardPreview() {
    CaregiverDashboardScreen(
        protectionEnabled = true,
        layoutLocked = true,
        hasPinConfigured = true,
        allAppsVisible = true,
        currentPageCount = 2,
        showBatteryInfo = true,
        skinConfig = com.easyui.core.domain.model.SkinConfig(),
        favoriteContactCount = 3,
        allowedAppCount = 6,
        hiddenAppCount = 2,
        healthInfoConfigured = true,
        emergencyPhoneNumber = "911",
        sosNumberCount = 2,
        easyUiLockEnabled = true,
        easyUiLockTimeoutSeconds = 60,
        onSetupPin = {},
        onChangePin = {},
        onToggleProtection = {},
        onToggleLayoutLock = {},
        onToggleAllAppsVisible = {},
        onToggleBatteryInfo = {},
        onOpenLayoutPages = {},
        onOpenReadabilityPreset = {},
        onOpenThemeSelection = {},
        onOpenAllowedApps = {},
        onManageFavoriteContacts = {},
        onOpenEmergencySettings = {},
        onOpenHealthInfo = {},
        onOpenBackupRestore = {},
        onOpenHiddenApps = {},
        onOpenGuardianSettings = {},
        onOpenLinkedDevices = {},
        onShareMyStatus = {},
        onFinishSetup = {},
        onResetLauncher = {},
        onRedoGuidedSetup = {},
    )
}
