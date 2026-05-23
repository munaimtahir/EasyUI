package com.easyui.core.domain.rules

import com.easyui.core.domain.model.GuardianCheckResult
import com.easyui.core.domain.model.GuardianCheckStatus
import com.easyui.core.domain.model.GuardianCheckType
import com.easyui.core.domain.model.PhoneHealthState
import com.easyui.core.domain.model.RemoteStatusPacket
import com.easyui.core.domain.model.SetupCompleteness
import com.easyui.core.domain.model.SetupCompletenessItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class RemoteLinkRulesTest {

    @Test
    fun `encode and decode packet preserves data`() {
        val packet = RemoteStatusPacket(
            deviceName = "Senior Phone",
            healthState = PhoneHealthState(
                checks = listOf(
                    GuardianCheckResult(
                        type = GuardianCheckType.BATTERY_LOW,
                        status = GuardianCheckStatus.WARNING,
                        message = "Battery is at 15%"
                    )
                ),
                overallStatus = GuardianCheckStatus.WARNING,
                primaryMessage = "Battery low"
            ),
            setupCompleteness = SetupCompleteness(
                items = listOf(
                    SetupCompletenessItem("id", "label", true, true)
                ),
                score = 1.0f
            )
        )

        val encoded = RemoteLinkRules.encodePacket(packet)
        assertNotNull(encoded)

        val decoded = RemoteLinkRules.decodePacket(encoded)
        assertNotNull(decoded)
        assertEquals(packet.deviceName, decoded?.deviceName)
        assertEquals(packet.healthState.overallStatus, decoded?.healthState?.overallStatus)
        assertEquals(packet.healthState.primaryMessage, decoded?.healthState?.primaryMessage)
        assertEquals(packet.healthState.checks.size, decoded?.healthState?.checks?.size)
        assertEquals(packet.setupCompleteness.score, decoded?.setupCompleteness?.score)
    }
}
