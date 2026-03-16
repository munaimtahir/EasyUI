package com.easyui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.easyui.core.domain.model.TileDisplayModel
import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.rules.HomeReadabilityRules
import com.easyui.core.ui.components.LargeActionTile
import com.easyui.core.ui.components.SectionHeader
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun HomeScreen(
    timeText: String,
    dateText: String,
    tiles: List<TileDisplayModel>,
    readabilityPreset: HomeReadabilityPreset,
    verySimpleModeEnabled: Boolean,
    fallbackTitle: String?,
    fallbackBody: String?,
    onTileClick: (String) -> Unit,
    onCaregiverToolsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val config = HomeReadabilityRules.config(readabilityPreset, verySimpleModeEnabled)
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyUiSpacing.lg * config.outerPaddingScale)
                .testTag("home_screen"),
            verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md * config.tileSpacingScale),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
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
                }
            }
            SectionHeader(text = if (verySimpleModeEnabled) "Simple Home" else "Home")
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(config.columns),
                verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md * config.tileSpacingScale),
                horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md * config.tileSpacingScale),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(min = EasyUiSpacing.xl * 6),
            ) {
                items(tiles, key = { it.id }) { tile ->
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
            Button(onClick = onCaregiverToolsClick, modifier = Modifier.fillMaxWidth()) {
                Text("Caregiver Tools")
            }
        }
    }
}
