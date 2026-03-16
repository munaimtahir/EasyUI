package com.easyui.core.domain.rules

import com.easyui.core.domain.model.InstalledApp
import java.util.Locale

object AppCatalogRules {
    fun sortAlphabetically(apps: List<InstalledApp>): List<InstalledApp> =
        apps.sortedWith(
            compareBy<InstalledApp> { it.label.lowercase(Locale.getDefault()) }
                .thenBy { it.packageName },
        )

    fun filterByQuery(apps: List<InstalledApp>, query: String): List<InstalledApp> {
        if (query.isBlank()) return sortAlphabetically(apps)
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        return sortAlphabetically(apps).filter { app ->
            app.label.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                app.packageName.lowercase(Locale.getDefault()).contains(normalizedQuery)
        }
    }
}
