plugins {
    kotlin("jvm") version "1.5.10"
    jacoco
}

group = "hex"
version ="1.0.6"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    val ktorVersion = "1.6.0"
    
    //Force to use 1.5.0 version for transitive dependencies to avoid compilation warnings:
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.10")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.5.10")
    
    implementation("org.xerial", "sqlite-jdbc", "3.34.0")
    implementation("org.slf4j:slf4j-jdk14:1.7.30")
    implementation("com.github.BartoszBlaszczak:PropertiesLoader:1.0.4")
    
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-thymeleaf:$ktorVersion")
    implementation("org.thymeleaf.extras:thymeleaf-extras-java8time:3.0.4.RELEASE")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.6.0")
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


jacoco {
    println("Default jacoco version: $toolVersion")
    toolVersion = "0.8.7"
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestReport { dependsOn(tasks.test) }
