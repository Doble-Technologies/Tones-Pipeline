package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val addressId: String,
    val streetAddress: String,
    val addressApartment: String,
    val town: String,
    val state: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double,
    val county: String,
    val intersection1: String="",
    val intersection2: String="",
    val locationName: String="",
    val weatherCondition: String=""
)
