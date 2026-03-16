package com.easyui.core.domain.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FallbackStateRulesTest {
    @Test
    fun `app list empty state is calm when no apps are visible`() {
        val fallback = FallbackStateRules.appList(query = "", visibleAppCount = 0)

        assertEquals("No apps are shown here right now", fallback?.title)
    }

    @Test
    fun `app list search state changes message for no match`() {
        val fallback = FallbackStateRules.appList(query = "maps", visibleAppCount = 0)

        assertEquals("No apps match that search", fallback?.title)
    }

    @Test
    fun `home fallback highlights very simple mode`() {
        val fallback = FallbackStateRules.home(tileCount = 2, verySimpleModeEnabled = true)

        assertEquals("Very simple home is on", fallback?.title)
    }

    @Test
    fun `no fallback is shown for a fuller home`() {
        assertNull(FallbackStateRules.home(tileCount = 4, verySimpleModeEnabled = false))
    }
}
