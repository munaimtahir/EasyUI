package com.easyui.core.domain.rules

import com.easyui.core.domain.model.InstalledApp
import org.junit.Assert.assertEquals
import org.junit.Test

class AppCatalogRulesTest {
    private val apps = listOf(
        InstalledApp("z.pkg", "A", "Zoom"),
        InstalledApp("a.pkg", "B", "Camera"),
        InstalledApp("m.pkg", "C", "Messages"),
    )

    @Test
    fun `sortAlphabetically orders by label`() {
        val sorted = AppCatalogRules.sortAlphabetically(apps)

        assertEquals(listOf("Camera", "Messages", "Zoom"), sorted.map { it.label })
    }

    @Test
    fun `filterByQuery matches labels and package names`() {
        val labelMatch = AppCatalogRules.filterByQuery(apps, "cam")
        val packageMatch = AppCatalogRules.filterByQuery(apps, "m.pkg")

        assertEquals(listOf("Camera"), labelMatch.map { it.label })
        assertEquals(listOf("Messages"), packageMatch.map { it.label })
    }
}
