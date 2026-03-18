package com.easyui.core.domain.model

data class LauncherSettings(
    val onboardingComplete: Boolean = false,
    val emergencyPhoneNumber: String = "911",
    val emergencyNumbers: List<EmergencyNumber> = listOf(
        EmergencyNumber(label = "Ambulance", phoneNumber = "911"),
        EmergencyNumber(label = "Police", phoneNumber = "911"),
        EmergencyNumber(label = "Fire", phoneNumber = "911"),
    ),
    val sosNumbers: List<String> = emptyList(),
    val use24HourClock: Boolean = false,
    val caregiverProtectionEnabled: Boolean = false,
    val layoutLocked: Boolean = false,
    val easyUiLockEnabled: Boolean = false,
    val easyUiLockTimeoutSeconds: Int = 60,
    val pinSaltHex: String? = null,
    val pinHashHex: String? = null,
    val appVisibilityPreset: String = AppVisibilityPreset.CUSTOM.name,
    val homeReadabilityPreset: String = HomeReadabilityPreset.STANDARD.name,
    val verySimpleModeEnabled: Boolean = false,
    val showBatteryInfo: Boolean = false,
    val homePageCount: Int = 2,
    val healthInfo: HealthInfo = HealthInfo(),
    val skinConfig: SkinConfig = SkinConfig(),
)
