package com.easyui.feature.caregiver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun HiddenAppsScreen(
    installedApps: List<InstalledApp>,
    hiddenPackages: Set<String>,
    onToggleHidden: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("hidden_apps_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text("Hidden Apps", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Apps toggled off here will not appear in EasyUI app surfaces that expose app inventory. They are still installed on the device.",
                style = MaterialTheme.typography.bodyLarge,
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
            ) {
                items(installedApps, key = { it.packageName }) { app ->
                    val isHidden = app.packageName in hiddenPackages
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(EasyUiSpacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.label, style = MaterialTheme.typography.titleLarge)
                                Text(app.packageName, style = MaterialTheme.typography.bodyMedium)
                            }
                            Switch(
                                checked = !isHidden,
                                onCheckedChange = { onToggleHidden(app.packageName) },
                                modifier = Modifier.testTag("hide_switch_${app.packageName}")
                            )
                        }
                    }
                }
            }

            Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Caregiver Settings")
            }
        }
    }
}
