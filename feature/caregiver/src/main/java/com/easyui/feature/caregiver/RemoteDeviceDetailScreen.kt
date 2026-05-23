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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.model.LinkedDevice
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RemoteDeviceDetailScreen(
    device: LinkedDevice,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    val packet = device.lastReceivedPacket ?: return

    val lastUpdate = device.lastReceivedAt?.let {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d, HH:mm")
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(formatter)
    } ?: "Unknown"

    Surface(
        modifier = modifier.fillMaxSize(),
        color = CaregiverDashboardTokens.backgroundBottom,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CaregiverDashboardTokens.backgroundBrush)
                .testTag("remote_device_detail_screen"),
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
                    Text(device.name, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = CaregiverDashboardTokens.textPrimary)
                    Text("Last reported: $lastUpdate", fontSize = 14.sp, color = CaregiverDashboardTokens.textTertiary)
                }

                item {
                    DashboardSurface(elevated = true) {
                        SectionHeader(title = "Phone Health", subtitle = "Current status of the senior's device.")
                        Text(
                            packet.healthState.primaryMessage,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (packet.healthState.overallStatus) {
                                com.easyui.core.domain.model.GuardianCheckStatus.CRITICAL -> CaregiverDashboardTokens.accentDanger
                                com.easyui.core.domain.model.GuardianCheckStatus.WARNING -> CaregiverDashboardTokens.accentWarning
                                else -> Color(0xFF2D6A4F)
                            }
                        )
                        packet.healthState.checks.forEach { check ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(check.message, color = CaregiverDashboardTokens.textSecondary)
                                StatusChip(
                                    label = check.status.name,
                                    accent = when (check.status) {
                                        com.easyui.core.domain.model.GuardianCheckStatus.CRITICAL -> CaregiverDashboardTokens.accentDanger
                                        com.easyui.core.domain.model.GuardianCheckStatus.WARNING -> CaregiverDashboardTokens.accentWarning
                                        else -> Color(0xFF2D6A4F)
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    DashboardSurface(elevated = true) {
                        SectionHeader(title = "Setup Completeness", subtitle = "Are all essential features configured?")
                        packet.setupCompleteness.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.label, color = CaregiverDashboardTokens.textSecondary)
                                StatusChip(
                                    label = if (item.isComplete) "OK" else if (item.isRequired) "Fix" else "Optional",
                                    accent = if (item.isComplete) Color(0xFF2D6A4F) else if (item.isRequired) CaregiverDashboardTokens.accentDanger else CaregiverDashboardTokens.accentWarning
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Back to Linked Phones")
                    }
                }
            }
        }
    }
}
