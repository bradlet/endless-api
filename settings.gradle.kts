rootProject.name = "endless-api"

pluginManagement {
    val ktorVersion: String by settings
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
        id("com.google.cloud.tools.jib") version "3.3.0"
    }
}