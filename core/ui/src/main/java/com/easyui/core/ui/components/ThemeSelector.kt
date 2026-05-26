package com.easyui.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.model.VisualTheme
import com.easyui.core.ui.theme.EasyUiSpacing

@Composable
fun ThemeSelector(
    visualTheme: VisualTheme,
    accessibilityMode: AccessibilityMode,
    onSelectVisualTheme: (VisualTheme) -> Unit,
    onSelectAccessibilityMode: (AccessibilityMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
    ) {
        ThemeChoiceCard(
            label = "Dark",
            description = "High contrast dark mode, easier on the eyes in low light.",
            selected = accessibilityMode != AccessibilityMode.HIGH_CONTRAST && visualTheme == VisualTheme.DARK_COMFORT,
            swatch = Color(0xFF161A1B),
            onClick = {
                onSelectAccessibilityMode(AccessibilityMode.NONE)
                onSelectVisualTheme(VisualTheme.DARK_COMFORT)
            },
        )
        ThemeChoiceCard(
            label = "Light",
            description = "Classic light mode with clear black text on bright background.",
            selected = accessibilityMode != AccessibilityMode.HIGH_CONTRAST && visualTheme == VisualTheme.LIGHT_PREMIUM,
            swatch = Color(0xFFF6F1E8),
            onClick = {
                onSelectAccessibilityMode(AccessibilityMode.NONE)
                onSelectVisualTheme(VisualTheme.LIGHT_PREMIUM)
            },
        )
        ThemeChoiceCard(
            label = "High Contrast",
            description = "Maximum visibility with bold outlines and strong colors.",
            selected = accessibilityMode == AccessibilityMode.HIGH_CONTRAST,
            swatch = Color(0xFFFFFFFF),
            onClick = {
                onSelectAccessibilityMode(AccessibilityMode.HIGH_CONTRAST)
            },
        )
        ThemeChoiceCard(
            label = "Auto",
            description = "Follows the phone's system theme (Light or Dark).",
            selected = accessibilityMode != AccessibilityMode.HIGH_CONTRAST && visualTheme == VisualTheme.AUTO,
            swatch = Color(0xFF808080),
            onClick = {
                onSelectAccessibilityMode(AccessibilityMode.NONE)
                onSelectVisualTheme(VisualTheme.AUTO)
            },
        )
    }
}

@Composable
private fun ThemeChoiceCard(
    label: String,
    description: String,
    selected: Boolean,
    swatch: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = if (selected) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("theme_choice_${label.lowercase().replace(' ', '_')}"),
        colors = colors,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EasyUiSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(EasyUiSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(0.dp)
                    .let { it },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(swatch),
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(EasyUiSpacing.xs)) {
                Text(label, style = MaterialTheme.typography.titleLarge)
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
            if (selected) {
                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = "Selected")
            }
        }
    }
}