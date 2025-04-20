package tech.parkhurst
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.util.Collections
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import java.io.File


fun main() {
    embeddedServer(Netty, port = 8765) {
        install(WebSockets) {
            pingPeriod = 30.seconds
            timeout = 15.seconds
        }
        routing {
            // Thread-safe set for all connected sessions
            val sessions = Collections.synchronizedSet(mutableSetOf<DefaultWebSocketServerSession>())

            // Launch a coroutine for periodic broadcasting
            launch {
                while (true) {
                    delay(60_000)
                    val letter = ('A'..'Z').random()
                    // Broadcast the letter to all connected sessions
                    synchronized(sessions) {
                        sessions.forEach { session ->
                            launch {
                                session.send(letter.toString())
                            }
                        }
                    }
                    println("Broadcasted: $letter")
                }
            }


            webSocket("/") {
                sessions.add(this)
                try {
                    // Optionally, handle incoming messages here
                    for (frame in incoming) {
                        // Ignore messages or handle as needed
                        println(frame)
                    }
                } finally {
                    sessions.remove(this)
                }
            }
        }
    }.start(wait = true)
}
