package com.easyui.core.platform.actions

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.net.Uri
import android.content.ComponentName
import com.easyui.core.domain.model.DeviceStatus
import com.easyui.core.domain.repository.DeviceStatusRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AndroidDeviceStatusRepository(
    context: Context,
) : DeviceStatusRepository {
    private val appContext = context.applicationContext
    private val connectivityManager: ConnectivityManager? = appContext.getSystemService(ConnectivityManager::class.java)
    private val telephonyManager: TelephonyManager? = appContext.getSystemService(TelephonyManager::class.java)
    private val powerManager: PowerManager? = appContext.getSystemService(PowerManager::class.java)

    override fun observeDeviceStatus(): Flow<DeviceStatus> = flow {
        while (true) {
            val status =
                try {
                    readStatus()
                } catch (_: SecurityException) {
                    DeviceStatus()
                } catch (_: IllegalStateException) {
                    DeviceStatus()
                }
            emit(status)
            delay(15_000)
        }
    }

    @SuppressLint("NewApi", "MissingPermission")
    private fun readStatus(): DeviceStatus {
        val networkCapabilities = readNetworkCapabilities()
        val isInternetAvailable = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        
        val wifiLabel = when {
            networkCapabilities == null -> "Wi-Fi unknown"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi connected"
            else -> "Wi-Fi off"
        }

        val isBatteryOptimized = powerManager?.isIgnoringBatteryOptimizations(appContext.packageName) == false

        val signalLabel =
            try {
                val strength = telephonyManager?.signalStrength?.level
                when (strength) {
                    null -> "Signal unknown"
                    0 -> "Signal none"
                    1 -> "Signal weak"
                    2 -> "Signal fair"
                    3 -> "Signal good"
                    else -> "Signal strong"
                }
            } catch (_: SecurityException) {
                "Signal unknown"
            } catch (_: IllegalStateException) {
                "Signal unknown"
            }

        val simLabel =
            try {
                val carrier = telephonyManager?.networkOperatorName?.trim().orEmpty()
                if (carrier.isBlank()) "SIM" else "SIM $carrier"
            } catch (_: SecurityException) {
                "SIM"
            } catch (_: IllegalStateException) {
                "SIM"
            }

        return DeviceStatus(
            signalLabel = signalLabel,
            simLabel = simLabel,
            wifiLabel = wifiLabel,
            isInternetAvailable = isInternetAvailable,
            isBatteryOptimized = isBatteryOptimized,
        )
    }

    @SuppressLint("MissingPermission")
    private fun readNetworkCapabilities(): NetworkCapabilities? {
        val manager = connectivityManager ?: return null
        return try {
            manager.getNetworkCapabilities(manager.activeNetwork)
        } catch (_: SecurityException) {
            null
        } catch (_: IllegalStateException) {
            null
        }
    }

    override fun requestIgnoreBatteryOptimizations() {
        // 1. Standard Request Intent
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${appContext.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        // 2. OEM Specific Settings Fallbacks
        val manufacturer = android.os.Build.MANUFACTURER.lowercase()
        val oemIntent = when {
            manufacturer.contains("samsung") -> Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"))
            manufacturer.contains("vivo") -> Intent().setComponent(ComponentName("com.iqoo.powersaving", "com.iqoo.powersaving.PowerSavingManagerActivity"))
            manufacturer.contains("huawei") -> Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"))
            manufacturer.contains("xiaomi") -> Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"))
            else -> null
        }?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            if (intent.resolveActivity(appContext.packageManager) != null) {
                appContext.startActivity(intent)
            } else if (oemIntent != null && oemIntent.resolveActivity(appContext.packageManager) != null) {
                appContext.startActivity(oemIntent)
            } else {
                appContext.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } catch (e: Exception) {
            // Absolute fallback to App Details
            val detailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", appContext.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try { appContext.startActivity(detailsIntent) } catch (ex: Exception) {}
        }
    }
}
