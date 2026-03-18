package com.easyui.launcher.app

import com.easyui.core.domain.model.SkinConfig
import com.easyui.core.domain.model.TileDisplayModel

data class HomeUiState(
    val timeText: String = "",
    val batteryPercent: String = "--%",
    val chargingLabel: String = "Not charging",
    val signalLabel: String = "Signal unknown",
    val simLabel: String = "SIM",
    val wifiLabel: String = "Wi-Fi unknown",
    val tiles: List<TileDisplayModel> = emptyList(),
    val sosTriggerProgress: Int = 0,
    val skinConfig: SkinConfig = SkinConfig(),
)
