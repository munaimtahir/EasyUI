package com.easyui.core.domain.rules

import com.easyui.core.domain.model.HomeTile
import com.easyui.core.domain.model.HomeTileAction
import com.easyui.core.domain.model.HomeTileType
import com.easyui.core.domain.model.InstalledApp
import org.junit.Assert.assertEquals
import org.junit.Test

class HiddenAppRulesTest {
    @Test
    fun `hidden apps are removed from launcher surfaces`() {
        val hidden = setOf("com.hidden")
        val apps = listOf(
            InstalledApp("com.hidden", "HiddenActivity", "Hidden"),
            InstalledApp("com.visible", "VisibleActivity", "Visible"),
        )
        val tiles = listOf(
            HomeTile("hidden", 0, "Hidden", HomeTileType.APP, packageName = "com.hidden"),
            HomeTile("phone", 1, "Phone", HomeTileType.ACTION, action = HomeTileAction.OPEN_DIALER),
        )

        assertEquals(listOf("Visible"), HiddenAppRules.visibleApps(apps, hidden).map { it.label })
        assertEquals(listOf("phone"), HiddenAppRules.visibleHomeTiles(tiles, hidden).map { it.id })
    }
}
