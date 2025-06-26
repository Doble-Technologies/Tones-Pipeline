package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class Incident(
    val incID: Int,
    val incNumber: Int,
    val jurisdictionNumber: Int=-1,
    val serviceNumber: Int=0,
    val serviceID: Int=0,
    val incDate: String="",
    val incNature: String="",
    val incNatureCode: String="",
    val incNatureCodeDesc: String="",
    val notes: String="",
    val status: String="",
    val origin: String=""
)
