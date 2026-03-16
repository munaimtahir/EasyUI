package com.easyui.core.domain.model

data class TileDisplayModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val kind: TileDisplayKind,
    val avatarImageUri: String? = null,
    val avatarFallback: String? = null,
)

enum class TileDisplayKind {
    APP,
    DIALER,
    APPS_LIST,
    FLASHLIGHT,
    EMERGENCY,
    CONTACT,
}
