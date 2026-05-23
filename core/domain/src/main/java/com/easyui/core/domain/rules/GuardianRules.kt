package com.easyui.core.domain.rules

import com.easyui.core.domain.model.GuardianCheckResult
import com.easyui.core.domain.model.GuardianCheckStatus
import com.easyui.core.domain.model.GuardianCheckType
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.PhoneHealthState
import com.easyui.core.domain.model.SetupCompleteness
import com.easyui.core.domain.model.SetupCompletenessItem

object GuardianRules {

    fun calculatePhoneHealthState(
        settings: LauncherSettings,
        batteryPercentage: Int?,
        isCharging: Boolean,
        isInternetAvailable: Boolean,
        isDefaultLauncher: Boolean,
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
                        detail = "Battery is very low ($batteryPercentage%)"
                    )
                )
            } else if (batteryPercentage <= settings.batteryLowThreshold) {
                checks.add(
                    GuardianCheckResult(
                        type = GuardianCheckType.BATTERY_LOW,
                        status = GuardianCheckStatus.WARNING,
                        message = "Battery low",
                        detail = "Battery is at $batteryPercentage%"
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
                    detail = "Senior cannot call for help in one tap."
                )
            )
        }

        // 3. EasyUI not default
        if (settings.defaultLauncherCheckEnabled && !isDefaultLauncher) {
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.NOT_DEFAULT_LAUNCHER,
                    status = GuardianCheckStatus.WARNING,
                    message = "EasyUI not set as home",
                    detail = "Phone might go back to normal Android layout."
                )
            )
        }

        // 4. No Internet
        if (settings.internetCheckEnabled && !isInternetAvailable) {
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.NO_INTERNET,
                    status = GuardianCheckStatus.WARNING,
                    message = "Internet is off",
                    detail = "Senior might not receive messages."
                )
            )
        }

        // 5. Setup Incomplete
        if (setupCompleteness.score < 1.0f) {
            checks.add(
                GuardianCheckResult(
                    type = GuardianCheckType.SETUP_INCOMPLETE,
                    status = GuardianCheckStatus.WARNING,
                    message = "Ask caregiver to fix setup",
                    detail = "Some features are not configured yet."
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
            checks.any { it.type == GuardianCheckType.NOT_DEFAULT_LAUNCHER } -> "EasyUI not set as home"
            checks.any { it.type == GuardianCheckType.NO_INTERNET } -> "Internet is off"
            checks.any { it.type == GuardianCheckType.BATTERY_LOW } -> "Battery low"
            checks.any { it.type == GuardianCheckType.SETUP_INCOMPLETE } -> "Ask caregiver to fix setup"
            else -> "Phone is ready"
        }

        val shouldPromptAlert = checks.any { 
            it.status == GuardianCheckStatus.CRITICAL || 
            it.type == GuardianCheckType.NO_INTERNET ||
            it.type == GuardianCheckType.NOT_DEFAULT_LAUNCHER
        }

        return PhoneHealthState(checks, overallStatus, primaryMessage, shouldPromptAlert)
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
