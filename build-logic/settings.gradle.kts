pluginManagement {
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
        gradlePluginPortal()
        // for detekt snapshot
        google()
        mavenCentral()
        maven {
            url = uri("https://example.com/")
        }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
