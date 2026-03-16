package com.easyui.core.domain.model

enum class ProtectedAction {
    ENTER_EDIT_MODE,
    MANAGE_HOME_DISPLAY,
    MANAGE_APP_VISIBILITY,
    MANAGE_FAVORITE_CONTACTS,
    RESET_LAUNCHER,
    CHANGE_PIN,
    TOGGLE_PROTECTION,
    TOGGLE_LAYOUT_LOCK,
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
