package com.easyui.core.domain.model

enum class ProtectedAction {
    OPEN_CAREGIVER_SETTINGS,
    MANAGE_LAYOUT_PAGES,
    MANAGE_ALLOWED_APPS,
    MANAGE_FAVORITE_CONTACTS,
    RESET_LAUNCHER,
    CHANGE_PIN,
    TOGGLE_PROTECTION,
    TOGGLE_LAYOUT_LOCK,
    MANAGE_HIDDEN_APPS,
}

enum class AppVisibilityPreset {
    CUSTOM,
    ESSENTIALS_ONLY,
    MINIMAL_COMMON_APPS,
}

enum class HomeReadabilityPreset {
    STANDARD,
    LARGER_TEXT,
    LARGER_TILES,
    EXTRA_SIMPLE_SPACING,
}

data class PinCredential(
    val saltHex: String,
    val hashHex: String,
)

data class PinValidationResult(
    val valid: Boolean,
    val message: String? = null,
)
