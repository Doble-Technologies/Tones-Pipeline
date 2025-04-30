package tech.parkhurst.modal

data class Incident(
    val incID: Int,
    val incNumber: Int,
    val jurisdictionNumber: Int,
    val serviceNumber: Int,
    val serviceID: Int,
    val incDate: String,
    val incNature: String,
    val incNatureCode: String,
    val incNatureCodeDesc: String,
    val notes: String,
    val status: String,
    val origin: String
)
