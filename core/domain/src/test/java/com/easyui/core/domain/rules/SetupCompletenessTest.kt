package com.easyui.core.domain.rules

import com.easyui.core.domain.model.LauncherSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SetupCompletenessTest {

    private val defaultSettings = LauncherSettings()

    @Test
    fun `score is zero for default settings`() {
        val completeness = GuardianRules.calculateSetupCompleteness(
            settings = defaultSettings.copy(emergencyPhoneNumber = "", pinHashHex = null),
            isDefaultLauncher = false,
            hasRequiredPermissions = false,
            favoriteContactCount = 0,
            allowedAppCount = 0
        )
        // items: default_launcher, caregiver_pin, layout_locked, emergency_contact, favorite_contacts, allowed_apps, permissions
        // default settings: layout_locked=false
        assertEquals(0f, completeness.score)
    }

    @Test
    fun `score is 1 for fully configured state`() {
        val settings = defaultSettings.copy(
            pinHashHex = "hash",
            layoutLocked = true,
            emergencyPhoneNumber = "911"
        )
        val completeness = GuardianRules.calculateSetupCompleteness(
            settings = settings,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            favoriteContactCount = 1,
            allowedAppCount = 1
        )
        assertEquals(1.0f, completeness.score)
        assertTrue(completeness.items.all { it.isComplete })
    }

    @Test
    fun `partial completeness`() {
        val settings = defaultSettings.copy(
            pinHashHex = "hash",
            layoutLocked = false,
            emergencyPhoneNumber = "911"
        )
        val completeness = GuardianRules.calculateSetupCompleteness(
            settings = settings,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            favoriteContactCount = 0,
            allowedAppCount = 1
        )
        // Complete: pin, emergency, default_launcher, permissions, allowed_apps = 5
        // Incomplete: layout_locked, favorite_contacts = 2
        // Total 7 items. Score = 5/7
        assertEquals(5f/7f, completeness.score, 0.01f)
    }
}
