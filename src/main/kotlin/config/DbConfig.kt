package tech.parkhurst.config

import org.jetbrains.exposed.sql.Database



fun connectToDatabase() {
    val dbUser: String = System.getenv("dbUser") ?: ""
    val dbPass: String = System.getenv("dbPass") ?: ""
    val dbUrl: String = System.getenv("dbUrl")
    val dbPort: String = System.getenv("dbPort") ?: ""
    Database.connect(
        url = "jdbc:postgresql://$dbUrl:$dbPort",
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPass
    )
}
