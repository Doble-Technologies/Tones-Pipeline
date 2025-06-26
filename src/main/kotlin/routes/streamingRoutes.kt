package tech.parkhurst.routes

import io.ktor.http.ContentType.Application.Json
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import tech.parkhurst.modal.Response
import kotlin.random.Random
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.encodeToString
//import io.github.serpro69.kfaker.Faker
import com.x12q.kotlin.randomizer.lib.random
import tech.parkhurst.services.GeneratorLogic

//fun generateCall(): Response {
//    var test: Response = Response(1,4,3,"NHFD")
//
//    return test
//}
val generator: GeneratorLogic = GeneratorLogic()


fun Route.streamingRoutes(sessions: MutableSet<DefaultWebSocketServerSession>) {

    webSocket("/callfeed") {
        //we add them to the sessions so as new calls come in they get sent
        sessions.add(this)
        try {
            while (true){
                val randomValue = Random.nextLong(20_000, 120_000)
                delay(5_000)//5 seconds
                val jsonString = encodeToString(generator.generateCall())
                this.send(jsonString)
            }
        } finally {
            sessions.remove(this)
        }
    }
}


