package com.easyui.core.domain.repository

import com.easyui.core.domain.model.BatteryStatus
import com.easyui.core.domain.model.LauncherActionState
import kotlinx.coroutines.flow.Flow

interface FlashlightController {
    suspend fun currentState(): LauncherActionState
    suspend fun performToggle(): LauncherActionState
}

interface EmergencyActionHandler {
    suspend fun currentState(phoneNumber: String?): LauncherActionState
    suspend fun launchDialer(phoneNumber: String?): Boolean
}

interface AppLauncher {
    suspend fun launch(packageName: String, activityName: String): Boolean
}

interface DefaultLauncherManager {
    fun isDefaultLauncher(): Boolean
    fun openDefaultLauncherSettings()
}

interface BatteryStatusRepository {
    fun observeBatteryStatus(): Flow<BatteryStatus>
}
