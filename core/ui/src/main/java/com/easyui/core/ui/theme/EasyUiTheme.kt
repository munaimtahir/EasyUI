package com.easyui.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.easyui.core.domain.model.AccessibilityMode
import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.VisualTheme

private val WarmLightColors = lightColorScheme(
    primary = Color(0xFF1B5E5A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8EEE8),
    onPrimaryContainer = Color(0xFF0D3A37),
    secondary = Color(0xFFC76E48),
    onSecondary = Color.White,
    background = Color(0xFFF6F1E8),
    onBackground = Color(0xFF1E2426),
    surface = Color(0xFFFFFCF7),
    onSurface = Color(0xFF1E2426),
    surfaceVariant = Color(0xFFE7DED0),
    onSurfaceVariant = Color(0xFF5D625F),
    error = Color(0xFF8C1D18),
)

private val MidnightIndigoColors = darkColorScheme(
    primary = Color(0xFFBCA5FF),
    onPrimary = Color(0xFF260085),
    primaryContainer = Color(0xFF411BA1),
    onPrimaryContainer = Color(0xFFE4DCFF),
    secondary = Color(0xFFFFB08D),
    onSecondary = Color(0xFF51220F),
    background = Color(0xFF101026),
    onBackground = Color(0xFFE3E1EB),
    surface = Color(0xFF181830),
    onSurface = Color(0xFFE3E1EB),
    surfaceVariant = Color(0xFF45434F),
    onSurfaceVariant = Color(0xFFC7C5D0),
    error = Color(0xFFFFB4AB),
)

@Composable
fun EasyUiTheme(
    skinConfig: SkinConfig = SkinConfig(),
    content: @Composable () -> Unit,
) {
    val colorScheme = resolveColorScheme(skinConfig)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = EasyUiTypography,
        content = content,
    )
}

private fun resolveColorScheme(config: SkinConfig): ColorScheme {
    if (config.accessibilityMode == AccessibilityMode.HIGH_CONTRAST) {
        return lightColorScheme(
            primary = Color(0xFF0033CC),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDCE6FF),
            onPrimaryContainer = Color(0xFF001A66),
            secondary = Color(0xFF111111),
            onSecondary = Color.White,
            background = Color(0xFFFFFFFF),
            onBackground = Color(0xFF000000),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF000000),
            surfaceVariant = Color(0xFFF2F2F2),
            onSurfaceVariant = Color(0xFF111111),
            error = Color(0xFFB00020),
        )
    }

    return when (config.visualTheme) {
        VisualTheme.DARK_COMFORT -> MidnightIndigoColors
        VisualTheme.SOFT_CALM -> darkColorScheme(
            primary = Color(0xFF4DB6AC),
            onPrimary = Color(0xFF00332E),
            primaryContainer = Color(0xFF005047),
            onPrimaryContainer = Color(0xFF82F7E8),
            secondary = Color(0xFF8DE3FF),
            onSecondary = Color(0xFF06202A),
            background = Color(0xFF102625),
            onBackground = Color(0xFFE8F1EF),
            surface = Color(0xFF163332),
            onSurface = Color(0xFFE8F1EF),
            surfaceVariant = Color(0xFF334244),
            onSurfaceVariant = Color(0xFFC3D0CF),
            error = Color(0xFFFFB4AB),
        )
        VisualTheme.CLINICAL_PROFESSIONAL -> lightColorScheme(
            primary = Color(0xFF1F5AA6),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD8E2FF),
            onPrimaryContainer = Color(0xFF001A44),
            secondary = Color(0xFF4A5F82),
            onSecondary = Color.White,
            background = Color(0xFFF7FAFF),
            onBackground = Color(0xFF0E141B),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0E141B),
            surfaceVariant = Color(0xFFE1E7F2),
            onSurfaceVariant = Color(0xFF3F4856),
            error = Color(0xFF8C1D18),
        )
        VisualTheme.LIGHT_PREMIUM -> WarmLightColors
    }
}
