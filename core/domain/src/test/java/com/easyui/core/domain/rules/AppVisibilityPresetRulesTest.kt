package com.easyui.core.domain.rules

import com.easyui.core.domain.model.AppVisibilityPreset
import com.easyui.core.domain.model.InstalledApp
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppVisibilityPresetRulesTest {
    private val apps = listOf(
        InstalledApp("com.android.dialer", "Phone", "Phone"),
        InstalledApp("com.google.android.apps.messaging", "Messages", "Messages"),
        InstalledApp("com.android.chrome", "Chrome", "Chrome"),
        InstalledApp("com.whatsapp", "WhatsApp", "WhatsApp"),
        InstalledApp("com.fun.game", "Game", "Game"),
    )

    @Test
    fun `essentials preset hides non-essential apps`() {
        val hidden = AppVisibilityPresetRules.hiddenPackagesForPreset(apps, AppVisibilityPreset.ESSENTIALS_ONLY)

        assertFalse("com.android.dialer" in hidden)
        assertFalse("com.google.android.apps.messaging" in hidden)
        assertTrue("com.android.chrome" in hidden)
        assertTrue("com.fun.game" in hidden)
    }

    @Test
    fun `minimal common preset keeps common communication apps`() {
        val hidden = AppVisibilityPresetRules.hiddenPackagesForPreset(apps, AppVisibilityPreset.MINIMAL_COMMON_APPS)

        assertFalse("com.android.chrome" in hidden)
        assertFalse("com.whatsapp" in hidden)
        assertTrue("com.fun.game" in hidden)
    }

    @Test
    fun `custom preset does not hide anything`() {
        val hidden = AppVisibilityPresetRules.hiddenPackagesForPreset(apps, AppVisibilityPreset.CUSTOM)

        assertTrue(hidden.isEmpty())
    }
}
