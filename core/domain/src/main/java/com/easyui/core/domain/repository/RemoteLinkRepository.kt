package com.easyui.core.domain.repository

import com.easyui.core.domain.model.LinkedDevice
import com.easyui.core.domain.model.RemoteStatusPacket
import kotlinx.coroutines.flow.Flow

interface RemoteLinkRepository {
    fun observeLinkedDevices(): Flow<List<LinkedDevice>>
    suspend fun addOrUpdateDevice(deviceName: String, packet: RemoteStatusPacket)
    suspend fun removeDevice(deviceId: String)
}
