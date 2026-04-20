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
    fun `starter layout includes fixed senior actions`() {
        val apps = listOf(
            InstalledApp("com.android.camera", "CameraActivity", "Camera"),
            InstalledApp("com.android.dialer", "DialerActivity", "Phone"),
        )

        val layout = HomeLayoutRules.starterLayout(apps)

        assertTrue(layout.any { it.action == HomeTileAction.OPEN_DIALER })
        assertTrue(layout.any { it.action == HomeTileAction.OPEN_MESSAGES })
        assertTrue(layout.any { it.action == HomeTileAction.OPEN_CONTACTS })
        assertTrue(layout.any { it.action == HomeTileAction.OPEN_PHOTOS })
        assertTrue(layout.any { it.action == HomeTileAction.OPEN_CAMERA })
        assertTrue(layout.any { it.action == HomeTileAction.EMERGENCY })
        assertTrue(HomeLayoutRules.isValid(layout))
    }

    @Test
    fun `ensure required actions replaces legacy first page tiles`() {
        val migrated = HomeLayoutRules.ensureRequiredActions(
            listOf(
                HomeTile("phone", 0, "Phone", HomeTileType.ACTION, action = HomeTileAction.OPEN_DIALER),
                HomeTile("flashlight", 1, "Flashlight", HomeTileType.ACTION, action = HomeTileAction.FLASHLIGHT),
                HomeTile("camera", 2, "Camera", HomeTileType.ACTION, action = HomeTileAction.OPEN_CAMERA),
                HomeTile("emergency", 3, "Emergency", HomeTileType.ACTION, action = HomeTileAction.EMERGENCY),
                HomeTile("health-info", 4, "Health Info", HomeTileType.ACTION, action = HomeTileAction.OPEN_HEALTH_INFO),
                HomeTile("sos", 5, "SOS", HomeTileType.ACTION, action = HomeTileAction.SOS),
            ),
        )

        assertEquals(
            listOf("Phone", "Messages", "Contacts", "Photos", "Camera", "Emergency"),
            migrated.filter { it.position in 0..5 }.map { it.title },
        )
    }

    @Test
    fun `isValid rejects duplicate positions`() {
        val invalidTiles = listOf(
            HomeTile("one", 0, "One", HomeTileType.ACTION, action = HomeTileAction.OPEN_DIALER),
            HomeTile("two", 0, "Two", HomeTileType.ACTION, action = HomeTileAction.FLASHLIGHT),
        )

        assertFalse(HomeLayoutRules.isValid(invalidTiles))
    }

    @Test
    fun `upsertContactTile adds or updates a favorite contact tile`() {
        val initial = listOf(
            HomeTile("phone", 0, "Phone", HomeTileType.ACTION, action = HomeTileAction.OPEN_DIALER),
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
            position = 6,
            pageCount = 2,
        )

        assertNotNull(updated)
        assertTrue(updated!!.any { it.packageName == "com.example.camera" && it.position == 6 })
    }

    @Test
    fun `pages keeps first page anchored and exposes caregiver page slots`() {
        val pages = HomeLayoutRules.pages(
            tiles = listOf(
                HomeTile("app-maps", 6, "Maps", HomeTileType.APP, packageName = "com.maps"),
                HomeTile("contact-1", 7, "Daughter", HomeTileType.CONTACT, phoneNumber = "123"),
            ),
            pageCount = 2,
        )

        assertEquals(2, pages.size)
        assertEquals("Phone", pages[0][0]?.title)
        assertEquals("Emergency", pages[0][5]?.title)
        assertEquals("Maps", pages[1][0]?.title)
        assertEquals("Daughter", pages[1][1]?.title)
    }

    @Test
    fun `canUsePageCount rejects removing page with configured tiles`() {
        val tiles = listOf(
            HomeTile("app-maps", 6, "Maps", HomeTileType.APP, packageName = "com.maps"),
        )

        assertFalse(HomeLayoutRules.canUsePageCount(tiles, pageCount = 1))
        assertTrue(HomeLayoutRules.canUsePageCount(tiles, pageCount = 2))
    }
}
