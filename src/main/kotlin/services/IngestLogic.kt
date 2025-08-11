package tech.parkhurst.services

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import tech.parkhurst.modal.Call
import tech.parkhurst.modal.tables.CallDataTable
import tech.parkhurst.modal.tables.toStrings


fun getCall(searchId: Int) : String {
    try {
        var ourCall: String="{}"

        transaction {
             CallDataTable.select(CallDataTable.id, CallDataTable.data)
                 .where{CallDataTable.id eq searchId}
                 .forEach{
                     ourCall=it[CallDataTable.data].toStrings()
                 }
        }
        return ourCall
    }catch(e: Exception){
        println("Error finding call: $e")
        return "{}"
    }
}
fun getAllCalls() : String {
    try {
        val callData:ArrayList<Call> = ArrayList<Call>()
        transaction {
            CallDataTable.select(CallDataTable.id, CallDataTable.data)
                .forEach{
                    callData.add(it[CallDataTable.data])
                }
        }
        return Json.encodeToString(callData)
    }catch(e: Exception){
        println("Error finding call: $e")
        return "[]"
    }
}

fun getRecentCalls(numOfCalls: Int) : String {
    try {
        val callData:ArrayList<Call> = ArrayList<Call>()
        transaction {
            CallDataTable.select(CallDataTable.id, CallDataTable.data)
                .orderBy(CallDataTable.id to SortOrder.DESC)
                .limit(numOfCalls)
                .forEach{
                    callData.add(it[CallDataTable.data])
                }
        }
        return Json.encodeToString(callData)
    }catch(e: Exception){
        println("Error finding call: $e")
        return "[]"
    }
}

/**
 * @param callData A Response call usually auto generated
 * @return 1 if success 0 if not
 */
fun insertCallData(callData: Call): Int {
    val generatedId = 0
    val callStatus = callData.incident.status
    val callId:Long = callData.callId
    return try {
        transaction {
            // Execute a simple query to check the connection
            val test=CallDataTable.insert {
                it[id]=callId.toInt()
                it[data]= callData
                it[status] = callStatus
            }
            test.insertedCount
        }
    } catch (e: Exception) {
        //Update table


        //or push  old row to audit table
        //then insert

        println("Error Insert: $e")
        generatedId
    }
}