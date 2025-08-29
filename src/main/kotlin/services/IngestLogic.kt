package tech.parkhurst.services

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsertReturning
import tech.parkhurst.modal.Call
import tech.parkhurst.modal.CreateUserParams
import tech.parkhurst.modal.GetCallsParams
import tech.parkhurst.modal.tables.CallDataTable
import tech.parkhurst.modal.tables.UserDataTable
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
                .orderBy(CallDataTable.createdAt to SortOrder.DESC)
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

fun getCallsParams(callParams: GetCallsParams) : String {
    val (numOfCalls, departments, status) = callParams
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
                .orderBy(CallDataTable.createdAt to SortOrder.DESC)
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

fun createUser(userParams: CreateUserParams): Int = transaction {
    UserDataTable.insert { row ->
        row[UserDataTable.firstName] = userParams.firstName
        row[UserDataTable.lastName] = userParams.lastName
        row[UserDataTable.number] = userParams.number
        row[UserDataTable.email] = userParams.email
        row[UserDataTable.provider] = userParams.provider
        row[UserDataTable.departments] = userParams.departments
        row[UserDataTable.globalRole] = userParams.globalRole
        row[UserDataTable.primaryDept] = userParams.primaryDept
        row[UserDataTable.token] = userParams.token
        row[UserDataTable.firebaseUid] = userParams.firebaseUid
    } get UserDataTable.id
}

/**
 * @param callData A Response call usually auto generated
 * @return 1 if success 0 if not
 */
fun insertCallData(callData: Call): String? {
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
            val result = CallDataTable
                .upsertReturning(
                    keys = arrayOf(CallDataTable.id), // conflict target = PRIMARY KEY
                    returning = listOf(
                        CallDataTable.id,
                        CallDataTable.data,
                        CallDataTable.status,
                        CallDataTable.departments,
                        CallDataTable.createdAt,
                        CallDataTable.updatedAt
                    )
                ) {
                    it[id] = callId.toInt()
                    it[data] = callData
                    it[status] = callStatus
                    it[departments] = callDepartments
                }
                .singleOrNull()
            if (result != null) {
                val updatedAt = result[CallDataTable.updatedAt]
                val createdAt = result[CallDataTable.createdAt]
                if(updatedAt==createdAt){
                    return@transaction "Insert"
                }else{
                    return@transaction "Update"
                }
            }
            ""
        }
    } catch (e: Exception) {
        logger.error { "Error on DB Insert: $e" }
        "Error"
    }
}