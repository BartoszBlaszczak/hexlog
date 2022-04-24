plugins {
    kotlin("jvm") version "1.6.21"
    id("io.gitlab.arturbosch.detekt").version("1.20.0")
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

group = "hex"
version ="1.4.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.slf4j:slf4j-jdk14:1.7.36")
    implementation("com.github.BartoszBlaszczak:PropertiesLoader:1.0.4")
    implementation("com.rometools:rome:1.18.0")
    
    val ktorVersion = "2.0.0"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-http-redirect:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.2.2")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-java:$ktorVersion")
}

tasks.test {
    useJUnitPlatform {}
}

tasks.koverVerify {
    rule {
        name = "Minimal line coverage rate in percent"
        bound { minValue = 90 }
    }
}

tasks.jar {
    manifest { attributes["Main-Class"] = "ApplicationKt" }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.runtimeClasspath.get().map{ if (it.isDirectory) it else zipTree(it) })
}
