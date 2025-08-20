package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class NotificationRequest(
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap(),
    val topics: List<String> = emptyList()
)

@Serializable
data class FcmNotification(
    val title: String,
    val body: String,
    val sound: String = "default"
)

@Serializable
data class FcmMessage(
    val topic: String? = null,
    val notification: FcmNotification,
    val data: Map<String, String> = emptyMap()
)

@Serializable
data class FcmRequest(
    val message: FcmMessage
)