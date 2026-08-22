package com.easyui.backend

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BackendTest {

    @Before
    fun setUp() {
        InMemoryStore.clearAll()
    }

    @Test
    fun testInitiatePairing() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/initiate-pairing") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("seniorDeviceId" to "test-senior-1"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<PairingToken>(response.bodyAsText())
        assertNotNull(body.code)
        assertEquals("test-senior-1", body.seniorDeviceId)
        assertTrue(body.expiresAt > System.currentTimeMillis())
    }

    @Test
    fun testPairingFailWithInvalidCode() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/pair") {
            contentType(ContentType.Application.Json)
            setBody(PairRequest("INVALID", "caregiver-1"))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testPairingSuccessWithValidCode() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Generate pairing code
        val token = InMemoryStore.initiatePairing("test-senior-1")

        val response = client.post("/pair") {
            contentType(ContentType.Application.Json)
            setBody(PairRequest(token.code, "caregiver-1"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<PairResponse>(response.bodyAsText())
        assertNotNull(body.deviceToken)
        assertEquals("test-senior-1", body.seniorDeviceId)
    }

    @Test
    fun testSeniorCompletesPairingWithItsPrivateCompletionSecret() = testApplication {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val pairing = InMemoryStore.initiatePairing("test-senior-1")
        val pairResponse = client.post("/pair") {
            contentType(ContentType.Application.Json)
            setBody(PairRequest(pairing.code, "caregiver-1"))
        }
        val caregiverSession = Json.decodeFromString<PairResponse>(pairResponse.bodyAsText())

        val completionResponse = client.get(
            "/pairing-status/test-senior-1?secret=${pairing.completionSecret}"
        )
        assertEquals(HttpStatusCode.OK, completionResponse.status)
        val completion = Json.decodeFromString<PairingCompletionResponse>(completionResponse.bodyAsText())
        assertEquals("test-senior-1", completion.seniorDeviceId)
        assertEquals(caregiverSession.permissions, completion.permissions)
        assertTrue(completion.deviceToken != caregiverSession.deviceToken)

        val statusPost = client.post("/status") {
            header(HttpHeaders.Authorization, "Bearer ${completion.deviceToken}")
            contentType(ContentType.Application.Json)
            setBody(StatusPayload(80, false, "1.0.1", System.currentTimeMillis()))
        }
        assertEquals(HttpStatusCode.OK, statusPost.status)

        val caregiverStatus = client.get("/status/test-senior-1") {
            header(HttpHeaders.Authorization, "Bearer ${caregiverSession.deviceToken}")
        }
        assertEquals(HttpStatusCode.OK, caregiverStatus.status)

        val rejectedSecret = client.get("/pairing-status/test-senior-1?secret=wrong-secret")
        assertEquals(HttpStatusCode.NotFound, rejectedSecret.status)
    }

    @Test
    fun testStatusAuthenticationRequired() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/status") {
            contentType(ContentType.Application.Json)
            setBody(StatusPayload(85, false, "1.0", System.currentTimeMillis()))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testPostStatusSuccess() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/status") {
            header(HttpHeaders.Authorization, "Bearer dev-senior-token")
            contentType(ContentType.Application.Json)
            setBody(StatusPayload(92, true, "1.0.0", System.currentTimeMillis()))
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // Caregiver fetches status
        val getResponse = client.get("/status/dev-senior-001") {
            header(HttpHeaders.Authorization, "Bearer dev-caregiver-token")
        }
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val statusRes = Json.decodeFromString<StatusResponse>(getResponse.bodyAsText())
        assertEquals(92, statusRes.batteryLevel)
        assertTrue(statusRes.isCharging)
    }

    @Test
    fun testGetStatusNotAuthorized() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Requester uses senior token of some other device
        val getResponse = client.get("/status/dev-senior-002") {
            header(HttpHeaders.Authorization, "Bearer dev-senior-token")
        }
        assertEquals(HttpStatusCode.Forbidden, getResponse.status)
    }

    @Test
    fun testGetStatusWithInvalidToken() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.get("/status/dev-senior-001") {
            header(HttpHeaders.Authorization, "Bearer invalid-token-xyz")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testPairingCodeReuseFails() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val token = InMemoryStore.initiatePairing("test-senior-1")

        // First complete pairing succeeds
        val res1 = client.post("/pair") {
            contentType(ContentType.Application.Json)
            setBody(PairRequest(token.code, "caregiver-1"))
        }
        assertEquals(HttpStatusCode.OK, res1.status)

        // Second complete pairing with same code fails
        val res2 = client.post("/pair") {
            contentType(ContentType.Application.Json)
            setBody(PairRequest(token.code, "caregiver-2"))
        }
        assertEquals(HttpStatusCode.Unauthorized, res2.status)
    }

    @Test
    fun testPairingCodeExpiryFails() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val token = InMemoryStore.initiatePairing("test-senior-1")
        
        // Fast-forward expiry
        val expiredToken = token.copy(expiresAt = System.currentTimeMillis() - 1000)
        InMemoryStore.pendingPairings[token.code] = expiredToken

        val response = client.post("/pair") {
            contentType(ContentType.Application.Json)
            setBody(PairRequest(token.code, "caregiver-1"))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testPermissionsEnforcedForCaregiver() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Setup caregiver relationship but with EMPTY permissions
        InMemoryStore.deviceTokens["custom-senior-token"] = "senior-1"
        InMemoryStore.caregiverTokens["custom-caregiver-token"] = "caregiver-1"
        InMemoryStore.caregiverToSenior["caregiver-1"] = "senior-1"
        InMemoryStore.permissions["senior-1"] = emptyList() // No permissions!

        // 1. Get status fails
        val statusRes = client.get("/status/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.Forbidden, statusRes.status)

        // 2. Get checkin fails
        val checkinRes = client.get("/checkin/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.Forbidden, checkinRes.status)

        // 3. Get alerts fails
        val alertsRes = client.get("/alerts/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.Forbidden, alertsRes.status)

        // 4. Post config fails
        val configRes = client.post("/config/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
            contentType(ContentType.Application.Json)
            setBody(ConfigPayload(emptyList()))
        }
        assertEquals(HttpStatusCode.Forbidden, configRes.status)
     }

    @Test
    fun testSeniorRevocation() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Setup caregiver relationship
        InMemoryStore.deviceTokens["custom-senior-token"] = "senior-1"
        InMemoryStore.caregiverTokens["custom-caregiver-token"] = "caregiver-1"
        InMemoryStore.caregiverToSenior["caregiver-1"] = "senior-1"
        InMemoryStore.permissions["senior-1"] = listOf("battery")
        InMemoryStore.deviceStatus["senior-1"] = StatusPayload(95, false, "1.0", 0L)

        // Caregiver fetches status successfully
        val statusResBefore = client.get("/status/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.OK, statusResBefore.status)

        // Senior revokes caregiver
        val revokeRes = client.post("/revoke") {
            header(HttpHeaders.Authorization, "Bearer custom-senior-token")
        }
        assertEquals(HttpStatusCode.OK, revokeRes.status)

        // Caregiver is now rejected
        val statusResAfter = client.get("/status/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.Unauthorized, statusResAfter.status)
    }

    @Test
    fun testCaregiverRevocation() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Setup caregiver relationship
        InMemoryStore.deviceTokens["custom-senior-token"] = "senior-1"
        InMemoryStore.caregiverTokens["custom-caregiver-token"] = "caregiver-1"
        InMemoryStore.caregiverToSenior["caregiver-1"] = "senior-1"

        // Caregiver revokes self
        val revokeRes = client.post("/revoke") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.OK, revokeRes.status)

        // Caregiver is now rejected on protected requests
        val checkinRes = client.get("/checkin/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.Unauthorized, checkinRes.status)
    }

    @Test
    fun testDeleteCaregiverAccount() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Setup caregiver relationship
        InMemoryStore.deviceTokens["custom-senior-token"] = "senior-1"
        InMemoryStore.caregiverTokens["custom-caregiver-token"] = "caregiver-1"
        InMemoryStore.caregiverToSenior["caregiver-1"] = "senior-1"

        // Caregiver deletes account
        val deleteRes = client.post("/delete-account") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.OK, deleteRes.status)

        // Caregiver token is destroyed and link is broken
        val statusRes = client.get("/status/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.Unauthorized, statusRes.status)
    }

    @Test
    fun testDeleteSeniorDevice() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Setup caregiver relationship + status
        InMemoryStore.deviceTokens["custom-senior-token"] = "senior-1"
        InMemoryStore.caregiverTokens["custom-caregiver-token"] = "caregiver-1"
        InMemoryStore.caregiverToSenior["caregiver-1"] = "senior-1"
        InMemoryStore.permissions["senior-1"] = listOf("battery")
        InMemoryStore.deviceStatus["senior-1"] = StatusPayload(95, false, "1.0", 0L)

        // Senior deletes device data
        val deleteRes = client.post("/delete-device") {
            header(HttpHeaders.Authorization, "Bearer custom-senior-token")
        }
        assertEquals(HttpStatusCode.OK, deleteRes.status)

        // Senior token is destroyed
        val statusResSenior = client.get("/status/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-senior-token")
        }
        assertEquals(HttpStatusCode.Unauthorized, statusResSenior.status)

        // Caregiver is revoked and token destroyed
        val statusResCaregiver = client.get("/status/senior-1") {
            header(HttpHeaders.Authorization, "Bearer custom-caregiver-token")
        }
        assertEquals(HttpStatusCode.Unauthorized, statusResCaregiver.status)
    }

    @Test
    fun testMalformedBearerHeaderRejected() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val res = client.get("/status/dev-senior-001") {
            header(HttpHeaders.Authorization, "Basic dXNlcjpwYXNz")
        }
        assertEquals(HttpStatusCode.Unauthorized, res.status)
    }

    @Test
    fun testCrossSeniorIdTamperingRejected() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Setup two distinct senior-caregiver pairs
        InMemoryStore.deviceTokens["senior-token-A"] = "senior-A"
        InMemoryStore.caregiverTokens["caregiver-token-A"] = "caregiver-A"
        InMemoryStore.caregiverToSenior["caregiver-A"] = "senior-A"
        InMemoryStore.permissions["senior-A"] = listOf("battery", "checkin")

        InMemoryStore.deviceTokens["senior-token-B"] = "senior-B"
        InMemoryStore.caregiverTokens["caregiver-token-B"] = "caregiver-B"
        InMemoryStore.caregiverToSenior["caregiver-B"] = "senior-B"
        InMemoryStore.permissions["senior-B"] = listOf("battery", "checkin")

        // Caregiver A attempts to query Senior B's status -> 403 Forbidden
        val res = client.get("/status/senior-B") {
            header(HttpHeaders.Authorization, "Bearer caregiver-token-A")
        }
        assertEquals(HttpStatusCode.Forbidden, res.status)
    }
}
