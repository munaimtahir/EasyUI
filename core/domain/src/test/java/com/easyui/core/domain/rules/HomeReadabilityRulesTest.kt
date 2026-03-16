package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeReadabilityPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeReadabilityRulesTest {
    @Test
    fun `larger tiles preset uses one home column`() {
        val config = HomeReadabilityRules.config(HomeReadabilityPreset.LARGER_TILES, verySimpleModeEnabled = false)

        assertEquals(1, config.columns)
        assertTrue(config.tileSpacingScale > 1f)
    }

    @Test
    fun `very simple mode overrides readability preset`() {
        val config = HomeReadabilityRules.config(HomeReadabilityPreset.STANDARD, verySimpleModeEnabled = true)

        assertEquals(1, config.columns)
        assertTrue(config.outerPaddingScale > 1f)
    }
}
