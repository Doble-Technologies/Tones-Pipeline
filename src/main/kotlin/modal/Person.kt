package tech.parkhurst.modal

//    "Person": {
//        "Name": "John Doe",
//        "Age": 19,
//        "Gender": "Female",
//        "Statement": "BLOOD PRESSURE 56/41 - IN AND OUT OF CON",
//        "Conscious": "No",
//        "Breathing": "Yes",
//        "CallBackNumber": "(223) 456-7890"
//    },
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
