package com.easyui.senior.ui

import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression coverage for reminder storage: titles were joined with unescaped ";"/"|"
 * delimiters, so a title containing either character corrupted the whole list. Storage
 * is now JSON-encoded, with a fallback decoder for data written in the old format.
 */
class RemindersSerializationTest {

    @Test
    fun reminder_roundTrips_whenTitleContainsDelimiterCharacters() {
        val reminders = listOf(
            Reminder(id = "1", title = "Take pills; wash up | rest", type = "Medication", time = "08:00")
        )
        val raw = remindersJson.encodeToString(reminders)
        assertEquals(reminders, decodeReminders(raw))
    }

    @Test
    fun decodeReminders_handlesEmptyStorage() {
        assertEquals(emptyList<Reminder>(), decodeReminders(""))
    }

    @Test
    fun decodeReminders_fallsBackToLegacyDelimitedFormat() {
        val legacyRaw = "1|Take pills|Medication|08:00;2|Walk|Activity|17:00"
        val expected = listOf(
            Reminder("1", "Take pills", "Medication", "08:00"),
            Reminder("2", "Walk", "Activity", "17:00")
        )
        assertEquals(expected, decodeReminders(legacyRaw))
    }
}
