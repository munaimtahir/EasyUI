package com.easyui.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.core.ui.theme.ColorPalette
import com.easyui.core.ui.theme.SpacingSet
import com.easyui.core.ui.theme.TileStyle
import com.easyui.core.ui.theme.TypographySet

@Composable
fun LargeActionTile(
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit,
    palette: ColorPalette,
    typography: TypographySet,
    spacing: SpacingSet,
    tileStyle: TileStyle,
    avatarImageUri: String? = null,
    avatarFallback: String? = null,
    highlighted: Boolean = false,
    showSubtitle: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val titleColor = if (highlighted) palette.sosColor else palette.primaryText
    val subtitleColor = if (highlighted) palette.sosColor else palette.secondaryText
    val iconTint = when {
        highlighted -> palette.sosColor
        title.equals("Phone", ignoreCase = true) -> palette.successColor
        title.equals("Health Info", ignoreCase = true) -> palette.accent
        else -> palette.accent
    }
    val containerColor = when {
        !enabled -> palette.tileBackgroundMuted
        highlighted -> palette.accentMuted
        else -> palette.tileBackground
    }
    val borderColor = when {
        highlighted -> palette.sosColor.copy(alpha = 0.24f)
        else -> palette.outline.copy(alpha = 0.92f)
    }

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics { role = Role.Button },
        shape = RoundedCornerShape(spacing.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(width = tileStyle.borderWidthDp.dp, color = borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = tileStyle.elevationDp.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (highlighted) palette.accentMuted.copy(alpha = 0.95f) else palette.surface.copy(alpha = 0.92f),
                            containerColor,
                        ),
                    ),
                )
                .padding(spacing.padding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.tileSpacing / 2f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.tileSpacing / 2f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TileBadge(
                        iconTint = iconTint,
                        title = title,
                        palette = palette,
                        highlighted = highlighted,
                        enabled = enabled,
                    )
                    if (avatarFallback != null) {
                        AvatarBadge(
                            imageUri = avatarImageUri,
                            fallbackText = avatarFallback,
                            modifier = Modifier.size(56.dp),
                        )
                    }
                }
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = when {
                            title.length > 14 -> typography.labelSize
                            title.length >= 9 -> typography.titleSize * 0.84f
                            else -> typography.titleSize
                        },
                        fontWeight = typography.fontWeight,
                    ),
                    color = titleColor,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(spacing.tileSpacing / 3f)) {
                if (showSubtitle) {
                    Text(
                        text = subtitle,
                        style = TextStyle(
                            fontSize = typography.supportSize,
                            fontWeight = FontWeight.Normal,
                        ),
                        color = subtitleColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = if (enabled) "Tap to open" else "Unavailable on this device",
                    style = TextStyle(
                        fontSize = typography.supportSize,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = if (enabled) palette.accent else palette.secondaryText,
                )
            }
        }
    }
}

@Composable
private fun TileBadge(
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    palette: ColorPalette,
    highlighted: Boolean,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    highlighted -> palette.sosColor.copy(alpha = 0.14f)
                    enabled -> palette.accentMuted
                    else -> palette.surfaceMuted
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = easyUiIconForLabel(title),
            contentDescription = null,
            tint = if (enabled) iconTint else palette.secondaryText,
            modifier = Modifier.size(22.dp),
        )
    }
}
