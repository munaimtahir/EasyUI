package com.easyui.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics { role = Role.Button },
        shape = RoundedCornerShape(spacing.cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) palette.tileBackground else palette.background,
        ),
        border = BorderStroke(width = tileStyle.borderWidthDp.dp, color = palette.accent.copy(alpha = 0.24f)),
        elevation = CardDefaults.cardElevation(defaultElevation = tileStyle.elevationDp.dp),
    ) {
        Column(
            modifier = Modifier.padding(spacing.padding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.tileSpacing / 2f)) {
                if (avatarFallback != null) {
                    AvatarBadge(
                        imageUri = avatarImageUri,
                        fallbackText = avatarFallback,
                        modifier = Modifier.size(56.dp),
                    )
                }
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = typography.labelSize,
                        fontWeight = typography.fontWeight,
                    ),
                    color = titleColor,
                    textAlign = TextAlign.Start,
                )
            }
            if (showSubtitle) {
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontSize = typography.bodySize,
                        fontWeight = FontWeight.Normal,
                    ),
                    color = subtitleColor,
                )
            }
        }
    }
}
