package tech.parkhurst.routes

import io.ktor.http.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import tech.parkhurst.modal.Call
import tech.parkhurst.modal.GetCallsParams
import tech.parkhurst.modal.tables.toStrings
import tech.parkhurst.services.*

val gen= GeneratorLogic()



//Todo define error objects for better error handling messaging
fun Route.ingestRoutes(){
    get("/generateCall"){
        var generatedCall: Call?= null
        try{
            generatedCall = gen.generateCall()
        }catch (e: Exception){
            call.respondText("Error Generating")
        }
        try{
            if (generatedCall != null) {
                insertCallData(generatedCall)
                call.respondText(generatedCall.toStrings())
            }
        }catch (e: Exception){
            call.respondText("Error Inserting")
        }
    }

    get("/testendpoint"){
        call.respondText("{'version': 1.0.3}")
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
        call.respond(getCallsParams(decoded.numCalls, decoded.departments, decoded.status))
    }
}