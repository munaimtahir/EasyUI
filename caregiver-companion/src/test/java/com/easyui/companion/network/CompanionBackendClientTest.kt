package com.easyui.companion.network

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.ServerSocket
import kotlin.concurrent.thread

/**
 * Regression coverage for fetchStatusResult(): 403 (not authorized) and 404 (no status
 * yet) used to collapse into the same generic null result. A raw socket stub stands in
 * for the backend so this runs as a plain JVM unit test with no extra dependencies.
 */
class CompanionBackendClientTest {

    private fun statusReason(code: Int): String = when (code) {
        200 -> "OK"
        403 -> "Forbidden"
        404 -> "Not Found"
        else -> "Unknown"
    }

    /** Accepts exactly one HTTP request on a fresh local port and replies with a fixed response. */
    private fun serveOnceAndPointClientAtIt(statusCode: Int, body: String?) {
        val serverSocket = ServerSocket(0)
        CompanionBackendClient.baseUrl = "http://127.0.0.1:${serverSocket.localPort}"
        CompanionBackendClient.deviceToken = "test-token"
        thread(isDaemon = true) {
            serverSocket.use { ss ->
                ss.accept().use { socket ->
                    val input = socket.getInputStream().bufferedReader()
                    while (true) {
                        val line = input.readLine() ?: break
                        if (line.isEmpty()) break // end of request headers
                    }
                    val out = socket.getOutputStream()
                    val response = StringBuilder("HTTP/1.1 $statusCode ${statusReason(statusCode)}\r\n")
                    if (body != null) {
                        response.append("Content-Type: application/json\r\n")
                        response.append("Content-Length: ${body.toByteArray().size}\r\n")
                    }
                    response.append("Connection: close\r\n\r\n")
                    out.write(response.toString().toByteArray())
                    if (body != null) out.write(body.toByteArray())
                    out.flush()
                }
            }
        }
    }

    @Test
    fun fetchStatusResult_returns403AsNotAuthorized() = runBlocking {
        serveOnceAndPointClientAtIt(403, null)
        val result = CompanionBackendClient.fetchStatusResult("test-senior")
        assertTrue("Expected NotAuthorized, got $result", result is SeniorStatusResult.NotAuthorized)
    }

    @Test
    fun fetchStatusResult_returns404AsNoStatusYet() = runBlocking {
        serveOnceAndPointClientAtIt(404, null)
        val result = CompanionBackendClient.fetchStatusResult("test-senior")
        assertTrue("Expected NoStatusYet, got $result", result is SeniorStatusResult.NoStatusYet)
    }

    @Test
    fun fetchStatusResult_returns200AsSuccess() = runBlocking {
        val body = """{"seniorDeviceId":"test-senior","lastSeen":123,"batteryLevel":80,"isCharging":true}"""
        serveOnceAndPointClientAtIt(200, body)
        val result = CompanionBackendClient.fetchStatusResult("test-senior")
        assertTrue("Expected Success, got $result", result is SeniorStatusResult.Success)
        assertEquals(80, (result as SeniorStatusResult.Success).status.batteryLevel)
    }
}
