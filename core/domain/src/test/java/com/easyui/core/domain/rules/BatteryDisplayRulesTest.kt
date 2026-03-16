package com.easyui.core.domain.rules

import com.easyui.core.domain.model.BatteryStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryDisplayRulesTest {
    @Test
    fun `summary shows charging state`() {
        assertEquals(
            "Battery 82% · Charging",
            BatteryDisplayRules.summary(BatteryStatus(percentage = 82, isCharging = true, isLow = false)),
        )
    }

    @Test
    fun `summary shows low battery state`() {
        assertEquals(
            "Battery 12% · Low",
            BatteryDisplayRules.summary(BatteryStatus(percentage = 12, isCharging = false, isLow = true)),
        )
    }
}
