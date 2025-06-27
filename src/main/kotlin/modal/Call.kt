package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class Call(
    val callId: Long,
    val incident: Incident,
    val address: Address?,
    val person: Person,
    val response: Response)
