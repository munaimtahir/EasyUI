package com.easyui.core.domain.rules

import com.easyui.core.domain.model.PinValidationResult
import com.easyui.core.domain.model.ProtectedAction

data class PinSaveBehavior(
    val protectionEnabled: Boolean,
    val layoutLocked: Boolean,
)

object CaregiverProtectionRules {
    fun validatePin(pin: String, confirmation: String? = null): PinValidationResult {
        if (pin.length < 4 || pin.length > 8) {
            return PinValidationResult(false, "Use a 4 to 8 digit PIN.")
        }
        if (pin.any { !it.isDigit() }) {
            return PinValidationResult(false, "PIN must use numbers only.")
        }
        if (confirmation != null && pin != confirmation) {
            return PinValidationResult(false, "PIN entries do not match.")
        }
        return PinValidationResult(true)
    }

    fun requiresPin(protectionEnabled: Boolean, hasPinConfigured: Boolean, action: ProtectedAction): Boolean =
        hasPinConfigured &&
            when (action) {
                ProtectedAction.OPEN_CAREGIVER_SETTINGS,
                ProtectedAction.MANAGE_LAYOUT_PAGES,
                ProtectedAction.MANAGE_ALLOWED_APPS,
                ProtectedAction.MANAGE_FAVORITE_CONTACTS,
                ProtectedAction.RESET_LAUNCHER,
                ProtectedAction.TOGGLE_LAYOUT_LOCK,
                -> protectionEnabled
                ProtectedAction.TOGGLE_PROTECTION -> protectionEnabled
                ProtectedAction.CHANGE_PIN -> true
            }

    fun pinSaveBehavior(
        hadExistingPinConfigured: Boolean,
        protectionEnabled: Boolean,
        layoutLocked: Boolean,
    ): PinSaveBehavior =
        if (hadExistingPinConfigured) {
            PinSaveBehavior(
                protectionEnabled = protectionEnabled,
                layoutLocked = layoutLocked,
            )
        } else {
            PinSaveBehavior(
                protectionEnabled = true,
                layoutLocked = true,
            )
        }
}
