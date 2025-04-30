package tech.parkhurst.modal


data class Event(
    val eventID: Long,
    val incident: Incident,
    val address: Address,
    val person: Person,
    val response: Response)
