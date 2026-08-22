package com.easyui.backend

import kotlinx.serialization.Serializable

@Serializable
data class PairRequest(val code: String, val caregiverDeviceId: String)

@Serializable
data class PairResponse(val deviceToken: String, val seniorDeviceId: String, val permissions: List<String>)

@Serializable
data class StatusPayload(val batteryLevel: Int, val isCharging: Boolean, val appVersion: String, val syncTimestamp: Long)

@Serializable
data class StatusResponse(val seniorDeviceId: String, val lastSeen: Long, val batteryLevel: Int, val isCharging: Boolean)

@Serializable
data class CheckInPayload(val timestamp: Long, val message: String = "I'm OK")

@Serializable
data class CheckInResponse(val acknowledged: Boolean, val timestamp: Long)

@Serializable
data class AlertPayload(val type: String, val timestamp: Long, val details: String = "")

@Serializable
data class AlertResponse(val alertId: String, val received: Boolean)

@Serializable
data class AlertListResponse(val alerts: List<StoredAlert>)

@Serializable
data class StoredAlert(val alertId: String, val type: String, val timestamp: Long, val details: String, val seen: Boolean)

@Serializable
data class ConfigPayload(val reminders: List<RemoteReminder>)

@Serializable
data class RemoteReminder(val id: String, val title: String, val type: String, val time: String)

@Serializable
data class ConfigResponse(val applied: Boolean)

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class PairingToken(
    val code: String,
    val seniorDeviceId: String,
    val expiresAt: Long,
    val completionSecret: String
)

@Serializable
data class PairingCompletion(
    val seniorDeviceId: String,
    val completionSecret: String,
    val seniorDeviceToken: String,
    val permissions: List<String>,
    val expiresAt: Long
)

@Serializable
data class PairingCompletionResponse(
    val seniorDeviceId: String,
    val deviceToken: String,
    val permissions: List<String>
)
