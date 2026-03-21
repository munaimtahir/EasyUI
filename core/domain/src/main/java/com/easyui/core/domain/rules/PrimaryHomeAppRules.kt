package com.easyui.core.domain.rules

import com.easyui.core.domain.model.InstalledApp

enum class PrimaryHomeAppKind {
    MESSAGES,
    CONTACTS,
    PHOTOS,
}

object PrimaryHomeAppRules {
    private val preferredPackages = mapOf(
        PrimaryHomeAppKind.MESSAGES to listOf(
            "com.google.android.apps.messaging",
            "com.samsung.android.messaging",
            "com.android.mms",
        ),
        PrimaryHomeAppKind.CONTACTS to listOf(
            "com.google.android.contacts",
            "com.samsung.android.contacts",
            "com.android.contacts",
        ),
        PrimaryHomeAppKind.PHOTOS to listOf(
            "com.google.android.apps.photos",
            "com.sec.android.gallery3d",
            "com.miui.gallery",
            "com.oneplus.gallery",
        ),
    )

    fun resolve(kind: PrimaryHomeAppKind, apps: List<InstalledApp>): InstalledApp? {
        val preferred = preferredPackages.getValue(kind)
        preferred.forEach { packageName ->
            apps.firstOrNull { it.packageName == packageName }?.let { return it }
        }
        return apps.firstOrNull { matches(kind, it.label) }
    }

    private fun matches(kind: PrimaryHomeAppKind, label: String): Boolean {
        val normalized = label.lowercase()
        return when (kind) {
            PrimaryHomeAppKind.MESSAGES -> {
                "message" in normalized || "messages" in normalized || "sms" in normalized || "text" in normalized
            }
            PrimaryHomeAppKind.CONTACTS -> "contact" in normalized || "people" in normalized
            PrimaryHomeAppKind.PHOTOS -> {
                "photo" in normalized || "photos" in normalized || "gallery" in normalized || "pictures" in normalized
            }
        }
    }
}
