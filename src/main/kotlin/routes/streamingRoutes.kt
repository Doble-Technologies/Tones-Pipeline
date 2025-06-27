package tech.parkhurst.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlinx.serialization.json.Json.Default.encodeToString
import tech.parkhurst.services.GeneratorLogic

val generator: GeneratorLogic = GeneratorLogic()


fun Route.streamingRoutes(sessions: MutableSet<DefaultWebSocketServerSession>) {

    webSocket("/callfeed") {
        //we add them to the sessions so as new calls come in they get sent
        sessions.add(this)
        try {
            while (true){
                val randomValue = Random.nextLong(520_000, 680_000)
                delay(randomValue)//5 seconds
                val jsonString = encodeToString(generator.generateCall())
                //Send to db as a json file
                this.send(jsonString)
            }
        }catch(e: Exception){
            println(e.toString())
            this.send(e.toString())

        }finally {
            sessions.remove(this)
        }
    }

    webSocket("/testwss") {
        //we add them to the sessions so as new calls come in they get sent
        sessions.add(this)
        try {
            while (true){
                val randomValue = Random.nextLong(520_000, 680_000)
                delay(5_000)//5 seconds
                this.send("CONNECTED")
            }
        }catch(e: Exception){
            println(e.toString())
            this.send(e.toString())

        }finally {
            sessions.remove(this)
        }
    }
}


