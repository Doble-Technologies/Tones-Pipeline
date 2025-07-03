package tech.parkhurst.config

import org.jetbrains.exposed.v1.jdbc.Database



fun connectToDatabase() {
    val dbUser: String = System.getenv("dbUser") ?: ""
    val dbPass: String = System.getenv("dbPass") ?: ""
    val dbUrl: String = System.getenv("dbUrl")
    Database.connect(
        url = "jdbc:postgresql://$dbUrl",
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPass

    )
}
