package com.easyui.core.domain.security

import com.easyui.core.domain.model.PinCredential
import java.security.MessageDigest
import java.security.SecureRandom

object PinHasher {
    fun create(pin: String): PinCredential {
        val salt = ByteArray(16).also(SecureRandom()::nextBytes)
        return PinCredential(
            saltHex = salt.toHex(),
            hashHex = hash(salt, pin),
        )
    }

    fun verify(pin: String, credential: PinCredential): Boolean =
        hash(credential.saltHex.hexToBytes(), pin) == credential.hashHex

    private fun hash(salt: ByteArray, pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        return digest.digest(pin.toByteArray()).toHex()
    }

    private fun ByteArray.toHex(): String =
        joinToString(separator = "") { byte -> "%02x".format(byte) }

    private fun String.hexToBytes(): ByteArray =
        chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
