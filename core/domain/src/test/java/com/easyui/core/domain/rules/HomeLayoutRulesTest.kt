package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeLayoutRulesTest {
    @Test
    fun `normalize reorders tiles and removes duplicate ids`() {
        val tiles = listOf(
            HomeTile("two", 2, "Second", HomeTileType.ACTION, action = HomeTileAction.FLASHLIGHT),
            HomeTile("one", 0, "First", HomeTileType.ACTION, action = HomeTileAction.OPEN_APP_LIST),
            HomeTile("two", 1, "Duplicate", HomeTileType.ACTION, action = HomeTileAction.EMERGENCY),
        )

        val normalized = HomeLayoutRules.normalize(tiles)

        assertEquals(listOf("one", "two"), normalized.map { it.id })
        assertEquals(listOf(0, 2), normalized.map { it.position })
    }

    @Test
    fun `starter layout includes phone and all apps actions`() {
        val apps = listOf(
            InstalledApp("com.android.camera", "CameraActivity", "Camera"),
            InstalledApp("com.android.dialer", "DialerActivity", "Phone"),
        )

        val layout = HomeLayoutRules.starterLayout(apps)

        assertTrue(layout.any { it.action == HomeTileAction.OPEN_DIALER })
        assertTrue(layout.any { it.action == HomeTileAction.OPEN_APP_LIST })
        assertTrue(layout.any { it.action == HomeTileAction.FLASHLIGHT })
        assertTrue(HomeLayoutRules.isValid(layout))
    }

    @Test
    fun `isValid rejects duplicate positions`() {
        val invalidTiles = listOf(
            HomeTile("one", 0, "One", HomeTileType.ACTION, action = HomeTileAction.OPEN_APP_LIST),
            HomeTile("two", 0, "Two", HomeTileType.ACTION, action = HomeTileAction.FLASHLIGHT),
        )

        assertFalse(HomeLayoutRules.isValid(invalidTiles))
    }

    @Test
    fun `upsertContactTile adds or updates a favorite contact tile`() {
        val initial = listOf(
            HomeTile("apps", 0, "All Apps", HomeTileType.ACTION, action = HomeTileAction.OPEN_APP_LIST),
            HomeTile("contact-1", 1, "Grace", HomeTileType.CONTACT, phoneNumber = "5551111"),
        )

        val updated = HomeLayoutRules.upsertContactTile(
            tiles = initial,
            tile = HomeTile("contact-1", 1, "Grace Hopper", HomeTileType.CONTACT, phoneNumber = "5552222"),
            pageCount = 1,
        )

        assertNotNull(updated)
        val contacts = HomeLayoutRules.contactTiles(updated!!)
        assertEquals(1, contacts.size)
        assertEquals("Grace Hopper", contacts.single().title)
        assertEquals("5552222", contacts.single().phoneNumber)
        assertTrue(updated.any { it.action == HomeTileAction.OPEN_DIALER })
    }

    @Test
    fun `assignAppToPosition places app into requested slot`() {
        val updated = HomeLayoutRules.assignAppToPosition(
            tiles = emptyList(),
            app = InstalledApp("com.example.camera", "CameraActivity", "Camera"),
            position = 2,
            pageCount = 1,
        )

        assertNotNull(updated)
        assertTrue(updated!!.any { it.packageName == "com.example.camera" && it.position == 2 })
    }
}
