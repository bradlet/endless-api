val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val kotestVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin")
    id("com.google.cloud.tools.jib")
}

group = "com.endless"
version = "0.0.1"
application {
    mainClass.set("$group.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    // Logging Dependencies
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.1.2")

    // Test Dependencies
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
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
    from {
        image = "public.ecr.aws/micahhausler/alpine:3.16.0"
        setCredHelper("ecr-login")
    }
    to {
        image = "964799319978.dkr.ecr.us-west-2.amazonaws.com/bradlet"
        setCredHelper("ecr-login")
    }
}
