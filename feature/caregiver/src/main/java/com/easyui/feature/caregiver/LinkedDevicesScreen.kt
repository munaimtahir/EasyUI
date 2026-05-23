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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun LinkedDevicesScreen(
    devices: List<LinkedDevice>,
    onViewDevice: (LinkedDevice) -> Unit,
    onRemoveDevice: (String) -> Unit,
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
                .testTag("linked_devices_screen"),
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
                    Text("Linked Phones", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = CaregiverDashboardTokens.textPrimary)
                }

                if (devices.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CaregiverDashboardTokens.surfaceElevated)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("No linked phones yet.", color = CaregiverDashboardTokens.textPrimary, fontSize = 18.sp)
                                Text(
                                    "When you scan a Remote Status link from another EasyUI device, it will appear here.",
                                    color = CaregiverDashboardTokens.textTertiary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                items(devices, key = { it.id }) { device ->
                    LinkedDeviceRow(
                        device = device,
                        onClick = { onViewDevice(device) },
                        onRemove = { onRemoveDevice(device.id) }
                    )
                }

                item {
                    OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                        Text("Back to Caregiver Settings")
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkedDeviceRow(
    device: LinkedDevice,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val lastUpdate = device.lastReceivedAt?.let {
        val formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm")
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(formatter)
    } ?: "Never"

    val statusColor = when (device.lastReceivedPacket?.healthState?.overallStatus) {
        com.easyui.core.domain.model.GuardianCheckStatus.CRITICAL -> CaregiverDashboardTokens.accentDanger
        com.easyui.core.domain.model.GuardianCheckStatus.WARNING -> CaregiverDashboardTokens.accentWarning
        else -> Color(0xFF2D6A4F)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CaregiverDashboardTokens.surfaceElevated),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(statusColor.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Phonelink, contentDescription = null, tint = statusColor)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(device.name, style = MaterialTheme.typography.titleLarge, color = CaregiverDashboardTokens.textPrimary)
                Text("Last update: $lastUpdate", style = MaterialTheme.typography.bodySmall, color = CaregiverDashboardTokens.textTertiary)
                Text(
                    device.lastReceivedPacket?.healthState?.primaryMessage ?: "No data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = CaregiverDashboardTokens.textTertiary)
            }
        }
    }
}
