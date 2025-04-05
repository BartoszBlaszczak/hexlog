plugins {
    application
    kotlin("jvm") version "2.1.20"
    id("io.gitlab.arturbosch.detekt").version("1.23.8")
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

group = "hex"
version ="4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
    implementation("org.slf4j:slf4j-jdk14:2.0.17")
    implementation("com.rometools:rome:2.1.0")
    
    val ktorVersion = "3.1.2"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-http-redirect:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-network-tls-certificates:$ktorVersion")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-java:$ktorVersion")
}

tasks.test {
    useJUnitPlatform {}
}

kover {
    reports {
        verify {
            rule {
                description = "Minimal line coverage rate in percent"
                bound { minValue = 90 }
            }
        }
    }
}
