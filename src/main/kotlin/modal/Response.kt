package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    var incID: Int=0,
    var responseID: Int=0,
    var serviceID: Int=0,
    var serviceName: String="",
    var units: ArrayList<Unit> = ArrayList<Unit>(),
){
}
