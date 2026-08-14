package com.easyui.backend

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Application.configureRouting() {
    routing {
        // Public routes
        post("/initiate-pairing") {
            val req = call.receive<Map<String, String>>()
            val seniorDeviceId = req["seniorDeviceId"] ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing seniorDeviceId"))
            val token = InMemoryStore.initiatePairing(seniorDeviceId)
            call.respond(token)
        }

        post("/pair") {
            val req = call.receive<PairRequest>()
            val response = InMemoryStore.completePairing(req.code, req.caregiverDeviceId)
            if (response != null) {
                call.respond(response)
            } else {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid or expired pairing code"))
            }
        }

        // Authenticated routes
        authenticate("bearer-auth") {
            post("/status") {
                val principal = call.principal<UserIdPrincipal>()!!
                val seniorDeviceId = InMemoryStore.deviceTokens[principal.name]
                    ?: return@post call.respond(HttpStatusCode.Forbidden, ErrorResponse("Not a senior device"))

                val status = call.receive<StatusPayload>()
                InMemoryStore.deviceStatus[seniorDeviceId] = status
                InMemoryStore.deviceStatusTimestamp[seniorDeviceId] = System.currentTimeMillis()
                call.respond(HttpStatusCode.OK)
            }

            get("/status/{seniorDeviceId}") {
                val principal = call.principal<UserIdPrincipal>()!!
                val requesterToken = principal.name
                val seniorDeviceId = call.parameters["seniorDeviceId"] ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing seniorDeviceId"))

                // Verify authorization: is this a caregiver linked to the senior? OR is it the senior itself?
                val authorized = isAuthorizedForSenior(requesterToken, seniorDeviceId)
                if (!authorized) {
                    return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse("Not authorized to view status for this device"))
                }

                // Check permissions
                val allowedPerms = InMemoryStore.permissions[seniorDeviceId] ?: emptyList()
                if (!allowedPerms.contains("battery")) {
                    return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse("Battery permission not granted by senior"))
                }

                val status = InMemoryStore.deviceStatus[seniorDeviceId]
                val lastSeen = InMemoryStore.deviceStatusTimestamp[seniorDeviceId] ?: 0L
                if (status != null) {
                    call.respond(StatusResponse(
                        seniorDeviceId = seniorDeviceId,
                        lastSeen = lastSeen,
                        batteryLevel = status.batteryLevel,
                        isCharging = status.isCharging
                    ))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("No status reported yet"))
                }
            }

            post("/checkin") {
                val principal = call.principal<UserIdPrincipal>()!!
                val seniorDeviceId = InMemoryStore.deviceTokens[principal.name]
                    ?: return@post call.respond(HttpStatusCode.Forbidden, ErrorResponse("Not a senior device"))

                val payload = call.receive<CheckInPayload>()
                InMemoryStore.checkIns[seniorDeviceId] = payload
                call.respond(CheckInResponse(acknowledged = true, timestamp = payload.timestamp))
            }

            get("/checkin/{seniorDeviceId}") {
                val principal = call.principal<UserIdPrincipal>()!!
                val requesterToken = principal.name
                val seniorDeviceId = call.parameters["seniorDeviceId"] ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing seniorDeviceId"))

                val authorized = isAuthorizedForSenior(requesterToken, seniorDeviceId)
                if (!authorized) {
                    return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse("Not authorized"))
                }

                val checkIn = InMemoryStore.checkIns[seniorDeviceId]
                if (checkIn != null) {
                    call.respond(checkIn)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("No check-ins yet"))
                }
            }

            post("/alert") {
                val principal = call.principal<UserIdPrincipal>()!!
                val seniorDeviceId = InMemoryStore.deviceTokens[principal.name]
                    ?: return@post call.respond(HttpStatusCode.Forbidden, ErrorResponse("Not a senior device"))

                val payload = call.receive<AlertPayload>()
                val alertList = InMemoryStore.alerts.getOrPut(seniorDeviceId) { mutableListOf() }
                val alertId = UUID.randomUUID().toString()
                alertList.add(StoredAlert(
                    alertId = alertId,
                    type = payload.type,
                    timestamp = payload.timestamp,
                    details = payload.details,
                    seen = false
                ))
                call.respond(AlertResponse(alertId = alertId, received = true))
            }

            get("/alerts/{seniorDeviceId}") {
                val principal = call.principal<UserIdPrincipal>()!!
                val requesterToken = principal.name
                val seniorDeviceId = call.parameters["seniorDeviceId"] ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing seniorDeviceId"))

                val authorized = isAuthorizedForSenior(requesterToken, seniorDeviceId)
                if (!authorized) {
                    return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse("Not authorized"))
                }

                val list = InMemoryStore.alerts[seniorDeviceId] ?: emptyList<StoredAlert>()
                call.respond(AlertListResponse(alerts = list))
            }

            post("/config/{seniorDeviceId}") {
                val principal = call.principal<UserIdPrincipal>()!!
                val requesterToken = principal.name
                val seniorDeviceId = call.parameters["seniorDeviceId"] ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing seniorDeviceId"))

                val authorized = isAuthorizedForSenior(requesterToken, seniorDeviceId)
                if (!authorized) {
                    return@post call.respond(HttpStatusCode.Forbidden, ErrorResponse("Not authorized"))
                }

                val payload = call.receive<ConfigPayload>()
                InMemoryStore.configs[seniorDeviceId] = payload
                call.respond(ConfigResponse(applied = true))
            }

            get("/config") {
                val principal = call.principal<UserIdPrincipal>()!!
                val seniorDeviceId = InMemoryStore.deviceTokens[principal.name]
                    ?: return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse("Not a senior device"))

                // Retrieve and clear
                val config = InMemoryStore.configs.remove(seniorDeviceId) ?: ConfigPayload(emptyList())
                call.respond(config)
            }
        }
    }
}

private fun isAuthorizedForSenior(token: String, seniorDeviceId: String): Boolean {
    val tokenSeniorId = InMemoryStore.deviceTokens[token]
    if (tokenSeniorId == seniorDeviceId) return true

    val caregiverId = InMemoryStore.caregiverTokens[token] ?: return false
    val linkedSeniorId = InMemoryStore.caregiverToSenior[caregiverId]
    return linkedSeniorId == seniorDeviceId
}
