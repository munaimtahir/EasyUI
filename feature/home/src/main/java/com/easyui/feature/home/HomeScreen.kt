package com.easyui.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.ui.components.LargeActionTile
import com.easyui.core.ui.theme.ColorPalette
import com.easyui.core.ui.theme.EmphasisMode
import com.easyui.core.ui.theme.LayoutConfig
import com.easyui.core.ui.theme.SkinManager
import com.easyui.core.ui.theme.SpacingSet
import com.easyui.core.ui.theme.TileStyle
import com.easyui.core.ui.theme.TypographySet
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
    skinConfig: SkinConfig,
    onTileClick: (String) -> Unit,
    onStatusBarLongPress: () -> Unit,
    onClockTapped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    var accessCueVisible by remember { mutableStateOf(false) }
    val skinManager = remember(skinConfig) { SkinManager(skinConfig) }
    val colors = skinManager.getColors()
    val typography = skinManager.getTypography()
    val spacing = skinManager.getSpacing()
    val tileStyle = skinManager.getTileStyle()
    val layout = skinManager.getLayoutConfig()
    val tileSlots = List(layout.gridRows * layout.gridCols) { index -> tiles.getOrNull(index) }

    LaunchedEffect(accessCueVisible) {
        if (accessCueVisible) {
            delay(900)
            accessCueVisible = false
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = colors.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.background,
                            colors.backgroundAccent.copy(alpha = 0.88f),
                            colors.background,
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = 240.dp, y = (-30).dp)
                    .clip(RoundedCornerShape(120.dp))
                    .background(colors.accentMuted.copy(alpha = 0.36f)),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.padding)
                    .testTag("home_screen"),
                verticalArrangement = Arrangement.spacedBy(spacing.sectionSpacing),
            ) {
                TopStatusBar(
                    timeText = timeText,
                    batteryPercent = batteryPercent,
                    chargingLabel = chargingLabel,
                    signalLabel = signalLabel,
                    simLabel = simLabel,
                    wifiLabel = wifiLabel,
                    accessCueVisible = accessCueVisible,
                    colors = colors,
                    typography = typography,
                    spacing = spacing,
                    onLongPressConfirmed = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        accessCueVisible = true
                        onStatusBarLongPress()
                    },
                    onClockTap = onClockTapped,
                )

                HomeSectionHeader(
                    title = "Today's essentials",
                    subtitle = "Large buttons, clear labels, and a steady layout every time.",
                    colors = colors,
                    typography = typography,
                )

                if (sosTriggerProgress > 0) {
                    SosProgressPill(
                        text = "SOS ready: $sosTriggerProgress/3 taps",
                        colors = colors,
                        typography = typography,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.tileSpacing),
                ) {
                    repeat(layout.gridRows) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f * layout.tileSizeScale),
                            horizontalArrangement = Arrangement.spacedBy(spacing.tileSpacing),
                        ) {
                            repeat(layout.gridCols) { column ->
                                val index = row * layout.gridCols + column
                                val tile = tileSlots.getOrNull(index)
                                if (tile != null) {
                                    LargeActionTile(
                                        title = tile.title,
                                        subtitle = tile.subtitle,
                                        enabled = tile.enabled,
                                        onClick = { onTileClick(tile.id) },
                                        highlighted = tile.title.equals("SOS", ignoreCase = true) || isEmphasized(tile.title, layout),
                                        showSubtitle = layout.showLabels,
                                        palette = colors,
                                        typography = typography,
                                        spacing = spacing,
                                        tileStyle = tileStyle,
                                        modifier = Modifier.weight(1f),
                                    )
                                } else {
                                    EmptyTilePlaceholder(
                                        colors = colors,
                                        spacing = spacing,
                                        tileStyle = tileStyle,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isEmphasized(title: String, layout: LayoutConfig): Boolean {
    val normalized = title.lowercase()
    return when (layout.emphasisMode) {
        EmphasisMode.BALANCED -> false
        EmphasisMode.CARE_FOCUSED -> normalized in setOf("health info", "emergency", "sos")
        EmphasisMode.COMMUNICATION_FOCUSED -> normalized in setOf("phone", "contacts")
    }
}

@Composable
private fun HomeSectionHeader(
    title: String,
    subtitle: String,
    colors: ColorPalette,
    typography: TypographySet,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = TextStyle(fontSize = typography.titleSize, fontWeight = FontWeight.SemiBold),
            color = colors.primaryText,
        )
        Text(
            text = subtitle,
            style = TextStyle(fontSize = typography.supportSize, fontWeight = FontWeight.Medium),
            color = colors.secondaryText,
        )
    }
}

@Composable
private fun SosProgressPill(
    text: String,
    colors: ColorPalette,
    typography: TypographySet,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(colors.sosColor.copy(alpha = 0.12f))
            .border(1.dp, colors.sosColor.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("sos_trigger_progress"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = typography.labelSize, fontWeight = typography.fontWeight),
            color = colors.sosColor,
        )
    }
}

@Composable
private fun EmptyTilePlaceholder(
    colors: ColorPalette,
    spacing: SpacingSet,
    tileStyle: TileStyle,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(spacing.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = colors.tileBackgroundMuted.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = tileStyle.elevationDp.dp / 2f),
    ) {}
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
    colors: ColorPalette,
    typography: TypographySet,
    spacing: SpacingSet,
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
                    },
                )
            },
        shape = RoundedCornerShape(spacing.panelCornerRadius),
        colors = CardDefaults.cardColors(containerColor = colors.statusCard),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = timeText,
                    style = TextStyle(fontSize = typography.displaySize, fontWeight = FontWeight.Bold),
                    color = colors.primaryText,
                    modifier = Modifier
                        .testTag("home_clock_text")
                        .pointerInput(onClockTap) { detectTapGestures(onTap = { onClockTap() }) },
                )
                Text(
                    text = "EasyUI Home",
                    style = TextStyle(fontSize = typography.supportSize, fontWeight = FontWeight.Medium),
                    color = colors.secondaryText,
                )
                if (accessCueVisible) {
                    Text(
                        text = "Access detected",
                        style = TextStyle(fontSize = typography.supportSize, fontWeight = typography.fontWeight),
                        color = colors.accent,
                        modifier = Modifier.testTag("caregiver_access_cue"),
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$batteryPercent · $chargingLabel",
                    style = TextStyle(fontSize = typography.labelSize, fontWeight = typography.fontWeight),
                    color = colors.primaryText,
                )
                Text(
                    text = "$signalLabel · $simLabel · $wifiLabel",
                    style = TextStyle(fontSize = typography.supportSize, fontWeight = typography.fontWeight),
                    color = colors.secondaryText,
                )
            }
        }
    }
}
