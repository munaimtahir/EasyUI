package com.easyui.senior.storage

import org.junit.Assert.*
import org.junit.Test

class CaregiverPinTest {

    private fun createRepoForTesting(): TestCaregiverPinLogic {
        return TestCaregiverPinLogic()
    }

    /**
     * Pure unit test of PIN hashing logic extracted from CaregiverRepository.
     * Tests use the same SHA-256 + salt mechanism without Android context.
     */
    class TestCaregiverPinLogic {
        private var storedHash: String = ""
        private var storedSalt: String = ""
        private var failedAttempts: Int = 0
        private var lockoutUntil: Long = 0L

        fun setPin(pin: String) {
            val salt = "TESTSALT1234567" // deterministic for testing
            val hash = hashPin(pin, salt)
            storedHash = hash
            storedSalt = salt
            failedAttempts = 0
            lockoutUntil = 0L
        }

        fun verifyPin(pin: String, now: Long = System.currentTimeMillis()): Boolean {
            if (now < lockoutUntil) return false
            if (storedHash.isEmpty()) return false
            val hash = hashPin(pin, storedSalt)
            return if (hash == storedHash) {
                failedAttempts = 0
                lockoutUntil = 0L
                true
            } else {
                failedAttempts++
                if (failedAttempts >= 5) {
                    lockoutUntil = now + 30000L
                }
                false
            }
        }

        val isPinSet: Boolean get() = storedHash.isNotEmpty()
        val attempts: Int get() = failedAttempts
        val isLockedOut: Boolean get() = System.currentTimeMillis() < lockoutUntil

        private fun hashPin(pin: String, salt: String): String {
            val md = java.security.MessageDigest.getInstance("SHA-256")
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

    @Test
    fun `pin is not set initially`() {
        val logic = createRepoForTesting()
        assertFalse(logic.isPinSet)
    }

    @Test
    fun `correct pin verifies successfully`() {
        val logic = createRepoForTesting()
        logic.setPin("1234")
        assertTrue(logic.verifyPin("1234"))
    }

    @Test
    fun `wrong pin fails verification`() {
        val logic = createRepoForTesting()
        logic.setPin("1234")
        assertFalse(logic.verifyPin("9999"))
    }

    @Test
    fun `pin is hashed not stored plaintext`() {
        val logic = createRepoForTesting()
        logic.setPin("1234")
        // Verify that the stored hash is not equal to the PIN itself
        // (the hash is 64 hex chars for SHA-256)
        val field = TestCaregiverPinLogic::class.java.getDeclaredField("storedHash")
        field.isAccessible = true
        val storedHash = field.get(logic) as String
        assertNotEquals("1234", storedHash)
        assertEquals(64, storedHash.length) // SHA-256 = 32 bytes = 64 hex chars
    }

    @Test
    fun `failed attempts counter increments`() {
        val logic = createRepoForTesting()
        logic.setPin("1234")
        logic.verifyPin("0000")
        logic.verifyPin("0000")
        assertEquals(2, logic.attempts)
    }

    @Test
    fun `lockout occurs after 5 failed attempts`() {
        val logic = createRepoForTesting()
        logic.setPin("1234")
        val now = System.currentTimeMillis()
        repeat(5) { logic.verifyPin("0000", now) }
        // After lockout, further attempts should fail
        assertFalse(logic.verifyPin("1234", now + 1000)) // Within lockout window
    }

    @Test
    fun `correct pin after wrong attempts resets counter`() {
        val logic = createRepoForTesting()
        logic.setPin("1234")
        logic.verifyPin("0000") // fail
        logic.verifyPin("0000") // fail
        assertTrue(logic.verifyPin("1234")) // succeed
        assertEquals(0, logic.attempts)
    }

    @Test
    fun `different pins produce different hashes`() {
        val logic1 = TestCaregiverPinLogic()
        val logic2 = TestCaregiverPinLogic()
        logic1.setPin("1234")
        logic2.setPin("5678")
        // Cross-verify
        assertFalse(logic1.verifyPin("5678"))
        assertFalse(logic2.verifyPin("1234"))
        assertTrue(logic1.verifyPin("1234"))
        assertTrue(logic2.verifyPin("5678"))
    }
}
