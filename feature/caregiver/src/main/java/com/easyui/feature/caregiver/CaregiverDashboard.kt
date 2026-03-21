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

@Composable
internal fun CaregiverDashboardScreen(
    protectionEnabled: Boolean,
    layoutLocked: Boolean,
    hasPinConfigured: Boolean,
    currentPageCount: Int,
    showBatteryInfo: Boolean,
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
    modifier: Modifier = Modifier,
) {
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    val securityAction = if (hasPinConfigured) onChangePin else onSetupPin

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
                    DashboardHeaderCard(
                        protectionEnabled = protectionEnabled,
                        layoutLocked = layoutLocked,
                        hasPinConfigured = hasPinConfigured,
                    )
                }
                item {
                    DashboardCardRow(
                        left = DashboardCardModel(
                            title = "Home Layout",
                            subtitle = "$currentPageCount page${if (currentPageCount == 1) "" else "s"} with the fixed senior 2x3 home.",
                            icon = Icons.Filled.DashboardCustomize,
                            accent = CaregiverDashboardTokens.accentPrimary,
                            status = "Pages $currentPageCount",
                            onClick = onOpenLayoutPages,
                        ),
                        right = DashboardCardModel(
                            title = "Contacts & Emergency",
                            subtitle = "$favoriteContactCount favorites, ${sosStatusLabel(sosNumberCount)}, emergency ready.",
                            icon = Icons.Filled.Favorite,
                            accent = CaregiverDashboardTokens.accentWarning,
                            status = if (favoriteContactCount > 0) "Ready" else "Needs setup",
                            onClick = onManageFavoriteContacts,
                        ),
                    )
                }
                item {
                    DashboardCardRow(
                        left = DashboardCardModel(
                            title = "Allowed Apps",
                            subtitle = "$allowedAppCount home apps placed, $hiddenAppCount hidden from EasyUI.",
                            icon = Icons.Filled.Widgets,
                            accent = CaregiverDashboardTokens.accentInfo,
                            status = if (allowedAppCount > 0) "Configured" else "Review",
                            onClick = onOpenAllowedApps,
                        ),
                        right = DashboardCardModel(
                            title = "Security & Lock",
                            subtitle = securitySummary(
                                hasPinConfigured = hasPinConfigured,
                                protectionEnabled = protectionEnabled,
                                layoutLocked = layoutLocked,
                                easyUiLockEnabled = easyUiLockEnabled,
                            ),
                            icon = Icons.Filled.Security,
                            accent = CaregiverDashboardTokens.accentPrimary,
                            status = if (hasPinConfigured) "Protected" else "Needs PIN",
                            onClick = securityAction,
                        ),
                    )
                }
                item {
                    SetupStatusCard(
                        protectionEnabled = protectionEnabled,
                        layoutLocked = layoutLocked,
                        showBatteryInfo = showBatteryInfo,
                        healthInfoConfigured = healthInfoConfigured,
                        emergencyPhoneNumber = emergencyPhoneNumber,
                        sosNumberCount = sosNumberCount,
                        easyUiLockEnabled = easyUiLockEnabled,
                        easyUiLockTimeoutSeconds = easyUiLockTimeoutSeconds,
                    )
                }
                item {
                    QuickTogglesCard(
                        protectionEnabled = protectionEnabled,
                        layoutLocked = layoutLocked,
                        showBatteryInfo = showBatteryInfo,
                        easyUiLockEnabled = easyUiLockEnabled,
                        easyUiLockTimeoutSeconds = easyUiLockTimeoutSeconds,
                        onToggleProtection = onToggleProtection,
                        onToggleLayoutLock = onToggleLayoutLock,
                        onToggleBatteryInfo = onToggleBatteryInfo,
                    )
                }
                item {
                    SupportToolsCard(
                        favoriteContactCount = favoriteContactCount,
                        hiddenAppCount = hiddenAppCount,
                        healthInfoConfigured = healthInfoConfigured,
                        sosNumberCount = sosNumberCount,
                        onManageFavoriteContacts = onManageFavoriteContacts,
                        onOpenEmergencySettings = onOpenEmergencySettings,
                        onOpenHealthInfo = onOpenHealthInfo,
                        onOpenBackupRestore = onOpenBackupRestore,
                        onOpenHiddenApps = onOpenHiddenApps,
                    )
                }
                item {
                    QuickActionsCard(
                        onOpenEmergencySettings = onOpenEmergencySettings,
                        onFinishSetup = onFinishSetup,
                        onOpenBackupRestore = onOpenBackupRestore,
                        onResetLauncher = onResetLauncher,
                    )
                }
            }
        }
    }
}

private data class DashboardCardModel(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val status: String,
    val onClick: () -> Unit,
)

@Composable
private fun DashboardHeaderCard(
    protectionEnabled: Boolean,
    layoutLocked: Boolean,
    hasPinConfigured: Boolean,
) {
    DashboardSurface(
        elevated = true,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(CaregiverDashboardTokens.sectionGap),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Caregiver",
                    color = CaregiverDashboardTokens.textPrimary,
                    fontSize = CaregiverDashboardTokens.titleSize,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Manage launcher, safety, and home setup with the same calm EasyUI design language.",
                    color = CaregiverDashboardTokens.textSecondary,
                    fontSize = CaregiverDashboardTokens.bodySize,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(
                    label = if (hasPinConfigured && protectionEnabled) "Protected" else if (hasPinConfigured) "PIN Ready" else "Needs PIN",
                    accent = when {
                        hasPinConfigured && protectionEnabled -> CaregiverDashboardTokens.accentSuccess
                        hasPinConfigured -> CaregiverDashboardTokens.accentPrimary
                        else -> CaregiverDashboardTokens.accentWarning
                    },
                )
                StatusChip(
                    label = if (layoutLocked) "Home Locked" else "Review Layout",
                    accent = if (layoutLocked) CaregiverDashboardTokens.accentPrimary else CaregiverDashboardTokens.accentWarning,
                )
            }
        }
    }
}

@Composable
private fun DashboardCardRow(
    left: DashboardCardModel,
    right: DashboardCardModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CaregiverDashboardTokens.sectionGap),
    ) {
        DashboardSectionCard(model = left, modifier = Modifier.weight(1f))
        DashboardSectionCard(model = right, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DashboardSectionCard(
    model: DashboardCardModel,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = model.onClick,
        modifier = modifier.heightIn(min = CaregiverDashboardTokens.dashboardCardMinHeight),
        shape = RoundedCornerShape(CaregiverDashboardTokens.radius),
        colors = CardDefaults.cardColors(containerColor = CaregiverDashboardTokens.surfacePrimary),
        border = BorderStroke(1.dp, CaregiverDashboardTokens.outlineSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CaregiverDashboardTokens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(CaregiverDashboardTokens.iconContainerSize)
                        .background(model.accent.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = model.icon,
                        contentDescription = null,
                        tint = model.accent,
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = CaregiverDashboardTokens.textTertiary,
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = model.title,
                    color = CaregiverDashboardTokens.textPrimary,
                    fontSize = CaregiverDashboardTokens.sectionTitleSize,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = model.subtitle,
                    color = CaregiverDashboardTokens.textSecondary,
                    fontSize = CaregiverDashboardTokens.bodySize,
                )
            }
            StatusChip(label = model.status, accent = model.accent)
        }
    }
}

@Composable
private fun SetupStatusCard(
    protectionEnabled: Boolean,
    layoutLocked: Boolean,
    showBatteryInfo: Boolean,
    healthInfoConfigured: Boolean,
    emergencyPhoneNumber: String,
    sosNumberCount: Int,
    easyUiLockEnabled: Boolean,
    easyUiLockTimeoutSeconds: Int,
) {
    DashboardSurface(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                title = "Setup Status",
                subtitle = "Color is reserved for meaning here: what is protected, what is ready, and what still needs attention.",
            )
            DashboardStatusRow(
                label = "Caregiver protection",
                value = if (protectionEnabled) "PIN required" else "Optional",
                chipLabel = if (protectionEnabled) "Enabled" else "Review",
                accent = if (protectionEnabled) CaregiverDashboardTokens.accentSuccess else CaregiverDashboardTokens.accentWarning,
            )
            DashboardStatusRow(
                label = "Senior home layout",
                value = if (layoutLocked) "Locked against accidental changes" else "Unlocked for review",
                chipLabel = if (layoutLocked) "Locked" else "Open",
                accent = if (layoutLocked) CaregiverDashboardTokens.accentPrimary else CaregiverDashboardTokens.accentWarning,
            )
            DashboardStatusRow(
                label = "Emergency setup",
                value = "Primary number: ${emergencyPhoneNumber.ifBlank { "911" }}",
                chipLabel = if (sosNumberCount > 0) "SOS Ready" else "Needs setup",
                accent = if (sosNumberCount > 0) CaregiverDashboardTokens.accentDanger else CaregiverDashboardTokens.accentWarning,
            )
            DashboardStatusRow(
                label = "Health info",
                value = if (healthInfoConfigured) "Medical shortcut has saved details." else "No health details saved yet.",
                chipLabel = if (healthInfoConfigured) "Saved" else "Review",
                accent = if (healthInfoConfigured) CaregiverDashboardTokens.accentSuccess else CaregiverDashboardTokens.accentWarning,
            )
            DashboardStatusRow(
                label = "Home battery info",
                value = if (showBatteryInfo) "Visible on the senior home." else "Hidden for a cleaner home.",
                chipLabel = if (showBatteryInfo) "Shown" else "Hidden",
                accent = CaregiverDashboardTokens.accentInfo,
            )
            DashboardStatusRow(
                label = "EasyUI lock overlay",
                value = if (easyUiLockEnabled) "Active after ${easyUiLockTimeoutSeconds}s of inactivity." else "Not enabled.",
                chipLabel = if (easyUiLockEnabled) "Enabled" else "Off",
                accent = if (easyUiLockEnabled) CaregiverDashboardTokens.accentSuccess else CaregiverDashboardTokens.accentPrimary,
            )
        }
    }
}

@Composable
private fun QuickTogglesCard(
    protectionEnabled: Boolean,
    layoutLocked: Boolean,
    showBatteryInfo: Boolean,
    easyUiLockEnabled: Boolean,
    easyUiLockTimeoutSeconds: Int,
    onToggleProtection: () -> Unit,
    onToggleLayoutLock: () -> Unit,
    onToggleBatteryInfo: (Boolean) -> Unit,
) {
    DashboardSurface(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                title = "Quick Toggles",
                subtitle = "Use restrained controls for common configuration changes.",
            )
            DashboardToggleRow(
                title = "Require caregiver PIN",
                subtitle = "Gate caregiver entry behind the configured PIN.",
                checked = protectionEnabled,
                onCheckedChange = { onToggleProtection() },
            )
            DashboardToggleRow(
                title = "Lock launcher layout",
                subtitle = "Prevent casual edits and movement on the senior home.",
                checked = layoutLocked,
                onCheckedChange = { onToggleLayoutLock() },
            )
            DashboardToggleRow(
                title = "Show battery on senior home",
                subtitle = "Only show this if it reduces support calls.",
                checked = showBatteryInfo,
                onCheckedChange = onToggleBatteryInfo,
            )
            DashboardStatusRow(
                label = "EasyUI lock overlay",
                value = if (easyUiLockEnabled) "Currently enabled with a ${easyUiLockTimeoutSeconds}s timeout." else "Currently disabled.",
                chipLabel = if (easyUiLockEnabled) "Locked" else "Off",
                accent = if (easyUiLockEnabled) CaregiverDashboardTokens.accentPrimary else CaregiverDashboardTokens.accentInfo,
            )
        }
    }
}

@Composable
private fun SupportToolsCard(
    favoriteContactCount: Int,
    hiddenAppCount: Int,
    healthInfoConfigured: Boolean,
    sosNumberCount: Int,
    onManageFavoriteContacts: () -> Unit,
    onOpenEmergencySettings: () -> Unit,
    onOpenHealthInfo: () -> Unit,
    onOpenBackupRestore: () -> Unit,
    onOpenHiddenApps: () -> Unit,
) {
    DashboardSurface(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                title = "Contacts, Emergency, and Support",
                subtitle = "Keep related caregiver tasks grouped instead of scattering them across bright tiles.",
            )
            DashboardActionRow(
                title = "Emergency Settings",
                subtitle = "Emergency number, SOS numbers, and lock overlay behavior.",
                detail = if (sosNumberCount > 0) "$sosNumberCount SOS numbers" else "Needs review",
                icon = Icons.Filled.Warning,
                accent = CaregiverDashboardTokens.accentDanger,
                onClick = onOpenEmergencySettings,
            )
            DashboardActionRow(
                title = "Health Info",
                subtitle = "Manage the senior medical shortcut and saved details.",
                detail = if (healthInfoConfigured) "Saved" else "Not saved",
                icon = Icons.Filled.MedicalServices,
                accent = CaregiverDashboardTokens.accentInfo,
                onClick = onOpenHealthInfo,
            )
            DashboardActionRow(
                title = "Hidden Apps",
                subtitle = "Control which apps disappear from EasyUI surfaces and search.",
                detail = "$hiddenAppCount hidden",
                icon = Icons.Filled.VisibilityOff,
                accent = CaregiverDashboardTokens.accentPrimary,
                onClick = onOpenHiddenApps,
            )
            DashboardActionRow(
                title = "Backup & Restore",
                subtitle = "Export or restore the local caregiver configuration safely.",
                detail = "Local only",
                icon = Icons.Filled.Backup,
                accent = CaregiverDashboardTokens.accentSuccess,
                onClick = onOpenBackupRestore,
            )
            DashboardActionRow(
                title = "Favorite Contacts",
                subtitle = "Manage the people the senior can reach quickly.",
                detail = "$favoriteContactCount favorites",
                icon = Icons.Filled.HealthAndSafety,
                accent = CaregiverDashboardTokens.accentWarning,
                onClick = onManageFavoriteContacts,
            )
        }
    }
}

@Composable
private fun QuickActionsCard(
    onOpenEmergencySettings: () -> Unit,
    onFinishSetup: () -> Unit,
    onOpenBackupRestore: () -> Unit,
    onResetLauncher: () -> Unit,
) {
    DashboardSurface(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                title = "Quick Actions",
                subtitle = "Use strong color only for priorities and destructive actions.",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CaregiverDashboardTokens.sectionGap),
            ) {
                DashboardPrimaryButton(
                    text = "Back to Home",
                    onClick = onFinishSetup,
                    modifier = Modifier.weight(1f),
                )
                DashboardSecondaryButton(
                    text = "Emergency",
                    onClick = onOpenEmergencySettings,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CaregiverDashboardTokens.sectionGap),
            ) {
                DashboardSecondaryButton(
                    text = "Backup",
                    onClick = onOpenBackupRestore,
                    modifier = Modifier.weight(1f),
                )
                DashboardDangerButton(
                    text = "Reset",
                    onClick = onResetLauncher,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DashboardSurface(
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
private fun SectionHeader(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            color = CaregiverDashboardTokens.textPrimary,
            fontSize = CaregiverDashboardTokens.sectionTitleSize,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = subtitle,
            color = CaregiverDashboardTokens.textSecondary,
            fontSize = CaregiverDashboardTokens.bodySize,
        )
    }
}

@Composable
private fun DashboardStatusRow(
    label: String,
    value: String,
    chipLabel: String,
    accent: Color,
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
private fun StatusChip(
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

@Preview(showBackground = true, backgroundColor = 0xFF0D1238)
@Composable
private fun CaregiverDashboardPreview() {
    CaregiverDashboardScreen(
        protectionEnabled = true,
        layoutLocked = true,
        hasPinConfigured = true,
        currentPageCount = 2,
        showBatteryInfo = true,
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
        onToggleBatteryInfo = {},
        onOpenLayoutPages = {},
        onOpenAllowedApps = {},
        onManageFavoriteContacts = {},
        onOpenEmergencySettings = {},
        onOpenHealthInfo = {},
        onOpenBackupRestore = {},
        onOpenHiddenApps = {},
        onFinishSetup = {},
        onResetLauncher = {},
    )
}
