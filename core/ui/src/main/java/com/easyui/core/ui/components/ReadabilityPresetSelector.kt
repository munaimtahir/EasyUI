package com.easyui.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.ui.theme.EasyUiSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadabilityPresetSelector(
    currentPreset: HomeReadabilityPreset,
    onPresetSelected: (HomeReadabilityPreset) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
    ) {
        HomeReadabilityPreset.entries.forEach { preset ->
            val selected = preset == currentPreset
            val description = when (preset) {
                HomeReadabilityPreset.STANDARD -> "Standard size for users with good vision."
                HomeReadabilityPreset.LARGER_TEXT -> "Larger text for better legibility."
                HomeReadabilityPreset.LARGER_TILES -> "Larger buttons for easier targets."
                HomeReadabilityPreset.EXTRA_SIMPLE_SPACING -> "Simplified layout with maximum clarity."
            }
            Card(
                onClick = { onPresetSelected(preset) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("readability_preset_${preset.name.lowercase()}"),
                colors = if (selected) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                } else {
                    CardDefaults.cardColors()
                },
                border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
            ) {
                Column(modifier = Modifier.padding(EasyUiSpacing.md), verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                    Text(
                        preset.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(description, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
