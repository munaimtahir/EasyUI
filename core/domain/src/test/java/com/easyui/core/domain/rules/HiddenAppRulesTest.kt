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
            HomeTile("all", 1, "All Apps", HomeTileType.ACTION, action = HomeTileAction.OPEN_APP_LIST),
        )

        assertEquals(listOf("Visible"), HiddenAppRules.visibleApps(apps, hidden).map { it.label })
        assertEquals(listOf("all"), HiddenAppRules.visibleHomeTiles(tiles, hidden).map { it.id })
    }
}
