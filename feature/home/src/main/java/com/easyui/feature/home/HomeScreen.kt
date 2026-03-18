package com.easyui.feature.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.ui.components.LargeActionTile
import com.easyui.core.ui.theme.EasyUiSpacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

private const val TopBarLongPressMs = 3_000L

@Composable
fun HomeScreen(
    timeText: String,
    batteryPercent: String,
    chargingLabel: String,
    signalLabel: String,
    simLabel: String,
    wifiLabel: String,
    tiles: List<TileDisplayModel>,
    sosTriggerProgress: Int,
    onTileClick: (String) -> Unit,
    onStatusBarLongPress: () -> Unit,
    onClockTapped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    var accessCueVisible by remember { mutableStateOf(false) }
    val tileSlots = List(6) { index -> tiles.getOrNull(index) }

    LaunchedEffect(accessCueVisible) {
        if (accessCueVisible) {
            delay(900)
            accessCueVisible = false
        }
    }

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
                accessCueVisible = accessCueVisible,
                onLongPressConfirmed = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    accessCueVisible = true
                    onStatusBarLongPress()
                },
                onClockTap = onClockTapped,
            )

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
    accessCueVisible: Boolean,
    onLongPressConfirmed: () -> Unit,
    onClockTap: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_top_status_bar")
            .pointerInput(onLongPressConfirmed) {
                detectTapGestures(
                    onPress = {
                        val releasedBeforeTimeout = withTimeoutOrNull(TopBarLongPressMs) {
                            tryAwaitRelease()
                        }
                        if (releasedBeforeTimeout == null) {
                            onLongPressConfirmed()
                        }
                    }
                )
            },
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
            Column {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .testTag("home_clock_text")
                        .pointerInput(onClockTap) {
                            detectTapGestures(onTap = { onClockTap() })
                        },
                )
                if (accessCueVisible) {
                    Text(
                        text = "Access detected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("caregiver_access_cue"),
                    )
                }
            }
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
