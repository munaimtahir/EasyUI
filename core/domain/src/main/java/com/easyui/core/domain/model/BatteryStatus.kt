package com.easyui.core.domain.model

data class BatteryStatus(
    val percentage: Int? = null,
    val isCharging: Boolean = false,
    val isLow: Boolean = false,
)
