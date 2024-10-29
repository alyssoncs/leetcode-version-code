plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.dependencies.sorter.gradle.plugin)
    implementation(libs.dependency.analysis.gradle.plugin)
}
