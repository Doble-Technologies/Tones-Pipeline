package tech.parkhurst.modal

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    var intID: Int=0,
    var responseID: Int=0,
    var serviceID: Int=0,
    var serviceName: String="",
//    var units: Array<Unit> = arrayOf<Unit>()
){
}
