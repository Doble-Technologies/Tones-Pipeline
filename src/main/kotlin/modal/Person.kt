package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val personId: Long,
    val name: String,
    val age: Int,
    val gender: String,
    val statement: String,
    val conscious: String,
    val breathing: String,
    val callBackNumber: String
)
