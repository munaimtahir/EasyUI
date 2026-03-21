package com.easyui.core.domain.rules

import com.easyui.core.domain.model.InstalledApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PrimaryHomeAppRulesTest {
    @Test
    fun `resolve prefers known package for messages`() {
        val apps = listOf(
            InstalledApp("com.example.sms", "SmsActivity", "Text Messages"),
            InstalledApp("com.google.android.apps.messaging", "MessagesActivity", "Messages"),
        )

        val resolved = PrimaryHomeAppRules.resolve(PrimaryHomeAppKind.MESSAGES, apps)

        assertEquals("com.google.android.apps.messaging", resolved?.packageName)
    }

    @Test
    fun `resolve falls back to label match for photos`() {
        val apps = listOf(
            InstalledApp("com.example.gallery", "GalleryActivity", "Family Gallery"),
        )

        val resolved = PrimaryHomeAppRules.resolve(PrimaryHomeAppKind.PHOTOS, apps)

        assertEquals("com.example.gallery", resolved?.packageName)
    }

    @Test
    fun `resolve returns null when no suitable app exists`() {
        val apps = listOf(
            InstalledApp("com.example.clock", "ClockActivity", "Clock"),
        )

        val resolved = PrimaryHomeAppRules.resolve(PrimaryHomeAppKind.CONTACTS, apps)

        assertNull(resolved)
    }
}
