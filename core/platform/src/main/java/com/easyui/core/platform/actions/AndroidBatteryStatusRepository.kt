package com.easyui.core.platform.actions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.easyui.core.domain.model.BatteryStatus
import com.easyui.core.domain.repository.BatteryStatusRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidBatteryStatusRepository(
    private val context: Context,
) : BatteryStatusRepository {
    override fun observeBatteryStatus(): Flow<BatteryStatus> = callbackFlow {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    trySend(intent.toBatteryStatus())
                }
            }

        context.registerReceiver(receiver, filter)?.let { initialIntent ->
            trySend(initialIntent.toBatteryStatus())
        }

        awaitClose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }

    @android.annotation.SuppressLint("InlinedApi")
    private fun Intent?.toBatteryStatus(): BatteryStatus {
        if (this == null) return BatteryStatus()
        val level = getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage =
            if (level >= 0 && scale > 0) {
                ((level * 100f) / scale).toInt().coerceIn(0, 100)
            } else {
                null
            }
        val status = getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging =
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val isLow = getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, percentage != null && percentage <= 15)
        return BatteryStatus(
            percentage = percentage,
            isCharging = isCharging,
            isLow = isLow,
        )
    }
}
