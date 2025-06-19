package tech.parkhurst
import com.mitteloupe.randomgenkt.FieldDataProvider
import com.mitteloupe.randomgenkt.builder.RandomGenBuilder
import com.mitteloupe.randomgenkt.fielddataprovider.ConcatenateFieldDataProvider
import com.mitteloupe.randomgenkt.fielddataprovider.ExplicitFieldDataProvider
import com.mitteloupe.randomgenkt.fielddataprovider.ExpressionFieldDataProvider
import com.mitteloupe.randomgenkt.fielddataprovider.GenericListFieldDataProvider
import com.mitteloupe.randomgenkt.fielddataprovider.IntFieldDataProvider
import com.mitteloupe.randomgenkt.fielddataprovider.PaddedFieldDataProvider
import com.x12q.kotlin.randomizer.lib.random
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import tech.parkhurst.config.UserSession
import tech.parkhurst.routes.streamingRoutes
import java.util.Collections
import kotlin.time.Duration.Companion.seconds
import java.io.File
import org.slf4j.LoggerFactory
import tech.parkhurst.modal.Response
import tech.parkhurst.services.GeneratorLogic
import java.security.KeyStore
import kotlin.system.exitProcess


//Todo finish streamingRoutes, and ingest routes(CRUD API)
// Setup connect to pocketbase auth db & bytebase lts postgres db



private fun ApplicationEngine.Configuration.envConfig() {

    val keyStore = KeyStore.getInstance("JKS").apply {
        File("src/main/resources/certs/keystore.jks").inputStream().use { load(it, "Panda1234".toCharArray()) }
    }
    connector {
        port = 8080
        host = "0.0.0.0"
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = "alias",
        keyStorePassword = { "X".toCharArray() },
        privateKeyPassword = { "X".toCharArray() }) {
        port = 8443
    }
}


fun main() {


    val generator:GeneratorLogic = GeneratorLogic()
    var unit =generator.generateUnit()


    embeddedServer(Netty, applicationEnvironment { log = LoggerFactory.getLogger("ktor.application") }, {envConfig()}) {
        install(WebSockets) {
            pingPeriod = 30.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        install(Sessions){
            header<UserSession>("user_session")
        }
        routing {

            // Thread-safe set for all connected sessions
            val sessions = Collections.synchronizedSet(mutableSetOf<DefaultWebSocketServerSession>())
            // Launch a coroutine for periodic broadcasting
            launch {
                while (true) {
                    delay(600_000)//Every 600 seconds do this action
                    //Global coroutine
                    synchronized(sessions) {
                        sessions.forEach { session ->
                            launch {
                                session.send("Connected: $session")
                            }
                        }
                    }
                }
            }
            streamingRoutes(sessions)
        }

    }.start(wait = true)
}
