package com.easyui.core.domain.model

enum class HomeTileType {
    APP,
    ACTION,
    CONTACT,
}

enum class HomeTileAction {
    OPEN_APP_LIST,
    FLASHLIGHT,
    EMERGENCY,
}

data class HomeTile(
    val id: String,
    val position: Int,
    val title: String,
    val type: HomeTileType,
    val packageName: String? = null,
    val action: HomeTileAction? = null,
    val phoneNumber: String? = null,
    val photoUri: String? = null,
)
