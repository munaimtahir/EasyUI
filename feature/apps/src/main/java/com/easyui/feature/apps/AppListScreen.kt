package com.easyui.feature.apps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                label = { Text("Search apps", fontSize = 18.sp) },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                }
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
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
                ) {
                    items(apps, key = { it.packageName }) { app ->
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val appIcon = remember(app.packageName) {
                            try {
                                context.packageManager.getApplicationIcon(app.packageName)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAppClick(app) }
                                .testTag("app_item_${app.packageName}"),
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(EasyUiSpacing.md)
                                    .fillMaxWidth(),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md)
                            ) {
                                if (appIcon != null) {
                                    androidx.compose.ui.viewinterop.AndroidView(
                                        factory = { ctx ->
                                            android.widget.ImageView(ctx).apply {
                                                setImageDrawable(appIcon)
                                            }
                                        },
                                        modifier = Modifier.size(48.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.GridView,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                Text(
                                    text = app.label.ifBlank { "Unnamed app" },
                                    style = MaterialTheme.typography.headlineSmall,
                                )
                            }
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
