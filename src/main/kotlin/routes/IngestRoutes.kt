package tech.parkhurst.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.ingestRoutes(){
    get("/helloworld"){
        call.respondText("Hello, world!")
    }
}