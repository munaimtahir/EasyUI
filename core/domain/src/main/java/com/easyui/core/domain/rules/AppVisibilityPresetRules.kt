package com.easyui.core.domain.rules

import com.easyui.core.domain.model.AppVisibilityPreset
import com.easyui.core.domain.model.InstalledApp

object AppVisibilityPresetRules {
    private val essentialsPackages = setOf(
        "com.google.android.dialer",
        "com.samsung.android.dialer",
        "com.android.dialer",
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",
        "com.google.android.apps.photos",
        "com.sec.android.gallery3d",
        "com.google.android.GoogleCamera",
        "com.sec.android.app.camera",
        "com.android.camera",
        "com.android.settings",
        "com.google.android.contacts",
        "com.samsung.android.contacts",
        "com.android.contacts",
    )

    private val minimalCommonPackages = essentialsPackages + setOf(
        "com.android.chrome",
        "com.google.android.gm",
        "com.whatsapp",
        "com.google.android.apps.maps",
        "com.google.android.youtube",
        "com.google.android.calendar",
    )

    fun hiddenPackagesForPreset(
        apps: List<InstalledApp>,
        preset: AppVisibilityPreset,
    ): Set<String> {
        if (preset == AppVisibilityPreset.CUSTOM) return emptySet()
        val includeExpandedCommon = preset == AppVisibilityPreset.MINIMAL_COMMON_APPS
        val visiblePackages = when (preset) {
            AppVisibilityPreset.ESSENTIALS_ONLY -> apps.filter { matches(it, essentialsPackages, includeExpandedCommon) }.map { it.packageName }.toSet()
            AppVisibilityPreset.MINIMAL_COMMON_APPS -> apps.filter { matches(it, minimalCommonPackages, includeExpandedCommon) }.map { it.packageName }.toSet()
            AppVisibilityPreset.CUSTOM -> emptySet()
        }
        if (visiblePackages.isNotEmpty()) {
            return apps.map { it.packageName }.filterNot { it in visiblePackages }.toSet()
        }
        val fallbackCount = if (preset == AppVisibilityPreset.ESSENTIALS_ONLY) 4 else 6
        val fallbackVisible = AppCatalogRules.sortAlphabetically(apps).take(fallbackCount).map { it.packageName }.toSet()
        return apps.map { it.packageName }.filterNot { it in fallbackVisible }.toSet()
    }

    private fun matches(
        app: InstalledApp,
        preferredPackages: Set<String>,
        includeExpandedCommon: Boolean,
    ): Boolean {
        if (app.packageName in preferredPackages) return true
        val normalized = app.label.lowercase()
        return when {
            "dial" in normalized || "phone" in normalized -> true
            "message" in normalized || "sms" in normalized -> true
            "camera" in normalized || "photo" in normalized || "gallery" in normalized -> true
            "settings" in normalized -> true
            "contact" in normalized -> true
            includeExpandedCommon && ("map" in normalized || "mail" in normalized || "whatsapp" in normalized || "youtube" in normalized || "calendar" in normalized || "chrome" in normalized || "browser" in normalized) -> true
            else -> false
        }
    }
}
