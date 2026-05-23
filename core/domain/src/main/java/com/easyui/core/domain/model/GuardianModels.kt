package com.easyui.core.domain.model

enum class GuardianCheckType {
    BATTERY_LOW,
    BATTERY_CRITICAL,
    NO_INTERNET,
    NOT_DEFAULT_LAUNCHER,
    EMERGENCY_CONTACT_MISSING,
    LAYOUT_LOCK_DISABLED,
    PERMISSIONS_MISSING,
    SETUP_INCOMPLETE
}

enum class GuardianCheckStatus {
    OK,
    WARNING,
    CRITICAL
}

data class GuardianCheckResult(
    val type: GuardianCheckType,
    val status: GuardianCheckStatus,
    val message: String,
    val detail: String? = null,
    val recoveryGuidance: RecoveryGuidance? = null
)

data class PhoneHealthState(
    val checks: List<GuardianCheckResult>,
    val overallStatus: GuardianCheckStatus,
    val primaryMessage: String,
    val shouldPromptAlert: Boolean = false,
    val primaryRecoveryGuidance: RecoveryGuidance? = null
)

data class SetupCompletenessItem(
    val id: String,
    val label: String,
    val isComplete: Boolean,
    val isRequired: Boolean,
    val actionLabel: String? = null
)

data class SetupCompleteness(
    val items: List<SetupCompletenessItem>,
    val score: Float // 0.0 to 1.0
)
