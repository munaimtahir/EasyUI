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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    onAlertCaregiver: () -> Unit = {},
    healthState: com.easyui.core.domain.model.PhoneHealthState = com.easyui.core.domain.model.PhoneHealthState(
        checks = emptyList(),
        overallStatus = com.easyui.core.domain.model.GuardianCheckStatus.OK,
        primaryMessage = "Phone is ready"
    ),
    batteryPercentage: Int? = null,
    isCharging: Boolean = false,
    isBatteryLow: Boolean = false,
    showBatteryInfo: Boolean = false,
    allAppsVisible: Boolean = true,
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
                    batteryPercentage = batteryPercentage,
                    isCharging = isCharging,
                    isBatteryLow = isBatteryLow,
                    showBatteryInfo = showBatteryInfo,
                    skinConfig = skinConfig,
                    onLongPressConfirmed = onStatusBarLongPress,
                    onClockTapped = onClockTapped,
                )
                PhoneHealthCard(
                    healthState = healthState,
                    skinConfig = skinConfig,
                    modifier = Modifier.fillMaxWidth()
                )
                if (healthState.shouldPromptAlert) {
                    SeniorAlertBanner(
                        message = healthState.primaryMessage,
                        onAlertClick = onAlertCaregiver,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_pager")
                        .weight(1f),
                    userScrollEnabled = pageCount > 1,
                    pageSpacing = SeniorHomeTokens.gridGap(skinConfig)
                ) { page ->
                    val pageStartIndex = page * slotsPerPage
                    val pageEndIndex = (page + 1) * slotsPerPage
                    val currentPageTiles = allTileSlots.subList(pageStartIndex, pageEndIndex)
                    val tileSlots = List(6) { index -> currentPageTiles.getOrNull(index) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(SeniorHomeTokens.gridGap(skinConfig)),
                    ) {
                        repeat(3) { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(SeniorHomeTokens.gridGap(skinConfig)),
                            ) {
                                repeat(2) { column ->
                                    val index = (row * 2) + column
                                    val tile = tileSlots[index]
                                    if (tile != null) {
                                        HomeActionTile(
                                            tile = tile,
                                            skinConfig = skinConfig,
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
                
                // Page indicator (only show if multiple pages)
                if (pageCount > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Page indicator dots
                        Row(
                            modifier = Modifier.testTag("home_page_indicators"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                            }
                        }
                    }
                }
                
                // All Apps button - visibility controlled by settings
                if (allAppsVisible) {
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
                            fontSize = SeniorHomeTokens.labelTextSize(skinConfig),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeaderCard(
    timeText: String,
    dateText: String,
    batteryPercentage: Int?,
    isCharging: Boolean,
    isBatteryLow: Boolean,
    showBatteryInfo: Boolean,
    skinConfig: SkinConfig,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (showBatteryInfo && isBatteryLow && !isCharging) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.BatteryAlert,
                        contentDescription = "Low Battery",
                        tint = SeniorHomeTokens.tileEmergency,
                        modifier = Modifier.size(24.dp).padding(end = 8.dp)
                    )
                }
                Text(
                    text = timeText,
                    color = SeniorHomeTokens.textPrimary,
                    fontSize = SeniorHomeTokens.timeTextSize(skinConfig),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .testTag("home_clock_text")
                        .pointerInput(onClockTapped) {
                            detectTapGestures(onTap = { onClockTapped() })
                        },
                )
            }
            Text(
                text = dateText,
                color = SeniorHomeTokens.textSecondaryOnHeader,
                fontSize = SeniorHomeTokens.dateTextSize(skinConfig),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            if (showBatteryInfo && batteryPercentage != null) {
                Text(
                    text = "Battery: $batteryPercentage%${if (isCharging) " (Charging)" else ""}",
                    color = if (isBatteryLow && !isCharging) SeniorHomeTokens.tileEmergency else SeniorHomeTokens.textSecondaryOnHeader,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun PhoneHealthCard(
    healthState: com.easyui.core.domain.model.PhoneHealthState,
    skinConfig: SkinConfig,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (healthState.overallStatus) {
        com.easyui.core.domain.model.GuardianCheckStatus.CRITICAL -> SeniorHomeTokens.tileEmergency.copy(alpha = 0.2f)
        com.easyui.core.domain.model.GuardianCheckStatus.WARNING -> Color.Yellow.copy(alpha = 0.15f)
        com.easyui.core.domain.model.GuardianCheckStatus.OK -> SeniorHomeTokens.headerBackground
    }
    
    val textColor = when (healthState.overallStatus) {
        com.easyui.core.domain.model.GuardianCheckStatus.CRITICAL -> SeniorHomeTokens.tileEmergency
        com.easyui.core.domain.model.GuardianCheckStatus.WARNING -> Color.Yellow
        com.easyui.core.domain.model.GuardianCheckStatus.OK -> SeniorHomeTokens.textSecondaryOnHeader
    }

    val icon = when (healthState.overallStatus) {
        com.easyui.core.domain.model.GuardianCheckStatus.CRITICAL -> "⚠️"
        com.easyui.core.domain.model.GuardianCheckStatus.WARNING -> "ℹ️"
        com.easyui.core.domain.model.GuardianCheckStatus.OK -> "✅"
    }

    Card(
        modifier = modifier.testTag("home_health_card"),
        shape = RoundedCornerShape(SeniorHomeTokens.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                modifier = Modifier.padding(end = 8.dp),
                fontSize = 18.sp
            )
            Text(
                text = healthState.primaryMessage,
                color = if (healthState.overallStatus == com.easyui.core.domain.model.GuardianCheckStatus.OK) SeniorHomeTokens.textPrimary else textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SeniorAlertBanner(
    message: String,
    onAlertClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.testTag("senior_alert_banner"),
        shape = RoundedCornerShape(SeniorHomeTokens.cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = SeniorHomeTokens.tileEmergency,
            contentColor = Color.White
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Phone Issue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = onAlertClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = SeniorHomeTokens.tileEmergency
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Alert Caregiver", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HomeActionTile(
    tile: TileDisplayModel,
    skinConfig: SkinConfig,
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
                    modifier = Modifier.size(SeniorHomeTokens.tileIconSize(skinConfig)),
                )
                Text(
                    text = tile.title,
                    color = SeniorHomeTokens.textPrimary,
                    fontSize = SeniorHomeTokens.labelTextSize(skinConfig),
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
