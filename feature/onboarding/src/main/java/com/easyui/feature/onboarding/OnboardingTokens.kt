package com.easyui.feature.onboarding

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object OnboardingTokens {
    val backgroundTop = Color(0xFF101848)
    val backgroundMid = Color(0xFF1A1E63)
    val backgroundBottom = Color(0xFF4B1C7D)

    val heroCard = Color(0xFFFAF5EB)
    val heroCardOutline = Color(0x33FFFFFF)
    val heroPrimary = Color(0xFF1C2442)
    val heroSecondary = Color(0xFF55607F)

    val setupCardBlue = Color(0xFF5D9BFF)
    val setupCardGreen = Color(0xFF57D64E)
    val setupCardOrange = Color(0xFFF29A3A)
    val setupCardRed = Color(0xFFE84D64)
    val setupCardPurple = Color(0xFF9B5DE5)

    val chipSurface = Color(0x26FFFFFF)
    val chipText = Color.White
    val primaryButton = Color(0xFFF29A3A)
    val primaryButtonText = Color(0xFF2B1A05)
    val secondaryButton = Color(0x24FFFFFF)
    val secondaryButtonBorder = Color(0x40FFFFFF)
    val secondaryButtonText = Color.White
    val outline = Color(0x26FFFFFF)
    val screenText = Color.White
    val screenTextMuted = Color(0xFFE7E4F5)

    val cornerRadius = 28.dp
    val cardCornerRadius = 24.dp
    val chipCornerRadius = 20.dp
    val pagePadding = 24.dp
    val sectionSpacing = 16.dp
    val heroSpacing = 20.dp
    val bottomSpacing = 18.dp
    val miniTileHeight = 82.dp

    val backgroundBrush: Brush
        get() = Brush.verticalGradient(
            colors = listOf(backgroundTop, backgroundMid, backgroundBottom),
        )
}
