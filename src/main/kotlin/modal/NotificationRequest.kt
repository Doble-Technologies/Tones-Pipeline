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
    var title: String,
    var body: String,
    var sound: String = "default"
)

@Serializable
data class FcmMessage(
    var topic: String? = null,
    var notification: FcmNotification,
    var data: Map<String, String> = emptyMap()
)

@Serializable
data class FcmRequest(
    var message: FcmMessage
)