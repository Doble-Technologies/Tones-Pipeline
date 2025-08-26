package tech.parkhurst.routes

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import tech.parkhurst.modal.NotificationRequest
import com.google.auth.oauth2.GoogleCredentials
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import tech.parkhurst.modal.FcmMessage
import tech.parkhurst.modal.FcmNotification
import tech.parkhurst.modal.FcmRequest
import java.io.FileInputStream
import java.io.FileNotFoundException
import kotlin.text.decodeToString

private val logger = KotlinLogging.logger {}



fun getAccessToken(): String {
    val configPath = System.getenv("TONES_CONFIG_PATH") ?: "src/main/resources/tones-config.json"
    val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream("tones-config.json")
    val serviceAccountStream = resourceStream ?: try { FileInputStream(configPath) } catch (e: FileNotFoundException) { null }
    if (serviceAccountStream == null) {
        throw FileNotFoundException("Service account file not found at $configPath or classpath.")
    }
    val credentials = GoogleCredentials.fromStream(serviceAccountStream)
        .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
    credentials.refreshIfExpired()
    println("Before Return")
    return credentials.accessToken.tokenValue
}

fun Route.notificationRoutes(client: HttpClient) {
    post("/sendnotification") {
        //TODO: Error handling if no body is passed
        logger.info { "Registered" }
        val encoded = call.receive<ByteArray>()
        val payload = Json.decodeFromString<NotificationRequest>(encoded.decodeToString())
        logger.info {payload.toString()}
        val accessToken = try {
            getAccessToken()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to load service account: ${e.message}"))
            return@post
        }
        logger.info { accessToken}
        logger.info {"PASSED"}

        if (payload.topics.isNotEmpty()) {
            val results = mutableListOf<Map<String, String>>()
            payload.topics.forEach { topic ->
                val fcmRequest = FcmRequest(
                    message = FcmMessage(
                        topic = topic,
                        notification = FcmNotification(
                            title = payload.title,
                            body = payload.body,
                            sound = "default"
                        ),
                        data = payload.data
                    )
                )
                try {

                    logger.info {"Bearer $accessToken"}

                    logger.info {"fcmRequest $fcmRequest.toString()"}
                    val response = client.post("https://fcm.googleapis.com/v1/projects/tones-9f1d4/messages:send") {
                        header("Authorization", "Bearer $accessToken")
                        contentType(ContentType.Application.Json)
                        setBody(fcmRequest)
                    }
                    logger.info { "Might be crazy bout what imma say: ${response.toString()}" }
                    results.add(mapOf("topic" to topic, "status" to "sent", "fcmResponse" to response.bodyAsText()))
                } catch (e: Exception) {
                    logger.error { e.toString() }
                    e.printStackTrace()
                    logger.error { e.message }
                    results.add(mapOf("topic" to topic, "error" to "Failed to send notification: ${e.message}"))
                }
            }
            logger.info {"Success"}
            call.respondText(results.toString(), contentType = ContentType.Text.Plain)
            return@post
        } else {
            call.respondText(
                "At least 1 Topic is Required",
                ContentType.Text.Plain,
                HttpStatusCode(491, "Topic Required")
            )
            return@post
        }
    }
}
