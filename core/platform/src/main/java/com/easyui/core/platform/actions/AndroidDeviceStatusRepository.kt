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
    private val connectivityManager = appContext.getSystemService(ConnectivityManager::class.java)
    private val telephonyManager = appContext.getSystemService(TelephonyManager::class.java)
    private val subscriptionManager = appContext.getSystemService(SubscriptionManager::class.java)

    override fun observeDeviceStatus(): Flow<DeviceStatus> = flow {
        while (true) {
            emit(readStatus())
            delay(15_000)
        }
    }

    private fun readStatus(): DeviceStatus {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val wifiLabel = when {
            networkCapabilities == null -> "Wi-Fi offline"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi connected"
            else -> "Wi-Fi off"
        }

        val signalLabel = runCatching {
            val strength = telephonyManager.signalStrength?.level
            when (strength) {
                null -> "Signal unknown"
                0 -> "Signal none"
                1 -> "Signal weak"
                2 -> "Signal fair"
                3 -> "Signal good"
                else -> "Signal strong"
            }
        }.getOrDefault("Signal unknown")

        val simLabel = runCatching {
            val subscription = subscriptionManager.activeSubscriptionInfoList?.firstOrNull()
            val carrier = subscription?.carrierName?.toString()?.trim().orEmpty()
            if (carrier.isBlank()) "SIM" else "SIM $carrier"
        }.getOrDefault("SIM")

        return DeviceStatus(
            signalLabel = signalLabel,
            simLabel = simLabel,
            wifiLabel = wifiLabel,
        )
    }
}
