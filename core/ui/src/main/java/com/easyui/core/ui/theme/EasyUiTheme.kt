package com.easyui.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F4C5C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0ECF4),
    onPrimaryContainer = Color(0xFF072A33),
    secondary = Color(0xFF6A7A2B),
    onSecondary = Color.White,
    background = Color(0xFFF6F4EE),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE8E1D3),
    onSurfaceVariant = Color(0xFF47473F),
    error = Color(0xFF8C1D18),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9BD3E2),
    onPrimary = Color(0xFF003640),
    secondary = Color(0xFFC5D488),
    onSecondary = Color(0xFF2F3C00),
    background = Color(0xFF111416),
    onBackground = Color(0xFFE3E2DE),
    surface = Color(0xFF1B1F21),
    onSurface = Color(0xFFE3E2DE),
    surfaceVariant = Color(0xFF3C4448),
    onSurfaceVariant = Color(0xFFC0C7CB),
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
