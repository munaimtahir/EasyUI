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
                action = ProtectedAction.ENTER_EDIT_MODE,
            ),
        )
        assertTrue(
            CaregiverProtectionRules.requiresPin(
                protectionEnabled = true,
                hasPinConfigured = true,
                action = ProtectedAction.ENTER_EDIT_MODE,
            ),
        )
        assertTrue(
            CaregiverProtectionRules.requiresPin(
                protectionEnabled = true,
                hasPinConfigured = true,
                action = ProtectedAction.MANAGE_HOME_DISPLAY,
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
}
