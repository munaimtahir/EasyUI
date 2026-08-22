package com.easyui.senior.network

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.work.*
import com.easyui.senior.BuildConfig
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyui.senior.storage.coreDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * WorkManager periodic job that reports device status to the backend.
 * Runs every 15 minutes when the device is connected.
 * Only reports if the device is paired (has a token).
 */
class StatusReportWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val pairingManager = PairingManager(applicationContext)
        if (!pairingManager.refreshPairingCompletion()) {
            Log.d("StatusReportWorker", "Not paired, skipping status report")
            return@withContext Result.success()
        }
        val state = pairingManager.getState()

        if (!state.isPaired) {
            Log.d("StatusReportWorker", "Not paired, skipping status report")
            return@withContext Result.success()
        }

        if (!state.permissions.contains(PairingManager.PERMISSION_BATTERY)) {
            Log.d("StatusReportWorker", "Battery permission not granted, skipping")
            return@withContext Result.success()
        }

        BackendClient.deviceToken = state.deviceToken

        val battery = getBatteryInfo(applicationContext)
        val success = BackendClient.postStatus(
            batteryLevel = battery.first,
            isCharging = battery.second,
            appVersion = BuildConfig.VERSION_NAME
        )

        // Also fetch pending remote config and apply
        if (state.permissions.contains(PairingManager.PERMISSION_CONFIG)) {
            applyPendingConfig(applicationContext)
        }

        if (success) Result.success() else Result.retry()
    }

    private fun getBatteryInfo(context: Context): Pair<Int, Boolean> {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale) * 100).toInt() else -1
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        return Pair(batteryPct, isCharging)
    }

    private suspend fun applyPendingConfig(context: Context) {
        val config = BackendClient.fetchConfig() ?: return
        if (config.reminders.isEmpty()) return

        Log.d("StatusReportWorker", "Received ${config.reminders.size} remote reminders")
        val remindersKey = stringPreferencesKey("local_reminders")
        val prefs = context.coreDataStore.data.first()
        val raw = prefs[remindersKey] ?: ""

        val localMap = if (raw.isEmpty()) emptyList()
        else {
            raw.split(";").mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size >= 4) {
                    parts[0] to parts
                } else null
            }
        }
        val mutableLocalMap = localMap.toMap().toMutableMap()

        config.reminders.forEach { r ->
            mutableLocalMap[r.id] = listOf(r.id, r.title, r.type, r.time)
        }

        val updatedRaw = mutableLocalMap.values.joinToString(";") { it.joinToString("|") }
        context.coreDataStore.edit { it[remindersKey] = updatedRaw }
    }

    companion object {
        private const val WORK_NAME = "easyui_status_report"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<StatusReportWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        /** Report the initial device state as soon as a caregiver pairing completes. */
        fun enqueueImmediate(context: Context) {
            val request = OneTimeWorkRequestBuilder<StatusReportWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "${WORK_NAME}_initial",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        fun cancelAll(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
