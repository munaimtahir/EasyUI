package com.easyui.feature.caregiver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun BackupRestoreScreen(
    isExporting: Boolean,
    isImporting: Boolean,
    lastResult: String?,
    pendingImportConfirmation: Boolean,
    onExport: () -> Unit,
    onPickImportFile: () -> Unit,
    onConfirmImport: () -> Unit,
    onCancelImport: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("backup_restore_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text("Backup and Restore", style = MaterialTheme.typography.headlineLarge)
            }
            item {
                Text(
                    "Save the EasyUI layout, settings, and call shortcuts to a file on this device. " +
                        "Use the file later to restore the setup on the same or a different phone.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                Text(
                    "Note: The caregiver PIN and contact photos are not included in the backup.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (pendingImportConfirmation) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(EasyUiSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                        ) {
                            Text("Confirm Restore", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "This will replace the current EasyUI layout and settings with the backup. " +
                                    "Your caregiver PIN will not be changed.",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Button(
                                onClick = onConfirmImport,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Restore from Backup")
                            }
                            OutlinedButton(
                                onClick = onCancelImport,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(EasyUiSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                        ) {
                            Text("Export", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "Save the current setup to a JSON file on this device.",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            if (isExporting) {
                                CircularProgressIndicator()
                            } else {
                                Button(
                                    onClick = onExport,
                                    modifier = Modifier.fillMaxWidth().testTag("export_button"),
                                ) {
                                    Text("Export Backup")
                                }
                            }
                        }
                    }
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(EasyUiSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                        ) {
                            Text("Restore", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "Pick a backup file from this device to restore a saved setup.",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            if (isImporting) {
                                CircularProgressIndicator()
                            } else {
                                Button(
                                    onClick = onPickImportFile,
                                    modifier = Modifier.fillMaxWidth().testTag("import_button"),
                                ) {
                                    Text("Choose Backup File")
                                }
                            }
                        }
                    }
                }
            }

            if (!lastResult.isNullOrBlank()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(EasyUiSpacing.md)) {
                            Text(lastResult, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            item {
                OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                    Text("Back to Caregiver Settings")
                }
            }
        }
    }
}
