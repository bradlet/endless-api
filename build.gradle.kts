val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val kotestVersion: String by project

plugins {
    java
    application
    kotlin("jvm")
    id("io.ktor.plugin")
    id("com.google.cloud.tools.jib")
}

group = "com.endless"
version = "0.0.1"
val main by extra("io.ktor.server.netty.EngineMain")
application {
    mainClass.set(main)
    applicationDefaultJvmArgs = listOf(
        "-server",
        "-Djava.awt.headless=true",
        "-Xms128m",
        "-Xmx256m",
        "-XX:+UseG1GC",
        "-XX:MaxGCPauseMillis=100"
    )
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    // Logging Dependencies
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")

    // Test Dependencies
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    task("printServiceName") {
        print(rootProject.name)
    }
}

jib {
    to {
        image = "964799319978.dkr.ecr.us-west-2.amazonaws.com/bradlet"
        setCredHelper("ecr-login")
    }
    container {
        ports = listOf("8080")
        mainClass = main

        // good defauls intended for Java 8 (>= 8u191) containers
        jvmFlags = listOf(
            "-server",
            "-Djava.awt.headless=true",
            "-XX:InitialRAMFraction=2",
            "-XX:MinRAMFraction=2",
            "-XX:MaxRAMFraction=2",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100",
            "-XX:+UseStringDeduplication"
        )
    }
}
