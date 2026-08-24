package com.easyui.senior.ui

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyui.senior.storage.coreDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Reminder(
    val id: String,
    val title: String,
    val type: String,
    val time: String
)

internal val remindersJson = Json { ignoreUnknownKeys = true; isLenient = true }

/**
 * Decodes the stored reminders list, falling back to the legacy ";"/"|"-joined format
 * (unescaped, so a title containing either character used to corrupt the whole list) for
 * data written before reminders were JSON-encoded. The next [saveReminders] call rewrites
 * whatever's loaded in the safe JSON format.
 */
internal fun decodeReminders(raw: String): List<Reminder> {
    if (raw.isBlank()) return emptyList()
    return try {
        remindersJson.decodeFromString<List<Reminder>>(raw)
    } catch (e: Exception) {
        raw.split(";").mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size >= 4) Reminder(parts[0], parts[1], parts[2], parts[3]) else null
        }
    }
}

@Composable
fun RemindersScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val remindersKey = stringPreferencesKey("local_reminders")

    val remindersFlow = remember(context) {
        context.coreDataStore.data.map { prefs -> decodeReminders(prefs[remindersKey] ?: "") }
    }

    val remindersList by remindersFlow.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf("Medication") }
    var timeInput by remember { mutableStateOf("08:00") }

    fun saveReminders(newList: List<Reminder>) {
        val raw = remindersJson.encodeToString(newList)
        scope.launch {
            context.coreDataStore.edit { it[remindersKey] = raw }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("reminders_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Local Reminders", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.testTag("reminders_back")
            ) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Note: Reminders are for convenience and are not certified medical devices.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                titleInput = ""
                typeInput = "Medication"
                timeInput = "08:00"
                showAddDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("add_reminder_button")
        ) {
            Text("Add New Reminder", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (remindersList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reminders set.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag("reminders_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(remindersList, key = { it.id }) { reminder ->
                    ReminderRow(
                        reminder = reminder,
                        onDelete = {
                            val updated = remindersList.filter { it.id != reminder.id }
                            saveReminders(updated)
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Reminder Title") },
                        modifier = Modifier.fillMaxWidth().testTag("reminder_title_field")
                    )

                    Text("Reminder Type", fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Medication", "Appointment", "Activity", "Custom").forEach { type ->
                            val selected = typeInput == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { typeInput = type }
                                    .padding(8.dp)
                                    .testTag("reminder_type_$type"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = timeInput,
                        onValueChange = { timeInput = it },
                        label = { Text("Time (e.g. 09:30)") },
                        modifier = Modifier.fillMaxWidth().testTag("reminder_time_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotEmpty() && timeInput.isNotEmpty()) {
                            val newReminder = Reminder(
                                id = System.currentTimeMillis().toString(),
                                title = titleInput,
                                type = typeInput,
                                time = timeInput
                            )
                            saveReminders(remindersList + newReminder)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_reminder")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ReminderRow(
    reminder: Reminder,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(reminder.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(reminder.type) }
                )
                SuggestionChip(
                    onClick = {},
                    label = { Text(reminder.time) }
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_reminder_${reminder.id}")
        ) {
            Text("DEL", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
    }
}
