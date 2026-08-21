package com.easyui.senior

import androidx.test.platform.app.InstrumentationRegistry
import com.easyui.senior.network.BackendClient
import com.easyui.senior.network.PairingManager
import com.easyui.senior.network.RemoteReminderDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * End-to-End device trust spine integration tests running on the emulator/device
 * against the live backend server on port 8088.
 *
 * Verifies Workflows A through G as defined in DEVICE_TESTING_PLAN.md:
 * - Workflow A: Pairing Code Generation & Trust Establishment
 * - Workflow B: Status Report Synchronization
 * - Workflow C: Senior "I'm OK" Manual Check-In
 * - Workflow D: SOS Emergency Alert Flow
 * - Workflow E: Remote Configuration / Reminders Sync
 * - Workflow F: Trust / Pairing Revocation
 * - Workflow G: Caregiver Account / Link Deletion
 */
class TrustSpineE2ETest {

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var pairingManager: PairingManager

    @Before
    fun setUp() {
        runBlocking {
            pairingManager = PairingManager(context)
            pairingManager.revokePairing()
        }
    }

    @Test
    fun testCompleteCaregiverSeniorTrustSpineE2E() = runBlocking {
        // =========================================================================
        // Workflow A: Pairing Code Generation & Trust Establishment
        // =========================================================================
        val stateBefore = pairingManager.getState()
        assertFalse("Should not be paired initially", stateBefore.isPaired)
        val seniorDeviceId = stateBefore.seniorDeviceId
        assertNotNull("Senior device ID must exist", seniorDeviceId)

        // 1. Senior requests pairing code
        val pairingCode = pairingManager.requestPairingCode()
        assertNotNull("Pairing code must be generated from backend", pairingCode)
        assertEquals("Pairing code must be 8 characters", 8, pairingCode!!.length)
        assertTrue("Pairing code must be uppercase alphanumeric", pairingCode.matches(Regex("^[A-Z0-9]{8}$")))

        // 2. Caregiver completes pairing using the code
        val caregiverDeviceId = "caregiver-device-e2e-${System.currentTimeMillis()}"
        val pairResponse = BackendClient.completePairing(pairingCode, caregiverDeviceId)
        assertNotNull("Caregiver pairing response must succeed", pairResponse)
        assertEquals("Senior device ID in response must match", seniorDeviceId, pairResponse!!.seniorDeviceId)
        assertNotNull("Device token must be issued", pairResponse.deviceToken)
        assertTrue("Permissions must include battery", pairResponse.permissions.contains("battery"))
        assertTrue("Permissions must include checkin", pairResponse.permissions.contains("checkin"))
        assertTrue("Permissions must include config", pairResponse.permissions.contains("config"))
        assertTrue("Permissions must include alerts", pairResponse.permissions.contains("alerts"))

        // 3. Senior applies device token and verifies paired state
        pairingManager.applyDeviceToken(pairResponse.deviceToken, pairResponse.permissions)
        val stateAfter = pairingManager.getState()
        assertTrue("Senior state must now be paired", stateAfter.isPaired)
        assertEquals("Persisted token must match", pairResponse.deviceToken, stateAfter.deviceToken)

        // =========================================================================
        // Workflow B: Status Report Synchronization
        // =========================================================================
        val statusPosted = BackendClient.postStatus(
            batteryLevel = 88,
            isCharging = true,
            appVersion = "0.1.0"
        )
        assertTrue("Status report must succeed", statusPosted)

        // =========================================================================
        // Workflow C: Senior "I'm OK" Manual Check-In
        // =========================================================================
        val checkInPosted = BackendClient.postCheckIn("I am doing great today!")
        assertTrue("Check-in submission must succeed", checkInPosted)

        // =========================================================================
        // Workflow D: SOS Emergency Alert Flow
        // =========================================================================
        val alertPosted = BackendClient.postAlert("SOS", "Urgent help needed at home")
        assertTrue("SOS alert submission must succeed", alertPosted)

        // =========================================================================
        // Workflow E: Remote Configuration / Reminders Sync
        // =========================================================================
        val fetchedConfig = BackendClient.fetchConfig()
        assertNotNull("Config fetch must succeed", fetchedConfig)

        // =========================================================================
        // Workflow F: Trust / Pairing Revocation
        // =========================================================================
        pairingManager.revokePairing()
        val stateAfterRevocation = pairingManager.getState()
        assertFalse("State must be unpaired after revocation", stateAfterRevocation.isPaired)
        assertNull("Device token must be cleared locally", stateAfterRevocation.deviceToken)

        // Verifying remote calls fail after revocation (unauthorized/forbidden)
        val statusAfterRevoke = BackendClient.postStatus(50, false, "0.1.0")
        assertFalse("Posting status after revocation must fail", statusAfterRevoke)

        // =========================================================================
        // Workflow G: Device Deletion Flow
        // =========================================================================
        // Re-establish a new pairing to test device deletion
        val newCode = pairingManager.requestPairingCode()
        assertNotNull(newCode)
        val newPairResponse = BackendClient.completePairing(newCode!!, caregiverDeviceId)
        assertNotNull(newPairResponse)
        pairingManager.applyDeviceToken(newPairResponse!!.deviceToken, newPairResponse.permissions)
        assertTrue(pairingManager.getState().isPaired)

        // Delete device data on server and locally
        pairingManager.deleteDeviceData()
        assertFalse("State must be unpaired after deleteDeviceData", pairingManager.getState().isPaired)
        val statusAfterDelete = BackendClient.postStatus(50, false, "0.1.0")
        assertFalse("Posting status after device deletion must fail", statusAfterDelete)
    }
}
