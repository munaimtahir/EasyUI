package com.easyui.core.domain.rules

import com.easyui.core.domain.model.GuardianCheckResult
import com.easyui.core.domain.model.GuardianCheckStatus
import com.easyui.core.domain.model.GuardianCheckType
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.PhoneHealthState
import com.easyui.core.domain.model.RecoveryActionType
import com.easyui.core.domain.model.RecoveryGuidance
import com.easyui.core.domain.model.SetupCompleteness
import com.easyui.core.domain.model.SetupCompletenessItem

object GuardianRules {

    fun calculatePhoneHealthState(
        settings: LauncherSettings,
        batteryPercentage: Int?,
        isCharging: Boolean,
        isInternetAvailable: Boolean,
        isDefaultLauncher: Boolean,
        isBatteryOptimized: Boolean,
        hasRequiredPermissions: Boolean,
        setupCompleteness: SetupCompleteness
    ): PhoneHealthState {
        val checks = mutableListOf<GuardianCheckResult>()

        // 1. Critical Battery
        if (settings.batteryLowCheckEnabled && batteryPercentage != null && !isCharging) {
            if (batteryPercentage <= settings.batteryCriticalThreshold) {
                checks.add(
                    GuardianCheckResult(
                        type = GuardianCheckType.BATTERY_CRITICAL,
                        status = GuardianCheckStatus.CRITICAL,
                        message = "Please charge phone",
                        detail = "Battery is very low ($batteryPercentage%)",
                        recoveryGuidance = RecoveryGuidance(
                            type = RecoveryActionType.NONE,
                            label = "Battery Critical",
                            description = "Your phone is about to turn off. Please plug in your charger immediately."
                        )
                    )
                )
            } else if (batteryPercentage <= settings.batteryLowThreshold) {
                checks.add(
                    GuardianCheckResult(
                        type = GuardianCheckType.BATTERY_LOW,
                        status = GuardianCheckStatus.WARNING,
                        message = "Battery low",
                        detail = "Battery is at $batteryPercentage%",
                        recoveryGuidance = RecoveryGuidance(
                            type = RecoveryActionType.NONE,
                            label = "Battery Low",
                            description = "Your battery is getting low. You should find a charger soon."
                        )
                    )
                )
            }
        }

        // 2. Emergency Contact Missing
        if (settings.emergencyContactCheckEnabled && settings.emergencyPhoneNumber.isBlank()) {
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.EMERGENCY_CONTACT_MISSING,
                    status = GuardianCheckStatus.CRITICAL,
                    message = "Emergency contact missing",
                    detail = "Senior cannot call for help in one tap.",
                    recoveryGuidance = RecoveryGuidance(
                        type = RecoveryActionType.OPEN_EMERGENCY_SETTINGS,
                        label = "No Emergency Contact",
                        description = "You don't have an emergency contact set up. This is important for your safety.",
                        actionButtonLabel = "Add Emergency Contact"
                    )
                )
            )
        }

        // 3. EasyUI not default
        if (settings.defaultLauncherCheckEnabled && !isDefaultLauncher) {
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.NOT_DEFAULT_LAUNCHER,
                    status = GuardianCheckStatus.WARNING,
                    message = "HomeScreen needs fixing",
                    detail = "Phone might go back to normal Android layout.",
                    recoveryGuidance = RecoveryGuidance(
                        type = RecoveryActionType.SET_DEFAULT_LAUNCHER,
                        label = "HomeScreen Problem",
                        description = "EasyUI is not set as your main home screen. This can make the phone harder to use.",
                        actionButtonLabel = "Fix HomeScreen"
                    )
                )
            )
        }

        // 4. No Internet
        if (settings.internetCheckEnabled && !isInternetAvailable) {
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.NO_INTERNET,
                    status = GuardianCheckStatus.WARNING,
                    message = "Connection is off",
                    detail = "Senior might not receive messages.",
                    recoveryGuidance = RecoveryGuidance(
                        type = RecoveryActionType.OPEN_WIFI_SETTINGS,
                        label = "Internet is off",
                        description = "Your phone is not connected to the internet. You might not receive messages or calls.",
                        actionButtonLabel = "Fix Internet"
                    )
                )
            )
        }

        // 5. Battery Optimization
        if (isBatteryOptimized) {
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.BATTERY_OPTIMIZED,
                    status = GuardianCheckStatus.WARNING,
                    message = "Battery saving is on",
                    detail = "System might close EasyUI to save power.",
                    recoveryGuidance = RecoveryGuidance(
                        type = RecoveryActionType.FIX_BATTERY_OPTIMIZATION,
                        label = "Battery Restrictions",
                        description = "Your phone might close EasyUI to save battery. This can stop important alerts from working.",
                        actionButtonLabel = "Fix Battery"
                    )
                )
            )
        }

        // 6. Setup Incomplete
        if (setupCompleteness.score < 1.0f) {
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.SETUP_INCOMPLETE,
                    status = GuardianCheckStatus.WARNING,
                    message = "Tell caregiver to finish setup",
                    detail = "Some features are not configured yet.",
                    recoveryGuidance = RecoveryGuidance(
                        type = RecoveryActionType.OPEN_CAREGIVER_TOOLS,
                        label = "Setup Needs Finishing",
                        description = "Some features of your phone are not fully set up yet. A caregiver can fix this.",
                        actionButtonLabel = "Open Setup Status"
                    )
                )
            )
        }

        val overallStatus = when {
            checks.any { it.status == GuardianCheckStatus.CRITICAL } -> GuardianCheckStatus.CRITICAL
            checks.any { it.status == GuardianCheckStatus.WARNING } -> GuardianCheckStatus.WARNING
            else -> GuardianCheckStatus.OK
        }

        val primaryMessage = when {
            checks.any { it.type == GuardianCheckType.BATTERY_CRITICAL } -> "Please charge phone"
            checks.any { it.type == GuardianCheckType.EMERGENCY_CONTACT_MISSING } -> "Emergency contact missing"
            checks.any { it.type == GuardianCheckType.NOT_DEFAULT_LAUNCHER } -> "HomeScreen needs fixing"
            checks.any { it.type == GuardianCheckType.BATTERY_OPTIMIZED } -> "Battery saving is on"
            checks.any { it.type == GuardianCheckType.NO_INTERNET } -> "Connection is off"
            checks.any { it.type == GuardianCheckType.BATTERY_LOW } -> "Battery low"
            checks.any { it.type == GuardianCheckType.SETUP_INCOMPLETE } -> "Tell caregiver to finish setup"
            else -> "Phone is ready"
        }

        val shouldPromptAlert = checks.any { 
            it.status == GuardianCheckStatus.CRITICAL || 
            it.type == GuardianCheckType.NO_INTERNET ||
            it.type == GuardianCheckType.NOT_DEFAULT_LAUNCHER ||
            it.type == GuardianCheckType.BATTERY_OPTIMIZED
        }
        
        val primaryRecoveryGuidance = checks.firstOrNull { it.status == GuardianCheckStatus.CRITICAL }?.recoveryGuidance
            ?: checks.firstOrNull { it.status == GuardianCheckStatus.WARNING }?.recoveryGuidance

        return PhoneHealthState(checks, overallStatus, primaryMessage, shouldPromptAlert, primaryRecoveryGuidance)
    }

    fun calculateSetupCompleteness(
        settings: LauncherSettings,
        isDefaultLauncher: Boolean,
        hasRequiredPermissions: Boolean,
        favoriteContactCount: Int,
        allowedAppCount: Int
    ): SetupCompleteness {
        val items = listOf(
            SetupCompletenessItem(
                id = "default_launcher",
                label = "EasyUI set as default",
                isComplete = isDefaultLauncher,
                isRequired = true,
                actionLabel = "Set as default"
            ),
            SetupCompletenessItem(
                id = "caregiver_pin",
                label = "Caregiver PIN set",
                isComplete = settings.pinHashHex != null,
                isRequired = true,
                actionLabel = "Set PIN"
            ),
            SetupCompletenessItem(
                id = "layout_locked",
                label = "Layout locked",
                isComplete = settings.layoutLocked,
                isRequired = true,
                actionLabel = "Lock layout"
            ),
            SetupCompletenessItem(
                id = "emergency_contact",
                label = "Emergency contact set",
                isComplete = settings.emergencyPhoneNumber.isNotBlank(),
                isRequired = true,
                actionLabel = "Add contact"
            ),
            SetupCompletenessItem(
                id = "favorite_contacts",
                label = "Important contacts added",
                isComplete = favoriteContactCount > 0,
                isRequired = false,
                actionLabel = "Add contacts"
            ),
            SetupCompletenessItem(
                id = "allowed_apps",
                label = "Allowed apps selected",
                isComplete = allowedAppCount > 0,
                isRequired = true,
                actionLabel = "Select apps"
            ),
            SetupCompletenessItem(
                id = "permissions",
                label = "Permissions available",
                isComplete = hasRequiredPermissions,
                isRequired = true,
                actionLabel = "Fix"
            )
        )

        val completedCount = items.count { it.isComplete }
        val score = completedCount.toFloat() / items.size

        return SetupCompleteness(items, score)
    }
}
