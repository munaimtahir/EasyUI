package com.easyui.backend

import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("InMemoryStore")

object InMemoryStore {
    val pendingPairings = ConcurrentHashMap<String, PairingToken>()
    val completedPairings = ConcurrentHashMap<String, PairingCompletion>()
    val deviceTokens = ConcurrentHashMap<String, String>() // token -> seniorDeviceId
    val caregiverTokens = ConcurrentHashMap<String, String>() // token -> caregiverDeviceId

    val deviceStatus = ConcurrentHashMap<String, StatusPayload>() // seniorDeviceId -> status
    val deviceStatusTimestamp = ConcurrentHashMap<String, Long>() // seniorDeviceId -> timestamp
    val checkIns = ConcurrentHashMap<String, CheckInPayload>() // seniorDeviceId -> last check-in
    val alerts = ConcurrentHashMap<String, MutableList<StoredAlert>>() // seniorDeviceId -> alerts
    val configs = ConcurrentHashMap<String, ConfigPayload>() // seniorDeviceId -> pending config

    val caregiverToSenior = ConcurrentHashMap<String, String>() // caregiverDeviceId -> seniorDeviceId
    val permissions = ConcurrentHashMap<String, List<String>>() // seniorDeviceId -> permissions

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val storageFile: File? by lazy {
        val path = System.getenv("EASYUI_STORAGE_FILE")
        if (!path.isNullOrBlank()) File(path) else null
    }

    init {
        val isProduction = System.getenv("EASYUI_ENV")?.equals("production", ignoreCase = true) == true
        val seedDev = System.getenv("EASYUI_SEED_DEV_TOKENS")?.toBoolean() ?: !isProduction

        if (seedDev) {
            seedDevelopmentData()
        }
        loadPersistentState()
    }

    fun seedDevelopmentData() {
        deviceTokens["dev-senior-token"] = "dev-senior-001"
        permissions["dev-senior-001"] = listOf("battery", "checkin", "config", "alerts")
        caregiverTokens["dev-caregiver-token"] = "dev-caregiver-001"
        caregiverToSenior["dev-caregiver-001"] = "dev-senior-001"
    }

    fun clearAll() {
        pendingPairings.clear()
        completedPairings.clear()
        deviceTokens.clear()
        caregiverTokens.clear()
        deviceStatus.clear()
        deviceStatusTimestamp.clear()
        checkIns.clear()
        alerts.clear()
        configs.clear()
        caregiverToSenior.clear()
        permissions.clear()

        val isProduction = System.getenv("EASYUI_ENV")?.equals("production", ignoreCase = true) == true
        val seedDev = System.getenv("EASYUI_SEED_DEV_TOKENS")?.toBoolean() ?: !isProduction
        if (seedDev) {
            seedDevelopmentData()
        }
        persistState()
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
        completedPairings.entries.removeIf { it.value.expiresAt < now }

        val code = generatePairingCode()
        val expiresAt = now + 600_000 // 10 minutes
        val token = PairingToken(code, seniorDeviceId, expiresAt, generateToken())
        pendingPairings[code] = token
        completedPairings.remove(seniorDeviceId)
        persistState()
        return token
    }

    fun completePairing(code: String, caregiverDeviceId: String): PairResponse? {
        // Atomic fetch-and-invalidate: remove first so a second concurrent
        // redemption of the same single-use code sees null immediately,
        // instead of racing a separate read against a later remove().
        val pairing = pendingPairings.remove(code) ?: return null
        val now = System.currentTimeMillis()
        if (pairing.expiresAt < now) {
            persistState()
            return null
        }

        // Complete pairing
        val seniorToken = generateToken()
        val caregiverToken = generateToken()
        val seniorDeviceId = pairing.seniorDeviceId

        // Revoke any token(s) previously issued to this caregiver device so a
        // stale token can't keep authorizing access after a re-pair.
        caregiverTokens.entries.removeIf { it.value == caregiverDeviceId }

        deviceTokens[seniorToken] = seniorDeviceId
        caregiverTokens[caregiverToken] = caregiverDeviceId
        caregiverToSenior[caregiverDeviceId] = seniorDeviceId

        val defaultPermissions = listOf("battery", "checkin", "config", "alerts")
        permissions[seniorDeviceId] = defaultPermissions
        completedPairings[seniorDeviceId] = PairingCompletion(
            seniorDeviceId = seniorDeviceId,
            completionSecret = pairing.completionSecret,
            seniorDeviceToken = seniorToken,
            permissions = defaultPermissions,
            expiresAt = pairing.expiresAt
        )
        persistState()

        return PairResponse(
            deviceToken = caregiverToken,
            seniorDeviceId = seniorDeviceId,
            permissions = defaultPermissions
        )
    }

    fun pairingCompletion(seniorDeviceId: String, completionSecret: String): PairingCompletion? {
        val completion = completedPairings[seniorDeviceId] ?: return null
        if (completion.expiresAt < System.currentTimeMillis()) {
            completedPairings.remove(seniorDeviceId)
            persistState()
            return null
        }
        return completion.takeIf { it.completionSecret == completionSecret }
    }

    fun persistState() {
        val file = storageFile ?: return
        try {
            val snapshot = StoreSnapshot(
                pendingPairings = HashMap(pendingPairings),
                completedPairings = HashMap(completedPairings),
                deviceTokens = HashMap(deviceTokens),
                caregiverTokens = HashMap(caregiverTokens),
                caregiverToSenior = HashMap(caregiverToSenior),
                permissions = HashMap(permissions),
                deviceStatus = HashMap(deviceStatus),
                deviceStatusTimestamp = HashMap(deviceStatusTimestamp),
                checkIns = HashMap(checkIns)
            )
            file.parentFile?.mkdirs()
            file.writeText(json.encodeToString(snapshot))
        } catch (e: Exception) {
            logger.warn("Failed to persist backend store state: ${e.message}")
        }
    }

    private fun loadPersistentState() {
        val file = storageFile ?: return
        if (!file.exists()) return
        try {
            val content = file.readText()
            val snapshot = json.decodeFromString<StoreSnapshot>(content)
            pendingPairings.putAll(snapshot.pendingPairings)
            completedPairings.putAll(snapshot.completedPairings)
            deviceTokens.putAll(snapshot.deviceTokens)
            caregiverTokens.putAll(snapshot.caregiverTokens)
            caregiverToSenior.putAll(snapshot.caregiverToSenior)
            permissions.putAll(snapshot.permissions)
            deviceStatus.putAll(snapshot.deviceStatus)
            deviceStatusTimestamp.putAll(snapshot.deviceStatusTimestamp)
            checkIns.putAll(snapshot.checkIns)
            logger.info("Loaded persistent backend store state from ${file.absolutePath}")
        } catch (e: Exception) {
            logger.warn("Failed to load persistent backend store state: ${e.message}")
        }
    }
}

@Serializable
data class StoreSnapshot(
    val pendingPairings: Map<String, PairingToken> = emptyMap(),
    val completedPairings: Map<String, PairingCompletion> = emptyMap(),
    val deviceTokens: Map<String, String> = emptyMap(),
    val caregiverTokens: Map<String, String> = emptyMap(),
    val caregiverToSenior: Map<String, String> = emptyMap(),
    val permissions: Map<String, List<String>> = emptyMap(),
    val deviceStatus: Map<String, StatusPayload> = emptyMap(),
    val deviceStatusTimestamp: Map<String, Long> = emptyMap(),
    val checkIns: Map<String, CheckInPayload> = emptyMap()
)
