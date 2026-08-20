package com.easyui.backend

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8088
    println("Starting easyui backend server on port $port...")
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(cause.localizedMessage ?: "Unknown error"))
        }
    }

    install(Authentication) {
        bearer("bearer-auth") {
            realm = "Access to easyui server"
            authenticate { credential ->
                val token = credential.token
                if (InMemoryStore.deviceTokens.containsKey(token) || InMemoryStore.caregiverTokens.containsKey(token)) {
                    UserIdPrincipal(token)
                } else {
                    null
                }
            }
        }
    }

    configureRouting()
}
