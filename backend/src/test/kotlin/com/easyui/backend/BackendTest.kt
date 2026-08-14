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
}
