package com.easyui.core.domain.rules

import com.easyui.core.domain.model.BatteryStatus

object BatteryDisplayRules {
    fun summary(status: BatteryStatus?): String? {
        status ?: return null
        val percent = status.percentage ?: return "Battery unavailable"
        val detail = when {
            status.isCharging -> "Charging"
            status.isLow -> "Low"
            else -> "Not charging"
        }
        return "Battery $percent% · $detail"
    }
}
