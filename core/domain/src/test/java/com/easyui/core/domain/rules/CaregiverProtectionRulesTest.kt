package com.easyui.core.domain.rules

import com.easyui.core.domain.model.ProtectedAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CaregiverProtectionRulesTest {
    @Test
    fun `validatePin requires digits and matching confirmation`() {
        assertFalse(CaregiverProtectionRules.validatePin("12a4").valid)
        assertFalse(CaregiverProtectionRules.validatePin("123", "123").valid)
        assertFalse(CaregiverProtectionRules.validatePin("1234", "0000").valid)
        assertTrue(CaregiverProtectionRules.validatePin("1234", "1234").valid)
    }

    @Test
    fun `protected actions require pin only when configured`() {
        assertFalse(
            CaregiverProtectionRules.requiresPin(
                protectionEnabled = false,
                hasPinConfigured = true,
                action = ProtectedAction.OPEN_CAREGIVER_SETTINGS,
            ),
        )
        assertTrue(
            CaregiverProtectionRules.requiresPin(
                protectionEnabled = true,
                hasPinConfigured = true,
                action = ProtectedAction.OPEN_CAREGIVER_SETTINGS,
            ),
        )
        assertTrue(
            CaregiverProtectionRules.requiresPin(
                protectionEnabled = true,
                hasPinConfigured = true,
                action = ProtectedAction.MANAGE_LAYOUT_PAGES,
            ),
        )
        assertTrue(
            CaregiverProtectionRules.requiresPin(
                protectionEnabled = true,
                hasPinConfigured = true,
                action = ProtectedAction.MANAGE_FAVORITE_CONTACTS,
            ),
        )
        assertTrue(
            CaregiverProtectionRules.requiresPin(
                protectionEnabled = false,
                hasPinConfigured = true,
                action = ProtectedAction.CHANGE_PIN,
            ),
        )
    }

    @Test
    fun `first pin setup enables protection and locks layout`() {
        val behavior = CaregiverProtectionRules.pinSaveBehavior(
            hadExistingPinConfigured = false,
            protectionEnabled = false,
            layoutLocked = false,
        )

        assertTrue(behavior.protectionEnabled)
        assertTrue(behavior.layoutLocked)
    }

    @Test
    fun `changing existing pin preserves current protection settings`() {
        val unlockedBehavior = CaregiverProtectionRules.pinSaveBehavior(
            hadExistingPinConfigured = true,
            protectionEnabled = false,
            layoutLocked = false,
        )
        assertFalse(unlockedBehavior.protectionEnabled)
        assertFalse(unlockedBehavior.layoutLocked)

        val lockedBehavior = CaregiverProtectionRules.pinSaveBehavior(
            hadExistingPinConfigured = true,
            protectionEnabled = true,
            layoutLocked = true,
        )
        assertTrue(lockedBehavior.protectionEnabled)
        assertTrue(lockedBehavior.layoutLocked)
    }
}
