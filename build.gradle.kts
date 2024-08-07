plugins {
    kotlin("jvm") version "2.0.0"
    id("io.gitlab.arturbosch.detekt").version("1.23.6")
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
}

group = "hex"
version ="2.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.46.0.1")
    implementation("org.slf4j:slf4j-jdk14:2.0.13")
    implementation("com.rometools:rome:2.1.0")
    
    val ktorVersion = "2.3.12"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-http-redirect:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")

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

tasks.jar {
    manifest { attributes["Main-Class"] = "ApplicationKt" }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.runtimeClasspath.get().map{ if (it.isDirectory) it else zipTree(it) })
}
