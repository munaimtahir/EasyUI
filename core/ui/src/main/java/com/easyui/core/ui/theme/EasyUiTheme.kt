package com.easyui.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
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

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7ED6C8),
    onPrimary = Color(0xFF072D2B),
    secondary = Color(0xFFFFB08D),
    onSecondary = Color(0xFF51220F),
    background = Color(0xFF161A1B),
    onBackground = Color(0xFFE8E3DA),
    surface = Color(0xFF202526),
    onSurface = Color(0xFFE8E3DA),
    surfaceVariant = Color(0xFF394142),
    onSurfaceVariant = Color(0xFFC2C8C5),
    error = Color(0xFFFFB4AB),
)

@Composable
fun EasyUiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = EasyUiTypography,
        content = content,
    )
}
