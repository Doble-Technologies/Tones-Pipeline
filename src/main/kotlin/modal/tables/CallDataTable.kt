package tech.parkhurst.modal.tables

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.jsonb
import tech.parkhurst.modal.Call


//Serialization Functions
fun String.toCall(): Call = Json.decodeFromString(this)

fun Call.toStrings(): String = Json.encodeToString(this)

object CallDataTable : Table("call_data") {
    val id = integer("id").autoIncrement()
    val data = jsonb("data", Call::toStrings, String::toCall )
    override val primaryKey = PrimaryKey(id, name = "id")
}