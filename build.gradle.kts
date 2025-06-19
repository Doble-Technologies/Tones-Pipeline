plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
//    id("dev.adamko.kx12q.kotlin-randomizer") version "0.4.0"
    id("com.x12q.kotlin.randomizer") version "1.0.0-alpha.16-2.1.0"
    // for other kotlin version:



    application
}

group = "tech.parkhurst"
version = "0.0.1"

kotlinRandomizer{
    enable = true
}
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

    // Kotlin Randomizer
    implementation("com.x12q:kotlin-randomizer-lib:1.0.0-alpha.16-2.1.0")
    //Kotlin Random
    implementation("io.github.serpro69:kotlin-faker:2.0.0-rc.10")
    implementation("io.github.serpro69:kotlin-faker-sports:2.0.0-rc.10")


    // JUnit 5
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // Optional: Netty
    implementation("io.netty:netty-all:4.2.0.Final")



    //Tem
    implementation("com.mitteloupe.randomgenkt:randomgenkt:2.0.1")
    implementation("com.mitteloupe.randomgenkt:randomgenkt.datasource:2.0.1")
}

tasks.test {
    useJUnitPlatform()
}
