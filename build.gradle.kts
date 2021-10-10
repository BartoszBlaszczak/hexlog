plugins {
    kotlin("jvm") version "1.5.31"
    id("io.gitlab.arturbosch.detekt").version("1.18.1")
    jacoco
}

group = "hex"
version ="1.2.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    val ktorVersion = "1.6.4"
    
    implementation("org.xerial:sqlite-jdbc:3.36.0.2")
    implementation("org.slf4j:slf4j-jdk14:1.7.32")
    implementation("com.github.BartoszBlaszczak:PropertiesLoader:1.0.4")
    implementation("com.rometools:rome:1.16.0")
    
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.6.3")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-java:$ktorVersion")
}

tasks.test {
    useJUnitPlatform {}
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jar {
    manifest { attributes["Main-Class"] = "ApplicationKt" }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.runtimeClasspath.get().map{ if (it.isDirectory) it else zipTree(it) })
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.9".toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestReport { dependsOn(tasks.test) }
