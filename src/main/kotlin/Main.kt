package tech.parkhurst
import io.github.oshai.kotlinlogging.KotlinLogging
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
import org.slf4j.LoggerFactory
import tech.parkhurst.config.connectToDatabase
import tech.parkhurst.routes.ingestRoutes
import java.security.KeyStore
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.system.exitProcess
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import tech.parkhurst.routes.notificationRoutes

private val logger = KotlinLogging.logger {}

//Todo finish streamingRoutes, and ingest routes(CRUD API)
// Setup connect to pocketbase auth db & postgress dbs
private fun ApplicationEngine.Configuration.envConfig() {
    val keyAlias: String = System.getenv("keyAlias") ?: ""
    val jksPass: String = System.getenv("jksPass") ?: ""

    val keyStore = KeyStore.getInstance("JKS").apply {
        val keystoreStream = object {}.javaClass.getResourceAsStream("/certs/keystore.jks")
        requireNotNull(keystoreStream) { "Keystore not found in resources!" }
        keystoreStream.use {
            load(it, jksPass.toCharArray())
        }
    }

    connector {
        host = "127.0.0.1"
        port = 9090
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = keyAlias,
        keyStorePassword = { jksPass.toCharArray() },
        privateKeyPassword = { jksPass.toCharArray() }) {
        port = 8443
    }
}



fun main() {
    val expectedApiKey: String = System.getenv("apiKey") ?: ""
    if(expectedApiKey == ""){
        logger.error { "Please Pass in Generated API Key for Users" }
        exitProcess(99);
    }

    val client = HttpClient(CIO)

    embeddedServer(Netty, applicationEnvironment { log = LoggerFactory.getLogger("ktor.application") }, {envConfig()}) {
        install(Authentication) {
            provider("apiKey") {
                authenticate { context ->
                    var apiKey = context.call.request.headers["apiKey"]
                    if(apiKey==null){
                        apiKey=context.call.parameters["apiKey"]
                    }
                    if (apiKey == expectedApiKey) {
                        context.principal(UserIdPrincipal("api-user"))
                    } else {
                        context.challenge("apiKey", AuthenticationFailedCause.InvalidCredentials) { challenge, call ->
                            call.respond(HttpStatusCode.Unauthorized, "Invalid or missing API key")
                            challenge.complete()
                        }
                    }
                }
            }
        }
        install(RateLimit) {
            register(RateLimitName("protected")) {
                rateLimiter(limit = 20, refillPeriod = 60.seconds)
            }
        }
        install(WebSockets) {
            pingPeriod = 30.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        install(Sessions){
            //Todo: Utilize this further(doesn't do anything atm)
            header<UserSession>("user_session")
        }
        connectToDatabase()
        routing {
            // Thread-safe set for all connected sessions
            val sessions = Collections.synchronizedSet(mutableSetOf<DefaultWebSocketServerSession>())
            // Launch a coroutine for periodic broadcasting
            authenticate("apiKey") {
                launch {
                    //5 Min Connected Messages
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
                rateLimit(RateLimitName("protected")){
                    ingestRoutes()
                }
                notificationRoutes(client)
            }
        }
        environment.monitor.subscribe(ApplicationStopped) {
            client.close()
        }

    }.start(wait = true)
}
