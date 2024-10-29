pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        // for detekt snapshot
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // for detekt snapshot
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "leetcode-version-code"
include("version-code")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
