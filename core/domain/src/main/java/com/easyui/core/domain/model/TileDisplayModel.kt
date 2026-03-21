package com.easyui.core.domain.model

data class TileDisplayModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val kind: TileDisplayKind,
    val packageName: String? = null,
    val activityName: String? = null,
    val avatarImageUri: String? = null,
    val avatarFallback: String? = null,
)

enum class TileDisplayKind {
    PHONE,
    MESSAGES,
    CONTACTS,
    PHOTOS,
    EMERGENCY,
    CAMERA,
}
