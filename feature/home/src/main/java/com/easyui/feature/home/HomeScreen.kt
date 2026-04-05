package com.easyui.feature.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.TileDisplayKind
import com.easyui.core.domain.model.TileDisplayModel
import kotlinx.coroutines.withTimeoutOrNull

private const val TopBarLongPressMs = 3_000L

@Composable
fun HomeScreen(
    timeText: String,
    dateText: String,
    tiles: List<TileDisplayModel>,
    skinConfig: SkinConfig,
    onTileClick: (String) -> Unit,
    onOpenAppList: () -> Unit,
    onStatusBarLongPress: () -> Unit,
    onClockTapped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    val tileSlots = List(6) { index -> tiles.getOrNull(index) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = SeniorHomeTokens.backgroundBottom,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SeniorHomeTokens.backgroundBrush)
                .testTag("home_screen"),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = SeniorHomeTokens.pageHorizontalPadding,
                        end = SeniorHomeTokens.pageHorizontalPadding,
                        top = safeDrawingPadding.calculateTopPadding() + SeniorHomeTokens.topSafeSpacing,
                        bottom = safeDrawingPadding.calculateBottomPadding() + SeniorHomeTokens.bottomSpacing,
                    ),
                verticalArrangement = Arrangement.spacedBy(SeniorHomeTokens.sectionGap),
            ) {
                HomeHeaderCard(
                    timeText = timeText,
                    dateText = dateText,
                    accessibilityMode = skinConfig.accessibilityMode,
                    onLongPressConfirmed = onStatusBarLongPress,
                    onClockTapped = onClockTapped,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SeniorHomeTokens.gridGap),
                ) {
                    repeat(3) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(SeniorHomeTokens.gridGap),
                        ) {
                            repeat(2) { column ->
                                val index = (row * 2) + column
                                val tile = tileSlots[index]
                                if (tile != null) {
                                    HomeActionTile(
                                        tile = tile,
                                        accessibilityMode = skinConfig.accessibilityMode,
                                        onClick = { onTileClick(tile.id) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                    )
                                } else {
                                    Box(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = onOpenAppList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = SeniorHomeTokens.minimumTargetSize)
                        .testTag("home_all_apps_button"),
                    shape = RoundedCornerShape(SeniorHomeTokens.cornerRadius),
                ) {
                    Text(
                        text = "All Apps",
                        color = SeniorHomeTokens.textPrimary,
                        fontSize = when (skinConfig.accessibilityMode) {
                            AccessibilityMode.BOLD_ACCESSIBILITY -> 24.sp
                            else -> 22.sp
                        },
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeaderCard(
    timeText: String,
    dateText: String,
    accessibilityMode: AccessibilityMode,
    onLongPressConfirmed: () -> Unit,
    onClockTapped: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
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
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPressConfirmed()
                        }
                    },
                )
            },
        shape = RoundedCornerShape(SeniorHomeTokens.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = SeniorHomeTokens.headerBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = SeniorHomeTokens.headerHorizontalPadding,
                    vertical = SeniorHomeTokens.headerVerticalPadding,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = timeText,
                color = SeniorHomeTokens.textPrimary,
                fontSize = when (accessibilityMode) {
                    AccessibilityMode.BOLD_ACCESSIBILITY -> SeniorHomeTokens.timeTextSizeLarge
                    else -> SeniorHomeTokens.timeTextSize
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .testTag("home_clock_text")
                    .pointerInput(onClockTapped) {
                        detectTapGestures(onTap = { onClockTapped() })
                    },
            )
            Text(
                text = dateText,
                color = SeniorHomeTokens.textSecondaryOnHeader,
                fontSize = when (accessibilityMode) {
                    AccessibilityMode.BOLD_ACCESSIBILITY -> SeniorHomeTokens.dateTextSizeLarge
                    else -> SeniorHomeTokens.dateTextSize
                },
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun HomeActionTile(
    tile: TileDisplayModel,
    accessibilityMode: AccessibilityMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "homeTileScale",
    )
    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .defaultMinSize(
                minWidth = SeniorHomeTokens.minimumTargetSize,
                minHeight = SeniorHomeTokens.minimumTargetSize,
            )
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .semantics {
                role = Role.Button
                contentDescription = tile.title
            }
            .testTag("home_tile_${tile.id}"),
        shape = RoundedCornerShape(SeniorHomeTokens.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = SeniorHomeTokens.tileColor(tile.kind)),
        border = if (isFocused) {
            androidx.compose.foundation.BorderStroke(SeniorHomeTokens.focusRingWidth, SeniorHomeTokens.focusRing)
        } else {
            null
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = SeniorHomeTokens.tileHorizontalPadding,
                    vertical = SeniorHomeTokens.tileVerticalPadding,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = SeniorHomeTokens.tileIcon(tile.kind),
                contentDescription = null,
                tint = SeniorHomeTokens.textPrimary,
                modifier = Modifier.size(SeniorHomeTokens.iconSize),
            )
            Text(
                text = tile.title,
                color = SeniorHomeTokens.textPrimary,
                fontSize = when (accessibilityMode) {
                    AccessibilityMode.BOLD_ACCESSIBILITY -> SeniorHomeTokens.labelTextSizeLarge
                    else -> SeniorHomeTokens.labelTextSize
                },
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1238)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        timeText = "9:41",
        dateText = "Friday, March 20",
        tiles = previewTiles(),
        skinConfig = SkinConfig(),
        onTileClick = {},
        onOpenAppList = {},
        onStatusBarLongPress = {},
        onClockTapped = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1238, fontScale = 1.3f)
@Composable
private fun HomeScreenLargeTextPreview() {
    HomeScreen(
        timeText = "9:41",
        dateText = "Friday, March 20",
        tiles = previewTiles(),
        skinConfig = SkinConfig(accessibilityMode = AccessibilityMode.BOLD_ACCESSIBILITY),
        onTileClick = {},
        onOpenAppList = {},
        onStatusBarLongPress = {},
        onClockTapped = {},
    )
}

private fun previewTiles(): List<TileDisplayModel> =
    listOf(
        TileDisplayModel("phone", "Phone", "Phone", true, TileDisplayKind.PHONE),
        TileDisplayModel("messages", "Messages", "Messages", true, TileDisplayKind.MESSAGES),
        TileDisplayModel("contacts", "Contacts", "Contacts", true, TileDisplayKind.CONTACTS),
        TileDisplayModel("photos", "Photos", "Photos", true, TileDisplayKind.PHOTOS),
        TileDisplayModel("camera", "Camera", "Camera", true, TileDisplayKind.CAMERA),
        TileDisplayModel("emergency", "Emergency", "Emergency", true, TileDisplayKind.EMERGENCY),
    )
