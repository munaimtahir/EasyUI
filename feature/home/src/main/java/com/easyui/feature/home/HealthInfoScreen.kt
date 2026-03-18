package com.easyui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.easyui.core.domain.model.HealthInfo
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun HealthInfoScreen(
    healthInfo: HealthInfo,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("health_info_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            item {
                Text("Health Info", style = MaterialTheme.typography.headlineLarge)
            }
            item {
                Text(
                    "This information is stored only on this phone to help during appointments or emergencies.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item { HealthFieldCard("Name", healthInfo.fullName) }
            item { HealthFieldCard("Age", healthInfo.age) }
            item { HealthFieldCard("Blood Group", healthInfo.bloodGroup) }
            item { HealthFieldCard("Allergies", healthInfo.allergies) }
            item { HealthFieldCard("Medical Conditions", healthInfo.medicalConditions) }
            item { HealthFieldCard("Medicines", healthInfo.medicines) }
            item { HealthFieldCard("Doctor / Emergency Contact", healthInfo.doctorOrEmergencyContact) }
            item { HealthFieldCard("Notes", healthInfo.notes) }
            item {
                Button(onClick = onBackHome, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to Home")
                }
            }
        }
    }
}

@Composable
private fun HealthFieldCard(
    label: String,
    value: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(EasyUiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
        ) {
            Text(label, style = MaterialTheme.typography.titleLarge)
            Text(
                text = value.ifBlank { "Not set" },
                style = MaterialTheme.typography.bodyLarge,
                color = if (value.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}
