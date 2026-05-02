package com.easyui.core.domain.model

data class SkinConfig(
    val layoutMode: LayoutMode = LayoutMode.SIMPLE_CLASSIC,
    val visualTheme: VisualTheme = VisualTheme.DARK_COMFORT,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.NONE,
)

enum class LayoutMode {
    SIMPLE_CLASSIC,
    VERY_SIMPLE,
    CARE_MODE,
    COMMUNICATION_MODE,
}

enum class VisualTheme {
    LIGHT_PREMIUM,
    DARK_COMFORT,
    CLINICAL_PROFESSIONAL,
    SOFT_CALM,
}

enum class AccessibilityMode {
    NONE,
    HIGH_CONTRAST,
    BOLD_ACCESSIBILITY,
}
