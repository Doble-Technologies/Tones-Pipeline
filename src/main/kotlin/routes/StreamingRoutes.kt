package tech.parkhurst.routes

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlinx.serialization.json.Json.Default.encodeToString
import tech.parkhurst.services.GeneratorLogic

val generator: GeneratorLogic = GeneratorLogic()
private val logger = KotlinLogging.logger {}




fun Route.streamingRoutes(sessions: MutableSet<DefaultWebSocketServerSession>) {

    webSocket("/callfeed") {
        //we add them to the sessions so as new calls come in they get sent
        sessions.add(this)
        try {
            while (true){
                val randomValue = Random.nextLong(520_000, 680_000)
                delay(randomValue)//5 seconds
                val callData=generator.generateCall()


                val jsonCall = encodeToString(callData)
                this.send(jsonCall)
            }
        }catch(e: Exception){
            logger.error{ "Unkown error in call feed: $e" }
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
            logger.error {"Error in test feed $e"}
            this.send(e.toString())

        }finally {
            sessions.remove(this)
        }
    }
}


