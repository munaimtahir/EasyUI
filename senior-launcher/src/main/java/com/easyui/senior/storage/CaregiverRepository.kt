package com.easyui.senior.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.experimental.and

class CaregiverRepository(private val context: Context) {

    private val pinHashKey = stringPreferencesKey("caregiver_pin_hash")
    private val pinSaltKey = stringPreferencesKey("caregiver_pin_salt")
    private val failedAttemptsKey = intPreferencesKey("caregiver_failed_attempts")
    private val lockoutUntilKey = longPreferencesKey("caregiver_lockout_until")

    // State for pairing and remote permissions
    private val isPairedKey = stringPreferencesKey("caregiver_paired_state") // "unpaired", "pairing", "paired"
    private val caregiverIdKey = stringPreferencesKey("caregiver_id")
    private val caregiverNameKey = stringPreferencesKey("caregiver_name")
    private val pairingTokenKey = stringPreferencesKey("caregiver_pairing_token")
    private val permissionsKey = stringPreferencesKey("caregiver_permissions") // Comma-separated list of approved permissions

    data class CaregiverState(
        val isPinSet: Boolean,
        val failedAttempts: Int,
        val lockoutUntil: Long,
        val isPaired: String,
        val caregiverId: String?,
        val caregiverName: String?,
        val pairingToken: String?,
        val permissions: Set<String>
    )

    val stateFlow: Flow<CaregiverState> = context.coreDataStore.data.map { prefs ->
        val hash = prefs[pinHashKey] ?: ""
        val permissionsStr = prefs[permissionsKey] ?: ""
        val permissions = if (permissionsStr.isEmpty()) emptySet() else permissionsStr.split(",").toSet()
        CaregiverState(
            isPinSet = hash.isNotEmpty(),
            failedAttempts = prefs[failedAttemptsKey] ?: 0,
            lockoutUntil = prefs[lockoutUntilKey] ?: 0L,
            isPaired = prefs[isPairedKey] ?: "unpaired",
            caregiverId = prefs[caregiverIdKey],
            caregiverName = prefs[caregiverNameKey],
            pairingToken = prefs[pairingTokenKey],
            permissions = permissions
        )
    }

    suspend fun setPin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        context.coreDataStore.edit { prefs ->
            prefs[pinHashKey] = hash
            prefs[pinSaltKey] = salt
            prefs[failedAttemptsKey] = 0
            prefs[lockoutUntilKey] = 0L
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        var success = false
        context.coreDataStore.edit { prefs ->
            val now = System.currentTimeMillis()
            val lockout = prefs[lockoutUntilKey] ?: 0L
            if (now < lockout) {
                // Currently locked out
                return@edit
            }

            val storedHash = prefs[pinHashKey] ?: ""
            val storedSalt = prefs[pinSaltKey] ?: ""

            if (storedHash.isEmpty()) {
                // If not set, always fail or allow setup depending on UI logic
                return@edit
            }

            val hash = hashPin(pin, storedSalt)
            if (hash == storedHash) {
                success = true
                prefs[failedAttemptsKey] = 0
                prefs[lockoutUntilKey] = 0L
            } else {
                val attempts = (prefs[failedAttemptsKey] ?: 0) + 1
                prefs[failedAttemptsKey] = attempts
                if (attempts >= 5) {
                    prefs[lockoutUntilKey] = now + 30000L // 30 second lockout
                }
            }
        }
        return success
    }

    suspend fun resetPinAttempts() {
        context.coreDataStore.edit { prefs ->
            prefs[failedAttemptsKey] = 0
            prefs[lockoutUntilKey] = 0L
        }
    }

    suspend fun clearPin() {
        context.coreDataStore.edit { prefs ->
            prefs[pinHashKey] = ""
            prefs[pinSaltKey] = ""
            prefs[failedAttemptsKey] = 0
            prefs[lockoutUntilKey] = 0L
        }
    }

    // Remote Pairing State Helpers
    suspend fun setPairingState(state: String, caregiverId: String? = null, caregiverName: String? = null, token: String? = null) {
        context.coreDataStore.edit { prefs ->
            prefs[isPairedKey] = state
            if (caregiverId != null) prefs[caregiverIdKey] = caregiverId
            if (caregiverName != null) prefs[caregiverNameKey] = caregiverName
            if (token != null) prefs[pairingTokenKey] = token
        }
    }

    suspend fun setPermissions(permissions: Set<String>) {
        context.coreDataStore.edit { prefs ->
            prefs[permissionsKey] = permissions.joinToString(",")
        }
    }

    suspend fun clearPairing() {
        context.coreDataStore.edit { prefs ->
            prefs[isPairedKey] = "unpaired"
            prefs[caregiverIdKey] = ""
            prefs[caregiverNameKey] = ""
            prefs[pairingTokenKey] = ""
            prefs[permissionsKey] = ""
        }
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return bytesToHex(saltBytes)
    }

    private fun hashPin(pin: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt.toByteArray(Charsets.UTF_8))
        val hashedBytes = md.digest(pin.toByteArray(Charsets.UTF_8))
        return bytesToHex(hashedBytes)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = (bytes[i].toInt() and 0xFF)
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }
}
