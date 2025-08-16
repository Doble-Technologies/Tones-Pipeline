package tech.parkhurst.services

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsertReturning
import tech.parkhurst.modal.Call
import tech.parkhurst.modal.tables.CallDataTable
import tech.parkhurst.modal.tables.toStrings
import tech.parkhurst.services.helpers.jsonbArrayOverlap

private val logger = KotlinLogging.logger {}



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
        logger.error { "Error finding call: $e" }
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
        logger.error { "Error finding call: $e" }
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
        logger.error { "Error finding call: $e" }
        return "[]"
    }
}

fun getCallsParams(numOfCalls: Int, departments: List<Int> = emptyList(), status: String) : String {
    try {
        val callData:ArrayList<Call> = ArrayList<Call>()
        transaction {
            val query = CallDataTable.select(CallDataTable.id, CallDataTable.data)
                .apply {
                    if (status != "all") {
                        andWhere { CallDataTable.status eq status }
                    }
                    if (departments.isNotEmpty()) {
                        andWhere { jsonbArrayOverlap(CallDataTable.departments.name, departments) }
                    }
                }
                .orderBy(CallDataTable.id to SortOrder.DESC)
                .limit(numOfCalls)
            query.forEach {
                callData.add(it[CallDataTable.data])
            }
        }
        return Json.encodeToString(callData)
    }catch(e: Exception){
        logger.error { "Error finding call: $e" }
        return "[]"
    }
}

/**
 * @param callData A Response call usually auto generated
 * @return 1 if success 0 if not
 */
fun insertCallData(callData: Call): Int? {
    val generatedId = 0
    val callStatus = callData.incident.status
    val callId:Long = callData.callId
    val callDepartments: List<Int> = buildSet {
        add(callData.incident.serviceID)
        addAll(callData.response.units.map { it.departmentId })
        add(callData.response.serviceID)
    }.toList()
    return try {
        transaction {
            // Execute a simple query to check the connection
            val test=CallDataTable.upsertReturning() {
                it[id]=callId.toInt()
                it[data]= callData
                it[status] = callStatus
                it[departments] = callDepartments
            }.singleOrNull()
            test?.get(CallDataTable.id)
        }
    } catch (e: Exception) {
        logger.error { "Error on DB Insert: $e" }
        generatedId
    }
}