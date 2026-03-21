package com.easyui.feature.caregiver

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CaregiverDashboardTokens {
    val backgroundTop = Color(0xFF0D1238)
    val backgroundMid = Color(0xFF171746)
    val backgroundBottom = Color(0xFF27124E)

    val surfacePrimary = Color(0xFF1E234F)
    val surfaceSecondary = Color(0xFF252B5C)
    val surfaceElevated = Color(0xFF2D3570)
    val outlineSubtle = Color(0x1AFFFFFF)

    val accentPrimary = Color(0xFF5D9BFF)
    val accentSuccess = Color(0xFF57D64E)
    val accentWarning = Color(0xFFF29A3A)
    val accentDanger = Color(0xFFD92D20)
    val accentInfo = Color(0xFF33B7C8)

    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xC7FFFFFF)
    val textTertiary = Color(0x94FFFFFF)

    val buttonSecondary = Color(0xFF202753)

    val radius = 16.dp
    val buttonRadius = 15.dp
    val pageHorizontalPadding = 16.dp
    val sectionGap = 12.dp
    val cardPadding = 16.dp
    val chipHorizontalPadding = 10.dp
    val chipVerticalPadding = 6.dp
    val iconContainerSize = 42.dp
    val dashboardCardMinHeight = 156.dp

    val titleSize = 24.sp
    val sectionTitleSize = 17.sp
    val bodySize = 14.sp
    val helperSize = 12.sp

    val backgroundBrush: Brush
        get() = Brush.verticalGradient(
            colors = listOf(backgroundTop, backgroundMid, backgroundBottom),
        )
}
