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
        get("/health") {
            call.respond(mapOf("status" to "healthy"))
        }

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

        get("/pairing-status/{seniorDeviceId}") {
            val seniorDeviceId = call.parameters["seniorDeviceId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing seniorDeviceId"))
            val completionSecret = call.request.queryParameters["secret"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing pairing completion secret"))
            val completion = InMemoryStore.pairingCompletion(seniorDeviceId, completionSecret)
                ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Pairing has not completed"))
            call.respond(
                PairingCompletionResponse(
                    seniorDeviceId = completion.seniorDeviceId,
                    deviceToken = completion.seniorDeviceToken,
                    permissions = completion.permissions
                )
            )
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
                if (!hasPermission(requesterToken, seniorDeviceId, "battery")) {
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

                if (!hasPermission(requesterToken, seniorDeviceId, "checkin")) {
                    return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse("Checkin permission not granted by senior"))
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

                if (!hasPermission(requesterToken, seniorDeviceId, "alerts")) {
                    return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse("Alerts permission not granted by senior"))
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

                if (!hasPermission(requesterToken, seniorDeviceId, "config")) {
                    return@post call.respond(HttpStatusCode.Forbidden, ErrorResponse("Config permission not granted by senior"))
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

            post("/revoke") {
                val principal = call.principal<UserIdPrincipal>()!!
                val requesterToken = principal.name

                val seniorDeviceId = InMemoryStore.deviceTokens[requesterToken]
                if (seniorDeviceId != null) {
                    // Senior is revoking
                    val linkedCaregiverIds = InMemoryStore.caregiverToSenior.filter { it.value == seniorDeviceId }.keys
                    for (cgId in linkedCaregiverIds) {
                        InMemoryStore.caregiverToSenior.remove(cgId)
                        InMemoryStore.caregiverTokens.entries.removeIf { it.value == cgId }
                    }
                    InMemoryStore.permissions.remove(seniorDeviceId)
                    call.respond(HttpStatusCode.OK, mapOf("revoked" to true))
                } else {
                    // Caregiver is revoking
                    val caregiverDeviceId = InMemoryStore.caregiverTokens[requesterToken]
                    if (caregiverDeviceId != null) {
                        InMemoryStore.caregiverToSenior.remove(caregiverDeviceId)
                        InMemoryStore.caregiverTokens.remove(requesterToken)
                        call.respond(HttpStatusCode.OK, mapOf("revoked" to true))
                    } else {
                        call.respond(HttpStatusCode.Forbidden, ErrorResponse("Unknown token type"))
                    }
                }
            }

            post("/delete-account") {
                val principal = call.principal<UserIdPrincipal>()!!
                val requesterToken = principal.name

                val caregiverDeviceId = InMemoryStore.caregiverTokens[requesterToken]
                if (caregiverDeviceId != null) {
                    InMemoryStore.caregiverToSenior.remove(caregiverDeviceId)
                    InMemoryStore.caregiverTokens.remove(requesterToken)
                    call.respond(HttpStatusCode.OK, mapOf("deleted" to true))
                } else {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse("Only caregiver can delete caregiver account"))
                }
            }

            post("/delete-device") {
                val principal = call.principal<UserIdPrincipal>()!!
                val requesterToken = principal.name

                val seniorDeviceId = InMemoryStore.deviceTokens[requesterToken]
                if (seniorDeviceId != null) {
                    InMemoryStore.deviceTokens.remove(requesterToken)
                    InMemoryStore.deviceStatus.remove(seniorDeviceId)
                    InMemoryStore.deviceStatusTimestamp.remove(seniorDeviceId)
                    InMemoryStore.checkIns.remove(seniorDeviceId)
                    InMemoryStore.alerts.remove(seniorDeviceId)
                    InMemoryStore.configs.remove(seniorDeviceId)
                    InMemoryStore.permissions.remove(seniorDeviceId)

                    val linkedCaregiverIds = InMemoryStore.caregiverToSenior.filter { it.value == seniorDeviceId }.keys
                    for (cgId in linkedCaregiverIds) {
                        InMemoryStore.caregiverToSenior.remove(cgId)
                        InMemoryStore.caregiverTokens.entries.removeIf { it.value == cgId }
                    }
                    call.respond(HttpStatusCode.OK, mapOf("deleted" to true))
                } else {
                    call.respond(HttpStatusCode.Forbidden, ErrorResponse("Only senior can delete senior device data"))
                }
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

private fun hasPermission(token: String, seniorDeviceId: String, permission: String): Boolean {
    val tokenSeniorId = InMemoryStore.deviceTokens[token]
    if (tokenSeniorId == seniorDeviceId) return true

    val allowedPerms = InMemoryStore.permissions[seniorDeviceId] ?: emptyList()
    return allowedPerms.contains(permission)
}
