package com.easyui.core.domain.model

import java.time.LocalDateTime

data class RemoteStatusPacket(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceName: String,
    val healthState: PhoneHealthState,
    val setupCompleteness: SetupCompleteness
)

data class LinkedDevice(
    val id: String,
    val name: String,
    val lastReceivedPacket: RemoteStatusPacket?,
    val lastReceivedAt: Long?
)
