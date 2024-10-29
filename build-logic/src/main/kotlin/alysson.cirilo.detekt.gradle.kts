import alysson.cirilo.versioncode.utils.libs
import alysson.cirilo.versioncode.utils.getLibrary

plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    parallel = true
    buildUponDefaultConfig = true
    config.setFrom("${rootDir}/config/detekt/detekt.yml")
}

dependencies {
    detektPlugins(project.libs.getLibrary("detekt.formatting"))
}
