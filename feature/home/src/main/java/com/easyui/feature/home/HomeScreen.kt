package com.easyui.feature.home

import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectTapGestures
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.domain.rules.HomeLayoutRules
import com.easyui.core.domain.rules.HomeReadabilityRules
import com.easyui.core.ui.components.LargeActionTile
import com.easyui.core.ui.components.SectionHeader
import com.easyui.core.ui.theme.EasyUiSpacing

private const val CaregiverTapThreshold = 5
private const val CaregiverTapWindowMs = 3_500L

@Composable
fun HomeScreen(
    timeText: String,
    dateText: String,
    batterySummary: String?,
    pages: List<List<TileDisplayModel?>>,
    readabilityPreset: HomeReadabilityPreset,
    verySimpleModeEnabled: Boolean,
    fallbackTitle: String?,
    fallbackBody: String?,
    onTileClick: (String) -> Unit,
    onCaregiverAccessRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val config = HomeReadabilityRules.config(readabilityPreset, verySimpleModeEnabled)
    val displayPages = remember(pages) {
        if (pages.isEmpty()) {
            listOf(List(HomeLayoutRules.SLOTS_PER_PAGE) { null })
        } else {
            pages
        }
    }
    val pagerState = rememberPagerState(pageCount = { displayPages.size })
    var caregiverTapCount by remember { mutableIntStateOf(0) }
    var lastCaregiverTapAt by remember { mutableLongStateOf(0L) }

    fun registerCaregiverTap() {
        val now = SystemClock.elapsedRealtime()
        caregiverTapCount =
            if (now - lastCaregiverTapAt <= CaregiverTapWindowMs) {
                caregiverTapCount + 1
            } else {
                1
            }
        lastCaregiverTapAt = now
        if (caregiverTapCount >= CaregiverTapThreshold) {
            caregiverTapCount = 0
            lastCaregiverTapAt = 0L
            onCaregiverAccessRequested()
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg * config.outerPaddingScale)
                .testTag("home_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md * config.tileSpacingScale),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_header")
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { registerCaregiverTap() })
                    },
            ) {
                Column(
                    modifier = Modifier.padding(EasyUiSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                ) {
                    Text(
                        timeText,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize * config.timeScale,
                            lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * config.timeScale,
                        ),
                    )
                    Text(
                        dateText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize * config.titleScale,
                            lineHeight = MaterialTheme.typography.titleLarge.lineHeight * config.titleScale,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (batterySummary != null) {
                        Text(
                            batterySummary,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize * config.subtitleScale,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * config.subtitleScale,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("battery_summary"),
                        )
                    }
                }
            }

            SectionHeader(text = if (verySimpleModeEnabled) "Simple Home" else "Home")

            if (displayPages.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_page_indicator"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(displayPages.size) { pageIndex ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (pageIndex == pagerState.currentPage) 12.dp else 10.dp)
                                .testTag("home_page_dot_$pageIndex"),
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = if (pageIndex == pagerState.currentPage) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                            ) {}
                        }
                    }
                }
            }

            if (fallbackTitle != null && fallbackBody != null) {
                Card(modifier = Modifier.fillMaxWidth().testTag("home_fallback_card")) {
                    Column(
                        modifier = Modifier.padding(EasyUiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                    ) {
                        Text(
                            fallbackTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = MaterialTheme.typography.titleLarge.fontSize * config.titleScale,
                            ),
                        )
                        Text(
                            fallbackBody,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize * config.subtitleScale,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * config.subtitleScale,
                            ),
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(min = EasyUiSpacing.xl * 6),
            ) { pageIndex ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(config.columns),
                    verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md * config.tileSpacingScale),
                    horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md * config.tileSpacingScale),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_page_$pageIndex"),
                ) {
                    itemsIndexed(displayPages[pageIndex], key = { slotIndex, tile -> tile?.id ?: "slot-$pageIndex-$slotIndex" }) { slotIndex, tile ->
                        if (tile == null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .testTag("empty_slot_${pageIndex}_$slotIndex"),
                            )
                        } else {
                            LargeActionTile(
                                title = tile.title,
                                subtitle = tile.subtitle,
                                enabled = tile.enabled,
                                avatarImageUri = tile.avatarImageUri,
                                avatarFallback = tile.avatarFallback,
                                titleScale = config.titleScale,
                                subtitleScale = config.subtitleScale,
                                onClick = { onTileClick(tile.id) },
                            )
                        }
                    }
                }
            }

            if (displayPages.size > 1) {
                Text(
                    text = "Page ${pagerState.currentPage + 1} of ${displayPages.size}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize * config.subtitleScale,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
