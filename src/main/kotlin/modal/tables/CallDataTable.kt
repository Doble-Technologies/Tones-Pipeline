package tech.parkhurst.modal.tables

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.json.jsonb
import tech.parkhurst.modal.Call


//Serialization Functions
fun String.toCall(): Call = Json.decodeFromString(this)

fun Call.toStrings(): String = Json.encodeToString(this)

fun List<Int>.toJson(): String = Json.encodeToString(this)

fun String.toIntList(): List<Int> = Json.decodeFromString(this)

object CallDataTable : Table("call_data") {
    var id = integer("id")
    val data = jsonb("data", Call::toStrings, String::toCall )
    val status =varchar("call_status",255)
    val departments = jsonb("departments", List<Int>::toJson, String::toIntList )
    val updatedAt = timestamp("updated_at")
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id, name = "id")
}