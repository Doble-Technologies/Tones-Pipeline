package tech.parkhurst.config

import kotlinx.serialization.Serializable


@Serializable
data class UserSession(val id: String, val count: Int)