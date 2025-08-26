package tech.parkhurst.modal.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.jsonb

object UserDataTable : Table("users") {
    val id = integer("user_id").autoIncrement()
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val number = varchar("p_number", 50)
    val email = varchar("email", 255)
    val provider = varchar("service_provider", 50)
    val departments = jsonb("departments", List<Int>::toJson, String::toIntList)
    val globalRole = varchar("global_role", 50)
    val primaryDept = integer("primary_dept").nullable()
    val token = varchar("token", 255).nullable()
    val firebaseUid = varchar("firebase_uid", 255).nullable()
    override val primaryKey = PrimaryKey(id)
}