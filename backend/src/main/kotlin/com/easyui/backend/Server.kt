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
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("EasyUIBackend")

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8088
    val host = System.getenv("HOST") ?: "0.0.0.0"
    val env = System.getenv("EASYUI_ENV") ?: "development"
    logger.info("Starting EasyUI backend server [$env] on $host:$port...")
    embeddedServer(Netty, port = port, host = host, module = Application::module)
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

    val isProduction = System.getenv("EASYUI_ENV")?.equals("production", ignoreCase = true) == true

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error("Unhandled server exception: ${cause.message}", cause)
            val errorMessage = if (isProduction) "Internal server error" else (cause.localizedMessage ?: "Internal server error")
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(errorMessage))
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
