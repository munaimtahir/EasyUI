package com.easyui.core.domain.model

data class DeviceStatus(
    val signalLabel: String = "Signal unknown",
    val simLabel: String = "SIM",
    val wifiLabel: String = "Wi-Fi unknown",
    val isInternetAvailable: Boolean = false,
    val isBatteryOptimized: Boolean = true,
)
