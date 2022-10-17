package endless.com

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.routes() {
    routing {
        get("/api") {
            call.respondText("Hello World!")
        }
    }
}
