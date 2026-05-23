package com.easyui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.ui.components.AvatarBadge
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun PhoneContactsScreen(
    contacts: List<HomeTile>,
    onCall: (String) -> Unit,
    onOpenDialer: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("phone_contacts_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            item {
                Text("Phone", style = MaterialTheme.typography.headlineLarge)
            }
            if (contacts.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(EasyUiSpacing.md)) {
                            Text("No contacts added", style = MaterialTheme.typography.titleLarge)
                            Text("Ask caregiver to add important family contacts.", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            } else {
                item {
                    Text("Important contacts", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
                items(contacts.take(10), key = { it.id }) { contact ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(EasyUiSpacing.md)
                                .fillMaxWidth(),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)
                        ) {
                            AvatarBadge(
                                imageUri = contact.photoUri,
                                fallbackText = contact.title.take(2).uppercase(),
                                modifier = Modifier.size(56.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(contact.phoneNumber.orEmpty(), style = MaterialTheme.typography.bodyLarge)
                            }
                            Button(
                                onClick = { contact.phoneNumber?.let(onCall) },
                                modifier = Modifier.defaultMinSize(minWidth = 80.dp)
                            ) {
                                Text("Call")
                            }
                        }
                    }
                }
            }
            
            item {
                Text("More actions", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            }
            
            item {
                Button(
                    onClick = onOpenDialer,
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Open Dialer", fontSize = 18.sp, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            item {
                OutlinedButton(onClick = onBackHome, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to Home")
                }
            }
        }
    }
}

@Composable
fun EmergencyCallScreen(
    numbers: List<EmergencyNumber>,
    onCall: (String) -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("emergency_call_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            item {
                Text("Emergency", style = MaterialTheme.typography.headlineLarge)
            }
            item {
                Text("Choose who to call right now.", style = MaterialTheme.typography.bodyLarge)
            }
            items(numbers, key = { "${it.label}-${it.phoneNumber}" }) { number ->
                Button(
                    onClick = { onCall(number.phoneNumber) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("${number.label}: ${number.phoneNumber}")
                }
            }
            item {
                OutlinedButton(onClick = onBackHome, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to Home")
                }
            }
        }
    }
}
