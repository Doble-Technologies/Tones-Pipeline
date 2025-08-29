package tech.parkhurst.routes

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import tech.parkhurst.GlobalStore
import tech.parkhurst.modal.Call
import tech.parkhurst.modal.CreateUserParams
import tech.parkhurst.modal.GetCallsParams
import tech.parkhurst.modal.tables.toStrings
import tech.parkhurst.services.*
import java.sql.SQLException

val gen = GeneratorLogic()

private val logger = KotlinLogging.logger {}


fun Route.ingestRoutes(){
    get("/generateCall"){
        //Todo: Rewrite to a single try catch with better error catching
        var generatedCall: Call?= null
        try{
            generatedCall = gen.generateCall()
        }catch (e: Exception){
            call.respondText("Error Generating")
        }
        try{
            if (generatedCall != null) {
                insertCallData(generatedCall)
                GlobalStore.pendingCalls.add(generatedCall)
                call.respondText(generatedCall.toStrings())
            }
        }catch (e: Exception){
            call.respondText("Error Inserting")
        }
    }

    get("/version"){
        call.respondText("{'version': 1.0.9}")
    }

    }


    get("/getCall/{callId}"){
        var callId=-1
        try{
            callId =Integer.parseInt(call.parameters["callId"])
        }catch(e: Exception){
            call.response.status(HttpStatusCode.BadRequest)
        }
        if(callId  > 0){
            call.respond(getCall(callId))
        }else{
            call.response.status(HttpStatusCode.BadRequest)
        }
    }

    get("/getAllCalls"){
        call.respond(getAllCalls())
    }

    //1-100 most recent calls
    get("/getRecentCalls/{numCalls}"){
        var numCalls=-1
        try{
            numCalls =Integer.parseInt(call.parameters["numCalls"])
        }catch(e: Exception){
            call.response.status(HttpStatusCode.BadRequest)
        }
        if(numCalls in 1..99){
            call.respond(getRecentCalls(numCalls))
        }else{
            call.response.status(HttpStatusCode.BadRequest)
        }
    }

    post("/getCallsParams"){
        val parameters = call.receive<ByteArray>()
        val decoded = Json.decodeFromString<GetCallsParams>(parameters.decodeToString())
        call.respond(getCallsParams(decoded))
    }

    post("/createUser") {
        try {
            val parameters = call.receive<ByteArray>()
            val decoded = Json.decodeFromString<CreateUserParams>(parameters.decodeToString())
            call.respond(
                createUser(decoded).toString()
            )
        } catch (e: Exception) {
            val (status, error, details) = when (e) {
                is SerializationException -> Triple(
                    HttpStatusCode(470, "Invalid Call Data"),
                    "Failed to parse request body into Call object",
                    e.message ?: "Unknown serialization error"
                )
                is SQLException -> Triple(
                    HttpStatusCode(471, "Database Insert Error"),
                    "Failed to insert call data into database",
                    e.message ?: "Unknown SQL error"
                )
                else -> Triple(
                    HttpStatusCode.InternalServerError,
                    "Unexpected server error",
                    e.message ?: "No details available"
                )
            }

            call.respond(status, mapOf("error" to error, "details" to details))
            logger.error { "${e::class.simpleName}: $e" }
        }
    }

    post("/ingest"){
        val parameters = call.receive<ByteArray>()
        var decoded: Call?= null
        try{
            decoded = Json.decodeFromString<Call>(parameters.decodeToString())
            val transactionType=insertCallData(decoded)
            //if it was an insert send notification, if update do nothing
            if(transactionType=="Insert"){
                GlobalStore.pendingCalls.add(decoded)
            }
            //async send notification
            call.respond("{'Success': $transactionType}")
        }catch(e: SerializationException){
            logger.error { "Error parsing input data: $e" }
            call.respond(
                    HttpStatusCode(470, "Invalid Call Data"),
            mapOf(
                "error" to "Failed to parse request body into Call object",
                "details" to (e.message ?: "Unknown serialization error")
            )
            )

        } catch (e: SQLException) {
            // Database error → return 471
            call.respond(
                HttpStatusCode(471, "Database Insert Error"),
                mapOf(
                    "error" to "Failed to insert call data into database",
                    "details" to (e.message ?: "Unknown SQL error")
                )
            )
            logger.error { "Database/SQL Error $e" }
        } catch (e: Exception) {
            // Fallback → 500
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf(
                    "error" to "Unexpected server error",
                    "details" to (e.message ?: "No details available")
                )
            )
            logger.error {"Generic Unknown Error: $e"}
        }
    }
}