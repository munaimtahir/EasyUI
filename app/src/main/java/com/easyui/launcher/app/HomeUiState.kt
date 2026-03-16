package com.easyui.launcher.app

import com.easyui.core.domain.model.HomeReadabilityPreset
import com.easyui.core.domain.model.TileDisplayModel

data class HomeUiState(
    val timeText: String = "",
    val dateText: String = "",
    val tiles: List<TileDisplayModel> = emptyList(),
    val readabilityPreset: HomeReadabilityPreset = HomeReadabilityPreset.STANDARD,
    val verySimpleModeEnabled: Boolean = false,
    val fallbackTitle: String? = null,
    val fallbackBody: String? = null,
)
