package com.easyui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.easyui.core.domain.model.EmergencyNumber
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.ui.components.AvatarBadge
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun PhoneContactsScreen(
    contacts: List<HomeTile>,
    onCall: (String) -> Unit,
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
            item {
                Text("Caregiver contacts", style = MaterialTheme.typography.bodyLarge)
            }
            if (contacts.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(EasyUiSpacing.md)) {
                            Text("No contacts configured", style = MaterialTheme.typography.titleLarge)
                            Text("Add contacts in Caregiver Settings.", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
            items(contacts.take(10), key = { it.id }) { contact ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(EasyUiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                    ) {
                        AvatarBadge(
                            imageUri = contact.photoUri,
                            fallbackText = contact.title.take(2).uppercase(),
                        )
                        Text(contact.title, style = MaterialTheme.typography.titleLarge)
                        Text(contact.phoneNumber.orEmpty(), style = MaterialTheme.typography.bodyLarge)
                        Button(
                            onClick = { contact.phoneNumber?.let(onCall) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Call")
                        }
                    }
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
