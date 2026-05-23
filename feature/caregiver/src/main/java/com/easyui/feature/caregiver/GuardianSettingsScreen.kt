package com.easyui.feature.caregiver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun GuardianSettingsScreen(
    settings: LauncherSettings,
    onUpdateBatteryLowCheck: (Boolean) -> Unit,
    onUpdateBatteryLowThreshold: (Int) -> Unit,
    onUpdateBatteryCriticalThreshold: (Int) -> Unit,
    onUpdateInternetCheck: (Boolean) -> Unit,
    onUpdateNoInternetDelay: (Int) -> Unit,
    onUpdateDefaultLauncherCheck: (Boolean) -> Unit,
    onUpdateEmergencyContactCheck: (Boolean) -> Unit,
    onUpdateLayoutLockCheck: (Boolean) -> Unit,
    onUpdatePermissionCheck: (Boolean) -> Unit,
    onDone: () -> Unit,
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
                .testTag("guardian_settings_screen"),
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
                    Text("Guardian Checks", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = CaregiverDashboardTokens.textPrimary)
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(CaregiverDashboardTokens.sectionGap)) {
                        GuardianToggleRow(
                            title = "Battery Checks",
                            subtitle = "Warn when battery is low or critical",
                            checked = settings.batteryLowCheckEnabled,
                            onCheckedChange = onUpdateBatteryLowCheck
                        )
                        if (settings.batteryLowCheckEnabled) {
                            GuardianSliderRow(
                                title = "Low Battery Threshold: ${settings.batteryLowThreshold}%",
                                value = settings.batteryLowThreshold.toFloat(),
                                onValueChange = { onUpdateBatteryLowThreshold(it.toInt()) },
                                valueRange = 5f..50f
                            )
                            GuardianSliderRow(
                                title = "Critical Battery Threshold: ${settings.batteryCriticalThreshold}%",
                                value = settings.batteryCriticalThreshold.toFloat(),
                                onValueChange = { onUpdateBatteryCriticalThreshold(it.toInt()) },
                                valueRange = 2f..20f
                            )
                        }

                        GuardianToggleRow(
                            title = "Internet Check",
                            subtitle = "Warn if internet is disconnected",
                            checked = settings.internetCheckEnabled,
                            onCheckedChange = onUpdateInternetCheck
                        )

                        GuardianToggleRow(
                            title = "Default Launcher Check",
                            subtitle = "Warn if EasyUI is not the main home screen",
                            checked = settings.defaultLauncherCheckEnabled,
                            onCheckedChange = onUpdateDefaultLauncherCheck
                        )

                        GuardianToggleRow(
                            title = "Emergency Contact Check",
                            subtitle = "Warn if no emergency contact is set",
                            checked = settings.emergencyContactCheckEnabled,
                            onCheckedChange = onUpdateEmergencyContactCheck
                        )

                        GuardianToggleRow(
                            title = "Layout Lock Check",
                            subtitle = "Warn if home layout is unlocked",
                            checked = settings.layoutLockCheckEnabled,
                            onCheckedChange = onUpdateLayoutLockCheck
                        )
                    }
                }

                item {
                    Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun GuardianToggleRow(
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
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = CaregiverDashboardTokens.textTertiary,
                fontSize = 14.sp,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun GuardianSliderRow(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = CaregiverDashboardTokens.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
