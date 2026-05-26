package com.easyui.feature.caregiver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun HomeLayoutPreview(
    pages: List<List<HomeTile?>>,
    currentPageIndex: Int = 0,
    layoutLocked: Boolean = false,
    readabilityPreset: HomeReadabilityPreset = HomeReadabilityPreset.STANDARD,
    modifier: Modifier = Modifier,
) {
    val currentPage = pages.getOrElse(currentPageIndex) { emptyList() }
    val tileSlots = List(6) { index -> currentPage.getOrNull(index) }
    
    val bgColor = Color(0xFF0D1238)
    val tileSize = 48.dp
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(EasyUiSpacing.md),
        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0xFF1A1F3A), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "9:41 • Friday, March 20",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        
        // Tile grid (2x3)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
        ) {
            repeat(3) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs),
                ) {
                    repeat(2) { column ->
                        val index = (row * 2) + column
                        val tile = tileSlots[index]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(tileSize)
                                .background(
                                    color = if (tile != null) Color(0xFF4A5FBD) else Color(0xFF2A3050),
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .testTag("preview_tile_$index"),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (tile != null) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = tile.title.take(1),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // All Apps button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color(0xFF2A3050), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "All Apps",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun HomeLayoutPreviewCard(
    pages: List<List<HomeTile?>>,
    currentPageIndex: Int = 0,
    pageCount: Int = 1,
    layoutLocked: Boolean = false,
    readabilityPreset: HomeReadabilityPreset = HomeReadabilityPreset.STANDARD,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_layout_preview_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyUiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
        ) {
            Text(
                text = "Home Layout Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            
            HomeLayoutPreview(
                pages = pages,
                currentPageIndex = currentPageIndex,
                layoutLocked = layoutLocked,
                readabilityPreset = readabilityPreset,
                modifier = Modifier.fillMaxWidth(),
            )
            
            // Status info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                    .padding(EasyUiSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Page ${currentPageIndex + 1} of $pageCount",
                    style = MaterialTheme.typography.bodySmall,
                )
                if (layoutLocked) {
                    Text(
                        text = "🔒 Layout Locked",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
