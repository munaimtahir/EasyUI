package com.easyui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.ui.components.LargeActionTile
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun HomeScreen(
    timeText: String,
    batteryPercent: String,
    chargingLabel: String,
    signalLabel: String,
    simLabel: String,
    wifiLabel: String,
    tiles: List<TileDisplayModel>,
    caregiverAccessVisible: Boolean,
    flashlightTriggerProgress: Int,
    sosTriggerProgress: Int,
    onTileClick: (String) -> Unit,
    onCaregiverAccessTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tileSlots = List(6) { index -> tiles.getOrNull(index) }
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg)
                .testTag("home_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            TopStatusBar(
                timeText = timeText,
                batteryPercent = batteryPercent,
                chargingLabel = chargingLabel,
                signalLabel = signalLabel,
                simLabel = simLabel,
                wifiLabel = wifiLabel,
            )

            if (caregiverAccessVisible) {
                Button(
                    onClick = onCaregiverAccessTap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("caregiver_access_reveal"),
                ) {
                    Text("Caregiver Access")
                }
            }

            if (flashlightTriggerProgress > 0 && !caregiverAccessVisible) {
                Text(
                    text = "Flashlight pattern: $flashlightTriggerProgress/6",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("flashlight_trigger_progress"),
                )
            }

            if (sosTriggerProgress > 0) {
                Text(
                    text = "SOS ready: $sosTriggerProgress/3 taps",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("sos_trigger_progress"),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
            ) {
                repeat(3) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
                    ) {
                        repeat(2) { column ->
                            val index = row * 2 + column
                            val tile = tileSlots[index]
                            if (tile != null) {
                                LargeActionTile(
                                    title = tile.title,
                                    subtitle = tile.subtitle,
                                    enabled = tile.enabled,
                                    onClick = { onTileClick(tile.id) },
                                    highlighted = tile.title.equals("SOS", ignoreCase = true),
                                    modifier = Modifier.weight(1f),
                                )
                            } else {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                ) {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopStatusBar(
    timeText: String,
    batteryPercent: String,
    chargingLabel: String,
    signalLabel: String,
    simLabel: String,
    wifiLabel: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_top_status_bar"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyUiSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$batteryPercent · $chargingLabel",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "$signalLabel · $simLabel · $wifiLabel",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
