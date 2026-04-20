package com.easyui.launcher.app

import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.TileDisplayModel

data class HomeUiState(
    val timeText: String = "",
    val dateText: String = "",
    val tiles: List<TileDisplayModel> = emptyList(),
    val pages: List<List<TileDisplayModel?>> = emptyList(),
    val skinConfig: SkinConfig = SkinConfig(),
)
