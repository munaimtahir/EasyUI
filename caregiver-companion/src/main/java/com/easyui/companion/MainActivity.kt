package com.easyui.companion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var selectedTab by remember { mutableStateOf(CompanionTab.Seniors) }

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
                CompanionTab.Seniors -> SeniorsTab()
                CompanionTab.Alerts -> AlertsTab()
                CompanionTab.Reminders -> RemindersTab()
                CompanionTab.Settings -> SettingsTab()
            }
        }
    }
}

@Composable
fun SeniorsTab() {
    // Shows linked seniors overview
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("seniors_tab")
    ) {
        Text(
            "Linked Seniors",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Placeholder state: not paired
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No seniors linked yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "On the Senior Launcher phone, go to Caregiver Settings → Connect Caregiver to generate a pairing code.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { /* TODO Phase M: Open pairing code entry */ },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .testTag("pair_new_senior_button")
                ) {
                    Text("Enter Pairing Code", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AlertsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("alerts_tab")
    ) {
        Text(
            "Alerts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No active alerts.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Alerts for SOS triggers, missed check-ins, and low battery will appear here when a senior is paired.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RemindersTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("reminders_tab")
    ) {
        Text(
            "Reminders",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No reminders set.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "You can set reminders for your linked senior. They will appear on their phone.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("settings_tab")
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        SettingsRow(label = "Account", value = "Not signed in", tag = "settings_account")
        Spacer(modifier = Modifier.height(12.dp))
        SettingsRow(label = "Privacy Policy", value = "View", tag = "settings_privacy")
        Spacer(modifier = Modifier.height(12.dp))
        SettingsRow(label = "App Version", value = "0.1.0", tag = "settings_version")

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Data You Can Access",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Battery level (if permitted)\n• Voluntary check-ins\n• SOS alerts\n• Last sync time\n• Reminder status",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You cannot access: SMS, calls, photos, location, browsing history, or app content.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
