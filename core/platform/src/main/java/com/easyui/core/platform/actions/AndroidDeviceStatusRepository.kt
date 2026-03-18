package com.easyui.core.platform.actions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
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
    private val subscriptionManager: SubscriptionManager? = appContext.getSystemService(SubscriptionManager::class.java)

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

    private fun readStatus(): DeviceStatus {
        val networkCapabilities = readNetworkCapabilities()
        val wifiLabel = when {
            networkCapabilities == null -> "Wi-Fi unknown"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi connected"
            else -> "Wi-Fi off"
        }

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
                val subscription = subscriptionManager?.activeSubscriptionInfoList?.firstOrNull()
                val carrier = subscription?.carrierName?.toString()?.trim().orEmpty()
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
        )
    }

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
}
