package com.easyui.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.InstalledApp
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.ui.theme.EasyUiSpacing
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionGrid(
    pageCount: Int,
    pages: List<List<HomeTile?>>,
    installedApps: List<InstalledApp>,
    assignedAppPackages: Set<String>,
    onAssignApp: (String, Int) -> Unit,
    onRemoveApp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPageIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedPosition by rememberSaveable { mutableStateOf<Int?>(null) }
    val currentPage = pages.getOrElse(selectedPageIndex) { List(HomeLayoutRules.SLOTS_PER_PAGE) { null } }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
            repeat(pageCount) { pageIndex ->
                val selected = pageIndex == selectedPageIndex
                val click = {
                    selectedPageIndex = pageIndex
                    selectedPosition = null
                }
                if (selected) {
                    Button(onClick = click, modifier = Modifier.weight(1f)) {
                        Text("Page ${pageIndex + 1}")
                    }
                } else {
                    OutlinedButton(onClick = click, modifier = Modifier.weight(1f)) {
                        Text("Page ${pageIndex + 1}")
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
            repeat(3) { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm)) {
                    repeat(2) { col ->
                        val slotIndex = row * 2 + col
                        val tile = currentPage.getOrNull(slotIndex)
                        val position = (selectedPageIndex * HomeLayoutRules.SLOTS_PER_PAGE) + slotIndex
                        Card(
                            onClick = {
                                if (tile == null) {
                                    selectedPosition = position
                                } else {
                                    onRemoveApp(tile.packageName.orEmpty())
                                }
                            },
                            modifier = Modifier.weight(1f).aspectRatio(1.5f).testTag("slot_select_$position"),
                            colors = if (tile != null) {
                                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            } else {
                                CardDefaults.cardColors()
                            },
                            border = if (tile != null) {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    if (tile != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                        )
                                        Text(tile.title, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
                                        Text("Tap to remove", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                    } else {
                                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                                        Text("Empty Slot", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedPosition != null) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { selectedPosition = null },
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = EasyUiSpacing.md, vertical = EasyUiSpacing.sm)
                    .testTag("allowed_apps_installed_list"),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
            ) {
                Text("Select an app for this slot", style = MaterialTheme.typography.titleLarge)

                val currentPkg = pages.flatten().firstOrNull { it?.position == selectedPosition }?.packageName
                if (currentPkg != null) {
                    OutlinedButton(
                        onClick = {
                            onRemoveApp(currentPkg)
                            selectedPosition = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Remove Current App")
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                ) {
                    items(installedApps) { app ->
                        val isAssigned = app.packageName in assignedAppPackages
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = EasyUiSpacing.xs),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(app.label, style = MaterialTheme.typography.bodyLarge)
                            if (isAssigned) {
                                Text("Placed", style = MaterialTheme.typography.labelMedium)
                            } else {
                                Button(
                                    onClick = {
                                        selectedPosition?.let { onAssignApp(app.packageName, it) }
                                        selectedPosition = null
                                    },
                                ) {
                                    Text("Place Here")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(EasyUiSpacing.lg))
            }
        }
    }
}
