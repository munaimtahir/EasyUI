package com.easyui.companion

import androidx.test.platform.app.InstrumentationRegistry
import com.easyui.companion.network.CompanionBackendClient
import com.easyui.companion.network.RemoteReminderDto
import com.easyui.companion.storage.CompanionSession
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * End-to-End device testing for Caregiver Companion app
 * running on the emulator/device against the live backend server on port 8088.
 */
class CompanionSpineE2ETest {

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var sessionManager: CompanionSession

    @Before
    fun setUp() = runBlocking {
        sessionManager = CompanionSession(context)
        sessionManager.clearSession()
        CompanionBackendClient.deviceToken = null
    }

    @Test
    fun testCompanionE2EWorkflows() = runBlocking {
        val initialSession = sessionManager.getSession()
        assertFalse("Session should be unpaired initially", initialSession.isPaired)
        val caregiverDeviceId = initialSession.caregiverDeviceId
        assertNotNull(caregiverDeviceId)

        // 1. Generate pairing on backend for a senior device
        val testSeniorId = "test-senior-${System.currentTimeMillis()}"
        val pairInitConn = java.net.URL("http://10.0.2.2:8088/initiate-pairing").openConnection() as java.net.HttpURLConnection
        pairInitConn.requestMethod = "POST"
        pairInitConn.setRequestProperty("Content-Type", "application/json")
        pairInitConn.doOutput = true
        pairInitConn.outputStream.use { it.write("""{"seniorDeviceId":"$testSeniorId"}""".toByteArray()) }
        val initResponse = pairInitConn.inputStream.bufferedReader().readText()
        val code = org.json.JSONObject(initResponse).getString("code")
        assertNotNull(code)

        // 2. Workflow A: Caregiver pairs using the code
        val pairResult = CompanionBackendClient.pairWithSenior(code, caregiverDeviceId)
        assertNotNull("Pairing must succeed", pairResult)
        assertEquals(testSeniorId, pairResult!!.seniorDeviceId)
        sessionManager.saveSession(pairResult.deviceToken, pairResult.seniorDeviceId, pairResult.permissions)
        assertTrue(sessionManager.getSession().isPaired)

        // 3. Senior posts initial status & checkin so caregiver can read it
        val statusConn = java.net.URL("http://10.0.2.2:8088/status").openConnection() as java.net.HttpURLConnection
        statusConn.requestMethod = "POST"
        statusConn.setRequestProperty("Content-Type", "application/json")
        statusConn.setRequestProperty("Authorization", "Bearer ${pairResult.deviceToken}")
        statusConn.doOutput = true
        statusConn.outputStream.use { it.write("""{"batteryLevel":95,"isCharging":true,"appVersion":"0.1.0","syncTimestamp":${System.currentTimeMillis()}}""".toByteArray()) }
        assertEquals(200, statusConn.responseCode)

        val checkinConn = java.net.URL("http://10.0.2.2:8088/checkin").openConnection() as java.net.HttpURLConnection
        checkinConn.requestMethod = "POST"
        checkinConn.setRequestProperty("Content-Type", "application/json")
        checkinConn.setRequestProperty("Authorization", "Bearer ${pairResult.deviceToken}")
        checkinConn.doOutput = true
        checkinConn.outputStream.use { it.write("""{"timestamp":${System.currentTimeMillis()},"message":"I am OK"}""".toByteArray()) }
        assertEquals(200, checkinConn.responseCode)

        // 4. Workflow B: Fetch status
        val status = CompanionBackendClient.fetchStatus(testSeniorId)
        assertNotNull("Status fetch must succeed", status)
        assertEquals(testSeniorId, status!!.seniorDeviceId)
        assertEquals(95, status.batteryLevel)
        assertTrue(status.isCharging)

        // 5. Workflow C: Fetch Check-in
        val checkin = CompanionBackendClient.fetchCheckIn(testSeniorId)
        assertNotNull("Check-in fetch must succeed", checkin)
        assertEquals("I am OK", checkin!!.message)

        // 6. Workflow D: Fetch alerts
        val alerts = CompanionBackendClient.fetchAlerts(testSeniorId)
        assertNotNull("Alerts fetch must succeed", alerts)

        // 7. Workflow E: Send remote reminders config
        val pushSuccess = CompanionBackendClient.sendConfig(
            testSeniorId,
            listOf(RemoteReminderDto("r-1", "Blood Pressure Medication", "Medication", "08:30"))
        )
        assertTrue("Reminder config push must succeed", pushSuccess)

        // 8. Workflow G: Caregiver delete account
        val deleteSuccess = CompanionBackendClient.deleteAccount()
        assertTrue("Delete account must succeed", deleteSuccess)
        sessionManager.clearSession()
        assertFalse(sessionManager.getSession().isPaired)
    }
}
