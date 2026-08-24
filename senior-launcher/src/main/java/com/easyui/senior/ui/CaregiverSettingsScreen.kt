package com.easyui.senior.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
fun CaregiverSettingsScreen(
    caregiverRepo: CaregiverRepository,
    onBack: () -> Unit,
    onRequestPinChange: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by caregiverRepo.stateFlow.collectAsState(initial = null)

    var showChangePinDialog by remember { mutableStateOf(false) }
    var showClearPinDialog by remember { mutableStateOf(false) }

    if (state == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isPinSet = state!!.isPinSet

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("caregiver_settings_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Caregiver Settings", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(8.dp)
                    .testTag("caregiver_settings_back"),
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PIN Protection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isPinSet) "Caregiver PIN is currently set." else "No Caregiver PIN set.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { showChangePinDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("change_pin_button")
                    ) {
                        Text(if (isPinSet) "Change PIN" else "Set PIN")
                    }
                    if (isPinSet) {
                        OutlinedButton(
                            onClick = { showClearPinDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("clear_pin_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Clear PIN")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "About Caregiver Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Caregiver Mode uses a PIN to protect launcher layout and settings from accidental changes. This is launcher-level protection and does not prevent all Android system access.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text("Set PIN") },
            text = {
                Text(
                    if (isPinSet) {
                        "You will be taken to the PIN setup screen. You'll need to enter your current PIN before choosing a new one."
                    } else {
                        "You will be taken to the PIN setup screen to set a new 4-digit PIN."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showChangePinDialog = false
                        onRequestPinChange()
                    },
                    modifier = Modifier.testTag("confirm_set_pin")
                ) {
                    Text("Proceed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearPinDialog) {
        AlertDialog(
            onDismissRequest = { showClearPinDialog = false },
            title = { Text("Clear PIN?") },
            text = {
                Text("This will remove the Caregiver PIN. Launcher settings will no longer be protected.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch { caregiverRepo.clearPin() }
                        showClearPinDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_clear_pin")
                ) {
                    Text("Clear PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
