package com.easyui.senior.home

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression coverage for Contact tile serialization: name/phone were joined with an
 * unescaped '|', so a literal '|' in either field corrupted the round trip and could
 * shift the action field, producing a shortcut that dials the wrong number.
 */
class HomeTileContentSerializationTest {

    @Test
    fun contact_roundTrips_whenFieldsContainNoSpecialCharacters() {
        val contact = HomeTileContent.Contact("Mom", "555-1234", ContactAction.Dial)
        val raw = contact.toStorageString()
        assertEquals(contact, homeTileContentFromStorageString(raw))
    }

    @Test
    fun contact_roundTrips_whenNameContainsPipe() {
        val contact = HomeTileContent.Contact("Mom | Home", "555-1234", ContactAction.SMS)
        val raw = contact.toStorageString()
        assertEquals(contact, homeTileContentFromStorageString(raw))
    }

    @Test
    fun contact_roundTrips_whenFieldsContainBackslashAndPipe() {
        val contact = HomeTileContent.Contact("Mom \\ Dad | Home", "5\\5|5", ContactAction.Dial)
        val raw = contact.toStorageString()
        assertEquals(contact, homeTileContentFromStorageString(raw))
    }

    @Test
    fun widget_and_app_roundTripUnaffected() {
        val widget = HomeTileContent.Widget(LocalWidgetType.Clock)
        assertEquals(widget, homeTileContentFromStorageString(widget.toStorageString()))

        val app = HomeTileContent.App(AppComponentRef("pkg", "act"))
        assertEquals(app, homeTileContentFromStorageString(app.toStorageString()))
    }
}
