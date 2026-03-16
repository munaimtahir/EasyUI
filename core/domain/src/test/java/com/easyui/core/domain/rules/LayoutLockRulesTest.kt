package com.easyui.core.domain.rules

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutLockRulesTest {
    @Test
    fun `layout lock blocks accidental home long press entry`() {
        assertTrue(LayoutLockRules.blocksHomeLongPress(layoutLocked = true))
        assertFalse(LayoutLockRules.blocksHomeLongPress(layoutLocked = false))
    }
}
