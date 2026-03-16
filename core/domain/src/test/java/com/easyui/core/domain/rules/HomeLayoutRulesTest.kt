package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
        assertEquals(listOf(0, 1), normalized.map { it.position })
    }

    @Test
    fun `starter layout includes all apps tile and emergency actions`() {
        val apps = listOf(
            InstalledApp("com.android.camera", "CameraActivity", "Camera"),
            InstalledApp("com.android.dialer", "DialerActivity", "Phone"),
        )

        val layout = HomeLayoutRules.starterLayout(apps)

        assertTrue(layout.any { it.action == HomeTileAction.OPEN_APP_LIST })
        assertTrue(layout.any { it.action == HomeTileAction.FLASHLIGHT })
        assertTrue(layout.any { it.action == HomeTileAction.EMERGENCY })
        assertTrue(HomeLayoutRules.isValid(layout))
    }

    @Test
    fun `isValid rejects non-sequential positions`() {
        val invalidTiles = listOf(
            HomeTile("one", 0, "One", HomeTileType.ACTION, action = HomeTileAction.OPEN_APP_LIST),
            HomeTile("two", 3, "Two", HomeTileType.ACTION, action = HomeTileAction.FLASHLIGHT),
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
        )

        assertEquals(2, updated.size)
        assertEquals("Grace Hopper", updated.last().title)
        assertEquals("5552222", updated.last().phoneNumber)
        assertTrue(HomeLayoutRules.contactTiles(updated).all { it.type == HomeTileType.CONTACT })
    }
}
