package com.easyui.feature.onboarding

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object OnboardingTokens {
    val backgroundTop = Color(0xFFF6F1E8)
    val backgroundMid = Color(0xFFF2E9DD)
    val backgroundBottom = Color(0xFFE8DED0)

    val heroCard = Color(0xFFFFFCF7)
    val heroCardOutline = Color(0x331B5E5A)
    val heroPrimary = Color(0xFF1E2426)
    val heroSecondary = Color(0xFF5D625F)

    val setupCardBlue = Color(0xFF4F83F1)
    val setupCardGreen = Color(0xFF57D64E)
    val setupCardOrange = Color(0xFFF29A3A)
    val setupCardRed = Color(0xFFE84D64)
    val setupCardPurple = Color(0xFF9B5DE5)

    val chipSurface = Color(0xFFE7DED0)
    val chipText = Color(0xFF1E2426)
    val primaryButton = Color(0xFF1B5E5A)
    val primaryButtonText = Color.White
    val secondaryButton = Color(0xFFE7DED0)
    val secondaryButtonBorder = Color(0x331B5E5A)
    val secondaryButtonText = Color(0xFF1B5E5A)
    val outline = Color(0x331B5E5A)
    val screenText = Color(0xFF1E2426)
    val screenTextMuted = Color(0xFF5D625F)

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
