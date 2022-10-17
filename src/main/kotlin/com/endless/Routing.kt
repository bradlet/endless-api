package com.endless

import com.endless.models.EndlessApiResponse
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

/**
 * Endless API routes
 * @author bradlet
 *
 * This is a fun little play project built to practice working with AWS architecture, from the perspective of someone
 * with prior experience in GCP.
 */
fun Application.routes() {
    routing {
        // Base path isn't going to have any custom behavior, just an intro:
        get("/") {
            call.respondText("Welcome to Endless API!\nCheckout any path to see what's there!")
        }

        get("/api/{...}") {
            call.respond(
                EndlessApiResponse(
                    path = call.request.path(),
                    content = "hello world!"
                )
            )
        }
    }
}
