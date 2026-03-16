package com.easyui.core.domain.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContactTileRulesTest {
    @Test
    fun `validate requires a display name and phone number`() {
        assertEquals("Enter a contact name.", ContactTileRules.validate("", "5551234"))
        assertEquals("Enter a phone number.", ContactTileRules.validate("Grace", "12"))
        assertNull(ContactTileRules.validate("Grace Hopper", "5551234"))
    }

    @Test
    fun `initials use up to two words`() {
        assertEquals("GH", ContactTileRules.initials("Grace Hopper"))
        assertEquals("A", ContactTileRules.initials("Ada"))
    }

    @Test
    fun `photo fallback uses initials when no photo is available`() {
        assertEquals("GH", ContactTileRules.photoFallback(null, "Grace Hopper"))
    }
}
