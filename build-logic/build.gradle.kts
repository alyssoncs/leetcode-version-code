plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.dependencies.sorter.gradle.plugin)
    implementation(libs.dependency.analysis.gradle.plugin)
}

kotlin.jvmToolchain(libs.versions.java.get().toInt())