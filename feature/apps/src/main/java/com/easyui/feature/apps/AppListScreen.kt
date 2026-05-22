package com.easyui.feature.apps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.ui.components.SectionHeader
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun AppListScreen(
    query: String,
    apps: List<InstalledApp>,
    emptyTitle: String?,
    emptyBody: String?,
    onQueryChange: (String) -> Unit,
    onAppClick: (InstalledApp) -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .navigationBarsPadding()
                .testTag("app_list_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            SectionHeader(text = "All Apps")
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_search_field"),
                label = { Text("Search apps") },
                singleLine = true,
            )
            if (apps.isEmpty() && emptyTitle != null && emptyBody != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_list_empty_state"),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                ) {
                    Text(emptyTitle, style = MaterialTheme.typography.titleLarge)
                    Text(emptyBody, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                ) {
                    items(apps, key = { it.packageName }) { app ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAppClick(app) }
                                .padding(vertical = EasyUiSpacing.sm)
                                .testTag("app_item_${app.packageName}"),
                        ) {
                            Text(app.label.ifBlank { "Unnamed app" }, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = onBackHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_list_back_home"),
            ) {
                Text("Back to Home")
            }
        }
    }
}
