package com.easyui.companion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.easyui.companion.network.*
import com.easyui.companion.storage.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompanionAppRoot()
                }
            }
        }
    }
}

enum class CompanionTab { Seniors, Alerts, Reminders, Settings }

@Composable
fun CompanionAppRoot() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { CompanionSession(context) }
    var sessionState by remember { mutableStateOf<CompanionSessionState?>(null) }
    var selectedTab by remember { mutableStateOf(CompanionTab.Seniors) }

    fun refreshSession() {
        scope.launch {
            val state = sessionManager.getSession()
            sessionState = state
            CompanionBackendClient.deviceToken = state.deviceToken
        }
    }

    LaunchedEffect(Unit) {
        refreshSession()
    }

    val state = sessionState
    if (state == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!state.isPaired) {
        PairingScreen(
            sessionManager = sessionManager,
            onPaired = { refreshSession() }
        )
        return
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Caregiver Companion",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == CompanionTab.Seniors,
                    onClick = { selectedTab = CompanionTab.Seniors },
                    icon = { Text("👴", fontSize = 20.sp) },
                    label = { Text("Seniors") },
                    modifier = Modifier.testTag("tab_seniors")
                )
                NavigationBarItem(
                    selected = selectedTab == CompanionTab.Alerts,
                    onClick = { selectedTab = CompanionTab.Alerts },
                    icon = { Text("🔔", fontSize = 20.sp) },
                    label = { Text("Alerts") },
                    modifier = Modifier.testTag("tab_alerts")
                )
                NavigationBarItem(
                    selected = selectedTab == CompanionTab.Reminders,
                    onClick = { selectedTab = CompanionTab.Reminders },
                    icon = { Text("📋", fontSize = 20.sp) },
                    label = { Text("Reminders") },
                    modifier = Modifier.testTag("tab_reminders")
                )
                NavigationBarItem(
                    selected = selectedTab == CompanionTab.Settings,
                    onClick = { selectedTab = CompanionTab.Settings },
                    icon = { Text("⚙️", fontSize = 20.sp) },
                    label = { Text("Settings") },
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                CompanionTab.Seniors -> SeniorsTab(state.linkedSeniorDeviceId!!)
                CompanionTab.Alerts -> AlertsTab(state.linkedSeniorDeviceId!!)
                CompanionTab.Reminders -> RemindersTab(state.linkedSeniorDeviceId!!)
                CompanionTab.Settings -> SettingsTab(
                    sessionManager = sessionManager,
                    onDisconnect = { refreshSession() }
                )
            }
        }
    }
}

@Composable
fun PairingScreen(sessionManager: CompanionSession, onPaired: () -> Unit) {
    val scope = rememberCoroutineScope()
    var codeInput by remember { mutableStateOf("") }
    var isPairing by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("pairing_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Caregiver Pairing", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Enter the 8-character pairing code displayed in the Privacy & Trust section of the Senior Launcher app.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = codeInput,
            onValueChange = { codeInput = it.uppercase() },
            label = { Text("Pairing Code") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pairing_code_input"),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )
        )

        if (errorText != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorText!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (codeInput.length != 8) {
                    errorText = "Pairing code must be exactly 8 characters."
                    return@Button
                }
                scope.launch {
                    isPairing = true
                    errorText = null
                    val state = sessionManager.getSession()
                    val res = CompanionBackendClient.pairWithSenior(codeInput, state.caregiverDeviceId)
                    if (res != null) {
                        sessionManager.saveSession(res.deviceToken, res.seniorDeviceId, res.permissions)
                        onPaired()
                    } else {
                        errorText = "Failed to pair. Code may be invalid, expired, or server offline."
                    }
                    isPairing = false
                }
            },
            enabled = !isPairing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("submit_pairing_code_button")
        ) {
            if (isPairing) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Pair with Launcher Device", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun SeniorsTab(seniorDeviceId: String) {
    var statusState by remember { mutableStateOf<StatusResponseDto?>(null) }
    var checkInState by remember { mutableStateOf<CheckInPayloadDto?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun loadData() {
        scope.launch {
            isRefreshing = true
            errorText = null
            val status = CompanionBackendClient.fetchStatus(seniorDeviceId)
            val checkin = CompanionBackendClient.fetchCheckIn(seniorDeviceId)
            statusState = status
            checkInState = checkin
            if (status == null) {
                errorText = "No response from device or permission missing."
            }
            isRefreshing = false
        }
    }

    LaunchedEffect(seniorDeviceId) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("seniors_tab")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Senior Device Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Refresh",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { loadData() }
                    .padding(8.dp)
                    .testTag("refresh_status_button")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorText != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorText!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Device ID: $seniorDeviceId", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))

                val status = statusState
                if (status != null) {
                    val fmt = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault())
                    Text("Last Seen: ${fmt.format(Date(status.lastSeen))}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Battery Level: ${status.batteryLevel}%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("Status: ${if (status.isCharging) "Charging ⚡" else "Discharging"}", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("No status received yet.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Last Voluntary Check-In", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                val checkin = checkInState
                if (checkin != null) {
                    val fmt = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault())
                    Text("Status: \"${checkin.message}\"", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reported: ${fmt.format(Date(checkin.timestamp))}", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("No check-ins reported yet.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun AlertsTab(seniorDeviceId: String) {
    var alertsList by remember { mutableStateOf<List<StoredAlertDto>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadAlerts() {
        scope.launch {
            isRefreshing = true
            val res = CompanionBackendClient.fetchAlerts(seniorDeviceId)
            if (res != null) {
                alertsList = res.alerts
            }
            isRefreshing = false
        }
    }

    LaunchedEffect(seniorDeviceId) {
        loadAlerts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("alerts_tab")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Emergency Alerts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Refresh",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { loadAlerts() }
                    .padding(8.dp)
                    .testTag("refresh_alerts_button")
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (alertsList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active alerts.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(alertsList) { alert ->
                    val fmt = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault())
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (alert.type == "SOS") MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = alert.type,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (alert.type == "SOS") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(fmt.format(Date(alert.timestamp)), style = MaterialTheme.typography.bodySmall)
                            }
                            if (alert.details.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(alert.details, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RemindersTab(seniorDeviceId: String) {
    val scope = rememberCoroutineScope()
    var titleInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf("Medication") }
    var timeInput by remember { mutableStateOf("09:00") }
    var remindersList by remember { mutableStateOf<List<RemoteReminderDto>>(emptyList()) }
    var isSending by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("reminders_tab")
    ) {
        Text("Suggest Reminders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Create Suggestion", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("Reminder Title") },
                    modifier = Modifier.fillMaxWidth().testTag("suggest_reminder_title")
                )

                OutlinedTextField(
                    value = timeInput,
                    onValueChange = { timeInput = it },
                    label = { Text("Time (e.g. 10:30)") },
                    modifier = Modifier.fillMaxWidth().testTag("suggest_reminder_time")
                )

                Button(
                    onClick = {
                        if (titleInput.isNotEmpty() && timeInput.isNotEmpty()) {
                            val newReminder = RemoteReminderDto(
                                id = System.currentTimeMillis().toString(),
                                title = titleInput,
                                type = typeInput,
                                time = timeInput
                            )
                            remindersList = remindersList + newReminder
                            titleInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("add_staged_reminder_button")
                ) {
                    Text("Stage Reminder")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Staged Suggestions (${remindersList.size})", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(remindersList) { r ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(r.title, fontWeight = FontWeight.Bold)
                            Text("${r.type} • ${r.time}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = "Remove",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .clickable { remindersList = remindersList.filter { it.id != r.id } }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    isSending = true
                    val success = CompanionBackendClient.sendConfig(seniorDeviceId, remindersList)
                    if (success) {
                        remindersList = emptyList()
                    }
                    isSending = false
                }
            },
            enabled = remindersList.isNotEmpty() && !isSending,
            modifier = Modifier.fillMaxWidth().testTag("push_reminders_button")
        ) {
            if (isSending) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Push suggestions to launcher")
            }
        }
    }
}

@Composable
fun SettingsTab(sessionManager: CompanionSession, onDisconnect: () -> Unit) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("settings_tab")
    ) {
        Text("Caregiver Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        SettingsRow(label = "Privacy Policy", value = "View", tag = "settings_privacy")
        Spacer(modifier = Modifier.height(12.dp))
        SettingsRow(label = "App Version", value = "0.1.0", tag = "settings_version")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch {
                    sessionManager.clearSession()
                    onDisconnect()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth().testTag("disconnect_companion_button")
        ) {
            Text("Disconnect and Clear Device Link")
        }
    }
}

@Composable
fun SettingsRow(label: String, value: String, tag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
