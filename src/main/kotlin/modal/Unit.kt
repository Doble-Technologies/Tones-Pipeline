package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class Unit (
    val unitId: Long,
    val unit: String,
    val departmentId: Int,
    val department: String,
    val dispatched: String,
    val responding: String,
    val onScene: String,
    val transporting: String,
    val inService: String
)