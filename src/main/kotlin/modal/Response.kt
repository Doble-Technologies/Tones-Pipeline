package tech.parkhurst.modal

data class Response(
    val intID: Int,
    val responseID: Int,
    val serviceID: Int,
    val serviceName: String,
    val units: Array<Unit>
)
