package com.easyui.core.domain.rules

import com.easyui.core.domain.model.InstalledApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherResetRulesTest {
    @Test
    fun `reset clears hidden apps and restores starter layout`() {
        val layout = LauncherResetRules.resetLayout(
            listOf(InstalledApp("com.android.camera", "CameraActivity", "Camera")),
        )

        assertTrue(layout.any { it.action != null })
        assertEquals(emptySet<String>(), LauncherResetRules.resetHiddenPackages())
    }
}
