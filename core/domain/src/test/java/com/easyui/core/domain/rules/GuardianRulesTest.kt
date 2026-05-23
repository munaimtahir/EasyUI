package com.easyui.core.domain.rules

import com.easyui.core.domain.model.GuardianCheckStatus
import com.easyui.core.domain.model.GuardianCheckType
import com.easyui.core.domain.model.LauncherSettings
import com.easyui.core.domain.model.SetupCompleteness
import org.junit.Assert.assertEquals
import org.junit.Test

class GuardianRulesTest {

    private val defaultSettings = LauncherSettings()
    private val fullSetup = SetupCompleteness(emptyList(), 1.0f)
    private val incompleteSetup = SetupCompleteness(emptyList(), 0.5f)

    @Test
    fun `ready state when all good`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings,
            batteryPercentage = 80,
            isCharging = false,
            isInternetAvailable = true,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(GuardianCheckStatus.OK, state.overallStatus)
        assertEquals("Phone is ready", state.primaryMessage)
    }

    @Test
    fun `critical battery priority`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings.copy(batteryCriticalThreshold = 10),
            batteryPercentage = 5,
            isCharging = false,
            isInternetAvailable = true,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(GuardianCheckStatus.CRITICAL, state.overallStatus)
        assertEquals("Please charge phone", state.primaryMessage)
    }

    @Test
    fun `low battery warning`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings.copy(batteryLowThreshold = 20, batteryCriticalThreshold = 10),
            batteryPercentage = 15,
            isCharging = false,
            isInternetAvailable = true,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(GuardianCheckStatus.WARNING, state.overallStatus)
        assertEquals("Battery low", state.primaryMessage)
    }

    @Test
    fun `no internet warning`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings,
            batteryPercentage = 80,
            isCharging = false,
            isInternetAvailable = false,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(GuardianCheckStatus.WARNING, state.overallStatus)
        assertEquals("Internet is off", state.primaryMessage)
    }

    @Test
    fun `emergency contact missing critical`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings.copy(emergencyPhoneNumber = ""),
            batteryPercentage = 80,
            isCharging = false,
            isInternetAvailable = true,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(GuardianCheckStatus.CRITICAL, state.overallStatus)
        assertEquals("Emergency contact missing", state.primaryMessage)
    }

    @Test
    fun `setup incomplete warning`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings,
            batteryPercentage = 80,
            isCharging = false,
            isInternetAvailable = true,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = incompleteSetup
        )
        assertEquals(GuardianCheckStatus.WARNING, state.overallStatus)
        assertEquals("Ask caregiver to fix setup", state.primaryMessage)
    }

    @Test
    fun `priority emergency contact over internet`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings.copy(emergencyPhoneNumber = ""),
            batteryPercentage = 80,
            isCharging = false,
            isInternetAvailable = false,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(GuardianCheckStatus.CRITICAL, state.overallStatus)
        assertEquals("Emergency contact missing", state.primaryMessage)
    }

    @Test
    fun `prompt alert on critical battery`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings,
            batteryPercentage = 5,
            isCharging = false,
            isInternetAvailable = true,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(true, state.shouldPromptAlert)
    }

    @Test
    fun `prompt alert on no internet`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings,
            batteryPercentage = 80,
            isCharging = false,
            isInternetAvailable = false,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(true, state.shouldPromptAlert)
    }

    @Test
    fun `do not prompt alert on low battery`() {
        val state = GuardianRules.calculatePhoneHealthState(
            settings = defaultSettings.copy(batteryLowThreshold = 20, batteryCriticalThreshold = 10),
            batteryPercentage = 15,
            isCharging = false,
            isInternetAvailable = true,
            isDefaultLauncher = true,
            hasRequiredPermissions = true,
            setupCompleteness = fullSetup
        )
        assertEquals(GuardianCheckStatus.WARNING, state.overallStatus)
        assertEquals(false, state.shouldPromptAlert)
    }
}
