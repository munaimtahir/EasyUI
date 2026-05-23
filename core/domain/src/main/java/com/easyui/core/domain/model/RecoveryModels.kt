package com.easyui.core.domain.model

enum class RecoveryActionType {
    NONE,
    OPEN_WIFI_SETTINGS,
    OPEN_BATTERY_SETTINGS,
    SET_DEFAULT_LAUNCHER,
    OPEN_EMERGENCY_SETTINGS,
    OPEN_CAREGIVER_TOOLS,
    REQUEST_PERMISSIONS
}

data class RecoveryGuidance(
    val type: RecoveryActionType,
    val label: String,
    val description: String,
    val actionButtonLabel: String? = null
)
