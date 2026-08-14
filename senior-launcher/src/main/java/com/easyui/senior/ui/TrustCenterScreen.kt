package com.easyui.senior.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.senior.storage.CaregiverRepository
import kotlinx.coroutines.launch

@Composable
fun TrustCenterScreen(
    caregiverRepo: CaregiverRepository,
    onBack: () -> Unit,
    onRevoke: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by caregiverRepo.stateFlow.collectAsState(initial = null)

    var showDisconnectConfirm by remember { mutableStateOf(false) }

    if (state == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isPaired = state!!.isPaired == "paired"
    val caregiverName = state!!.caregiverName ?: "Unknown Caregiver"
    val permissions = state!!.permissions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("trust_center_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Privacy & Trust Center", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(8.dp)
                    .testTag("trust_center_back"),
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Data Transparency Policy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "• Local Only: Your private SMS, calls, messages, camera pictures, and browser history NEVER leave this phone.\n\n" +
                                   "• Shared with Caregiver: If paired, your caregiver can view your battery level and receive SOS emergency alerts. No other telemetry is gathered.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Connected Caregiver",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (isPaired) {
                            Text(
                                text = "Paired Caregiver: $caregiverName",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Granted Permissions:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            if (permissions.isEmpty()) {
                                Text("No permissions granted yet.", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                permissions.forEach { perm ->
                                    Text("• $perm", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showDisconnectConfirm = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("disconnect_caregiver_button")
                            ) {
                                Text("Disconnect Caregiver", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(
                                text = "Not paired with any remote caregiver.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDisconnectConfirm) {
        AlertDialog(
            onDismissRequest = { showDisconnectConfirm = false },
            title = { Text("Disconnect Caregiver?") },
            text = {
                Text("This will immediately revoke remote access and delete connection details. Your caregiver will no longer see updates or receive alerts.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            caregiverRepo.clearPairing()
                            showDisconnectConfirm = false
                            onRevoke()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_disconnect")
                ) {
                    Text("Disconnect")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisconnectConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
