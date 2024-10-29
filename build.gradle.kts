plugins {
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.kotlin.jvm) apply false
}

dependencyAnalysis {
    structure {
        bundle("junit") {
            includeGroup("org.junit.jupiter")
        }

        bundle("kotest-assertions") {
            include("io.kotest:kotest-assertions.*")
        }
    }
}
