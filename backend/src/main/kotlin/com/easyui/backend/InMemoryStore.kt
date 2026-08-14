package com.easyui.backend

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object InMemoryStore {
    val pendingPairings = ConcurrentHashMap<String, PairingToken>()
    val deviceTokens = ConcurrentHashMap<String, String>() // token -> seniorDeviceId
    val caregiverTokens = ConcurrentHashMap<String, String>() // token -> caregiverDeviceId

    val deviceStatus = ConcurrentHashMap<String, StatusPayload>() // seniorDeviceId -> status
    val deviceStatusTimestamp = ConcurrentHashMap<String, Long>() // seniorDeviceId -> timestamp
    val checkIns = ConcurrentHashMap<String, CheckInPayload>() // seniorDeviceId -> last check-in
    val alerts = ConcurrentHashMap<String, MutableList<StoredAlert>>() // seniorDeviceId -> alerts
    val configs = ConcurrentHashMap<String, ConfigPayload>() // seniorDeviceId -> pending config

    val caregiverToSenior = ConcurrentHashMap<String, String>() // caregiverDeviceId -> seniorDeviceId
    val permissions = ConcurrentHashMap<String, List<String>>() // seniorDeviceId -> permissions

    init {
        // Seed dev tokens for local testing
        // senior side
        deviceTokens["dev-senior-token"] = "dev-senior-001"
        permissions["dev-senior-001"] = listOf("battery", "checkin", "config", "alerts")
        // caregiver side
        caregiverTokens["dev-caregiver-token"] = "dev-caregiver-001"
        caregiverToSenior["dev-caregiver-001"] = "dev-senior-001"
    }

    fun clearAll() {
        pendingPairings.clear()
        deviceTokens.clear()
        caregiverTokens.clear()
        deviceStatus.clear()
        deviceStatusTimestamp.clear()
        checkIns.clear()
        alerts.clear()
        configs.clear()
        caregiverToSenior.clear()
        permissions.clear()

        // Re-seed
        deviceTokens["dev-senior-token"] = "dev-senior-001"
        permissions["dev-senior-001"] = listOf("battery", "checkin", "config", "alerts")
        caregiverTokens["dev-caregiver-token"] = "dev-caregiver-001"
        caregiverToSenior["dev-caregiver-001"] = "dev-senior-001"
    }

    fun generateSeniorDeviceId(): String = UUID.randomUUID().toString()
    fun generateToken(): String = UUID.randomUUID().toString()

    fun generatePairingCode(): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..8)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun initiatePairing(seniorDeviceId: String): PairingToken {
        // Clean up expired pairings first
        val now = System.currentTimeMillis()
        pendingPairings.entries.removeIf { it.value.expiresAt < now }

        val code = generatePairingCode()
        val expiresAt = now + 600_000 // 10 minutes
        val token = PairingToken(code, seniorDeviceId, expiresAt)
        pendingPairings[code] = token
        return token
    }

    fun completePairing(code: String, caregiverDeviceId: String): PairResponse? {
        val now = System.currentTimeMillis()
        val pairing = pendingPairings[code] ?: return null
        if (pairing.expiresAt < now) {
            pendingPairings.remove(code)
            return null
        }

        // Complete pairing
        pendingPairings.remove(code)
        val token = generateToken()
        val seniorDeviceId = pairing.seniorDeviceId

        deviceTokens[token] = seniorDeviceId
        // Also map this token to the caregiver device ID so Ktor auth works for both
        caregiverTokens[token] = caregiverDeviceId
        caregiverToSenior[caregiverDeviceId] = seniorDeviceId

        val defaultPermissions = listOf("battery", "checkin", "config", "alerts")
        permissions[seniorDeviceId] = defaultPermissions

        return PairResponse(
            deviceToken = token,
            seniorDeviceId = seniorDeviceId,
            permissions = defaultPermissions
        )
    }
}
