package com.easyui.core.domain.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ActionAvailabilityResolverTest {
    @Test
    fun `flashlight state is disabled when unsupported`() {
        val state = ActionAvailabilityResolver.flashlight(isSupported = false)

        assertFalse(state.enabled)
        assertEquals("Flashlight is not available on this device.", state.fallbackMessage)
    }

    @Test
    fun `emergency state is disabled without dialer`() {
        val state = ActionAvailabilityResolver.emergency(hasDialer = false, phoneNumber = "911")

        assertFalse(state.enabled)
        assertEquals("No dialer app is available on this device.", state.fallbackMessage)
    }

    @Test
    fun `emergency state is enabled when dialer and phone number exist`() {
        val state = ActionAvailabilityResolver.emergency(hasDialer = true, phoneNumber = "911")

        assertTrue(state.enabled)
    }
}
