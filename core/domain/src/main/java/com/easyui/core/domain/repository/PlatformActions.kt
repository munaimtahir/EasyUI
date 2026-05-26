package com.easyui.core.domain.repository

import com.easyui.core.domain.model.BatteryStatus
import com.easyui.core.domain.model.DeviceStatus
import com.easyui.core.domain.model.LauncherActionState
import kotlinx.coroutines.flow.Flow

interface FlashlightController {
    suspend fun currentState(): LauncherActionState
    suspend fun performToggle(): LauncherActionState
}

interface EmergencyActionHandler {
    suspend fun currentState(phoneNumber: String?): LauncherActionState
    suspend fun launchDialer(phoneNumber: String?): Boolean
    suspend fun sendSms(phoneNumber: String, message: String): Boolean
    suspend fun callPhone(phoneNumber: String): Boolean
}

interface CameraActionHandler {
    suspend fun currentState(): LauncherActionState
    suspend fun launchCamera(): Boolean
}

interface AppLauncher {
    suspend fun launch(packageName: String, activityName: String): Boolean
}

interface DefaultLauncherManager {
    fun isDefaultLauncher(): Boolean
    fun openDefaultLauncherSettings()
    fun triggerLauncherChooser()
}

interface BatteryStatusRepository {
    fun observeBatteryStatus(): Flow<BatteryStatus>
}

interface DeviceStatusRepository {
    fun observeDeviceStatus(): Flow<DeviceStatus>
    fun requestIgnoreBatteryOptimizations()
}
