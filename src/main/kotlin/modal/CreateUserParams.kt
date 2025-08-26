package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserParams(
    val firstName: String,
    val lastName: String,
    val number: String,
    val email: String,
    val provider: String,
    val departments: List<Int>,
    val globalRole: String,
    val primaryDept: Int?,
    val token: String?,
    val firebaseUid: String?
)