plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    application
}

group = "tech.parkhurst"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("tech.parkhurst.MainKt")
}

dependencies {
    // Kotlin Standard Library
    implementation(kotlin("stdlib"))

    // Ktor (use the version you had in pom)
    val ktorVersion = "3.1.2"

    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")
    implementation("io.ktor:ktor-network-jvm:$ktorVersion")
    implementation("io.ktor:ktor-network-tls-jvm:$ktorVersion")
    implementation("io.ktor:ktor-network-tls-certificates-jvm:$ktorVersion")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")


    // JUnit 5
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // Optional: Netty
    implementation("io.netty:netty-all:4.2.0.Final")

    //Kotlin Random
    implementation("io.github.serpro69:kotlin-faker:2.0.0-rc.10")

    implementation("com.google.maps:google-maps-services:2.2.0")
    implementation("org.slf4j:slf4j-simple:1.7.25")


    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("org.postgresql:postgresql:42.7.2")
    //Connection Pooling
    implementation("com.zaxxer:HikariCP:5.1.0")

}

tasks.test {
    useJUnitPlatform()
}
