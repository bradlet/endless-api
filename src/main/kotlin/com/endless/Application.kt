package com.endless

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import mu.KotlinLogging
import org.slf4j.event.Level

private val logger = KotlinLogging.logger { }
fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    logger.info { "Starting server on port ${environment.config.port}" }

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/api") }
    }
    routes()
}
