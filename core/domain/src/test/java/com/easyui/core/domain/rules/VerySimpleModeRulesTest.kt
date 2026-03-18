package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import org.junit.Assert.assertEquals
import org.junit.Test

class VerySimpleModeRulesTest {
    @Test
    fun `very simple mode keeps a calm subset of tiles`() {
        val tiles = listOf(
            HomeTile("emergency", 0, "Emergency", HomeTileType.ACTION, action = HomeTileAction.EMERGENCY),
            HomeTile("contact-a", 1, "Ada", HomeTileType.CONTACT, phoneNumber = "111"),
            HomeTile("contact-b", 2, "Grace", HomeTileType.CONTACT, phoneNumber = "222"),
            HomeTile("app-camera", 3, "Camera", HomeTileType.APP, packageName = "camera"),
            HomeTile("flashlight", 4, "Flashlight", HomeTileType.ACTION, action = HomeTileAction.FLASHLIGHT),
            HomeTile("phone", 5, "Phone", HomeTileType.ACTION, action = HomeTileAction.OPEN_DIALER),
        )

        val simplified = VerySimpleModeRules.simplify(tiles, enabled = true)

        assertEquals(listOf("phone", "emergency", "contact-a", "contact-b"), simplified.map { it.id })
    }

    @Test
    fun `very simple mode is reversible`() {
        val tiles = listOf(
            HomeTile("emergency", 0, "Emergency", HomeTileType.ACTION, action = HomeTileAction.EMERGENCY),
            HomeTile("app-camera", 1, "Camera", HomeTileType.APP, packageName = "camera"),
        )

        assertEquals(tiles, VerySimpleModeRules.simplify(tiles, enabled = false))
    }
}
