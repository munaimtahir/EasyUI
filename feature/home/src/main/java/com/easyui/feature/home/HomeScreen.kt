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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.launch
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import com.easyui.core.ui.theme.EasyUiSpacing
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
    pageCount: Int = 1,
    layoutLocked: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scope = rememberCoroutineScope()
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    val slotsPerPage = 6
    val totalSlots = slotsPerPage * pageCount
    val allTileSlots = List(totalSlots) { index -> tiles.getOrNull(index) }

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
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    userScrollEnabled = pageCount > 1,
                    pageSpacing = SeniorHomeTokens.gridGap
                ) { page ->
                    val pageStartIndex = page * slotsPerPage
                    val pageEndIndex = (page + 1) * slotsPerPage
                    val currentPageTiles = allTileSlots.subList(pageStartIndex, pageEndIndex)
                    val tileSlots = List(6) { index -> currentPageTiles.getOrNull(index) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
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
                                            layoutLocked = layoutLocked,
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
                }
                
                // Page navigation UI (only show if multiple pages)
                if (pageCount > 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Page indicator dots
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_page_indicators"),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            repeat(pageCount) { pageIndex ->
                                Box(
                                    modifier = Modifier
                                        .size(if (pageIndex == pagerState.currentPage) 12.dp else 8.dp)
                                        .background(
                                            color = if (pageIndex == pagerState.currentPage) {
                                                SeniorHomeTokens.textPrimary
                                            } else {
                                                SeniorHomeTokens.textPrimary.copy(alpha = 0.3f)
                                            },
                                            shape = CircleShape,
                                        )
                                        .testTag("home_page_indicator_$pageIndex"),
                                )
                                if (pageIndex < pageCount - 1) {
                                    Box(modifier = Modifier.width(8.dp))
                                }
                            }
                        }
                        
                        // Page navigation buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_page_navigation"),
                            horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                        ) {
                            OutlinedButton(
                                onClick = { if (pagerState.currentPage > 0) scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                                enabled = pagerState.currentPage > 0,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = SeniorHomeTokens.minimumTargetSize)
                                    .testTag("home_page_previous"),
                                shape = RoundedCornerShape(SeniorHomeTokens.cornerRadius),
                            ) {
                                Text(
                                    text = "← Previous",
                                    color = if (pagerState.currentPage > 0) {
                                        SeniorHomeTokens.textPrimary
                                    } else {
                                        SeniorHomeTokens.textPrimary.copy(alpha = 0.5f)
                                    },
                                    fontSize = when (skinConfig.accessibilityMode) {
                                        AccessibilityMode.BOLD_ACCESSIBILITY -> 20.sp
                                        else -> 18.sp
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            OutlinedButton(
                                onClick = { if (pagerState.currentPage < pageCount - 1) scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                                enabled = pagerState.currentPage < pageCount - 1,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = SeniorHomeTokens.minimumTargetSize)
                                    .testTag("home_page_next"),
                                shape = RoundedCornerShape(SeniorHomeTokens.cornerRadius),
                            ) {
                                Text(
                                    text = "Next →",
                                    color = if (pagerState.currentPage < pageCount - 1) {
                                        SeniorHomeTokens.textPrimary
                                    } else {
                                        SeniorHomeTokens.textPrimary.copy(alpha = 0.5f)
                                    },
                                    fontSize = when (skinConfig.accessibilityMode) {
                                        AccessibilityMode.BOLD_ACCESSIBILITY -> 20.sp
                                        else -> 18.sp
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                )
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
                    onTap = { onClockTapped() },
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
    layoutLocked: Boolean = false,
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

    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = SeniorHomeTokens.minimumTargetSize,
                minHeight = SeniorHomeTokens.minimumTargetSize,
            )
    ) {
        Card(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxSize()
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
        
        // Lock icon (top-right corner)
        if (layoutLocked) {
            Box(
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .padding(2.dp),
            ) {
                Text(
                    text = "🔒",
                    fontSize = 10.sp,
                )
            }
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
        pageCount = 1,
        layoutLocked = false,
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
        pageCount = 1,
        layoutLocked = false,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1238)
@Composable
private fun HomeScreenLockedPreview() {
    HomeScreen(
        timeText = "9:41",
        dateText = "Friday, March 20",
        tiles = previewTiles(),
        skinConfig = SkinConfig(),
        onTileClick = {},
        onOpenAppList = {},
        onStatusBarLongPress = {},
        onClockTapped = {},
        pageCount = 1,
        layoutLocked = true,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1238)
@Composable
private fun HomeScreenMultiPagePreview() {
    HomeScreen(
        timeText = "9:41",
        dateText = "Friday, March 20",
        tiles = previewTilesMultiPage(),
        skinConfig = SkinConfig(),
        onTileClick = {},
        onOpenAppList = {},
        onStatusBarLongPress = {},
        onClockTapped = {},
        pageCount = 2,
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

private fun previewTilesMultiPage(): List<TileDisplayModel> =
    listOf(
        TileDisplayModel("phone", "Phone", "Phone", true, TileDisplayKind.PHONE),
        TileDisplayModel("messages", "Messages", "Messages", true, TileDisplayKind.MESSAGES),
        TileDisplayModel("contacts", "Contacts", "Contacts", true, TileDisplayKind.CONTACTS),
        TileDisplayModel("photos", "Photos", "Photos", true, TileDisplayKind.PHOTOS),
        TileDisplayModel("camera", "Camera", "Camera", true, TileDisplayKind.CAMERA),
        TileDisplayModel("emergency", "Emergency", "Emergency", true, TileDisplayKind.EMERGENCY),
        TileDisplayModel("app1", "News", "News", true, TileDisplayKind.PHONE),
        TileDisplayModel("app2", "Games", "Games", true, TileDisplayKind.MESSAGES),
        TileDisplayModel("app3", "Music", "Music", true, TileDisplayKind.CONTACTS),
        TileDisplayModel("app4", "Videos", "Videos", true, TileDisplayKind.PHOTOS),
        TileDisplayModel("app5", "Gallery", "Gallery", true, TileDisplayKind.CAMERA),
        TileDisplayModel("app6", "Settings", "Settings", true, TileDisplayKind.EMERGENCY),
    )
