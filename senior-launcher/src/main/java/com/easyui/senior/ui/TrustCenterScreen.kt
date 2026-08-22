package com.easyui.senior.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.senior.network.PairingManager
import com.easyui.senior.network.PairingState
import com.easyui.senior.storage.CaregiverRepository
import kotlinx.coroutines.launch

@Composable
fun TrustCenterScreen(
    caregiverRepo: CaregiverRepository,
    onBack: () -> Unit,
    onRevoke: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pairingManager = remember { PairingManager(context) }
    var pairingState by remember { mutableStateOf<PairingState?>(null) }
    var showDisconnectConfirm by remember { mutableStateOf(false) }
    var isRequestingCode by remember { mutableStateOf(false) }
    var codeRequestError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        pairingState = pairingManager.getState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("trust_center_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Privacy & Trust",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.testTag("trust_back")
            ) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pairing status section
        val state = pairingState
        if (state != null) {
            if (state.isPaired) {
                PairedStatusCard(
                    permissions = state.permissions,
                    onDisconnect = { showDisconnectConfirm = true }
                )
            } else {
                // Show pending code or generate new one
                PairingCodeCard(
                    pendingCode = state.pendingPairingCode,
                    isLoading = isRequestingCode,
                    error = codeRequestError,
                    onRequestCode = {
                        scope.launch {
                            isRequestingCode = true
                            codeRequestError = null
                            val code = pairingManager.requestPairingCode()
                            if (code != null) {
                                pairingState = pairingManager.getState()
                            } else {
                                codeRequestError = "Could not connect to server. Make sure the companion app backend is running."
                            }
                            isRequestingCode = false
                        }
                    },
                    onCheckPairingStatus = {
                        scope.launch {
                            isRequestingCode = true
                            codeRequestError = null
                            if (pairingManager.refreshPairingCompletion()) {
                                pairingState = pairingManager.getState()
                            } else {
                                codeRequestError = "Pairing is still waiting for your caregiver."
                            }
                            isRequestingCode = false
                        }
                    }
                )
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // What data is shared
        DataSharingCard()

        Spacer(modifier = Modifier.height(24.dp))

        // What we never share
        NeverSharedCard()
    }

    if (showDisconnectConfirm) {
        AlertDialog(
            onDismissRequest = { showDisconnectConfirm = false },
            title = { Text("Disconnect Caregiver?") },
            text = { Text("This will remove the caregiver link. They will no longer receive your status, alerts, or check-ins. You can re-pair at any time.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            pairingManager.revokePairing()
                            pairingState = pairingManager.getState()
                            showDisconnectConfirm = false
                            onRevoke()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_revoke")
                ) { Text("Disconnect") }
            },
            dismissButton = {
                TextButton(onClick = { showDisconnectConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PairedStatusCard(permissions: List<String>, onDisconnect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("paired_status_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("✅ Caregiver Connected", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Active permissions:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            val permissionLabels = mapOf(
                "battery" to "Battery level & charging status",
                "checkin" to "Voluntary check-ins (I'm OK)",
                "config" to "Receive reminder suggestions",
                "alerts" to "SOS alerts"
            )
            permissionLabels.forEach { (key, label) ->
                val granted = permissions.contains(key)
                Text(
                    text = "${if (granted) "✓" else "✗"} $label",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (granted) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onDisconnect,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().testTag("disconnect_button")
            ) { Text("Disconnect Caregiver") }
        }
    }
}

@Composable
private fun PairingCodeCard(
    pendingCode: String?,
    isLoading: Boolean,
    error: String?,
    onRequestCode: () -> Unit,
    onCheckPairingStatus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("pairing_code_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Connect a Caregiver", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (pendingCode != null) {
                Text(
                    "Share this code with your caregiver app:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 32.dp, vertical = 20.dp)
                        .testTag("pairing_code_display")
                ) {
                    Text(
                        text = pendingCode,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        letterSpacing = 6.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Code expires in 10 minutes. Ask your caregiver to enter this code in their Caregiver Companion app.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCheckPairingStatus,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().testTag("check_pairing_status_button")
                ) { Text("I've shared the code — check connection") }
                TextButton(onClick = onRequestCode, enabled = !isLoading) { Text("Generate New Code") }
            } else {
                Text(
                    "No caregiver is connected. Generate a pairing code to share with your caregiver.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRequestCode,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().testTag("generate_code_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Generate Pairing Code")
                    }
                }
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("pairing_error")
                    )
                }
            }
        }
    }
}

@Composable
private fun DataSharingCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("What your caregiver can see (if permitted):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            listOf(
                "Battery level and charging status",
                "\"I'm OK\" check-in timestamps",
                "SOS alerts you send",
                "Reminder suggestions they send"
            ).forEach { item ->
                Text("• $item", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun NeverSharedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("What is NEVER shared:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(modifier = Modifier.height(8.dp))
            listOf(
                "SMS or call content",
                "Photos or files",
                "Location",
                "Browsing history",
                "App content or usage",
                "Microphone or camera"
            ).forEach { item ->
                Text("🚫 $item", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}
