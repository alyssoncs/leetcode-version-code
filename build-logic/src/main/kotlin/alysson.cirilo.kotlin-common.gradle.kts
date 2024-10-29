import alysson.cirilo.versioncode.utils.getIntVersion
import alysson.cirilo.versioncode.utils.getLibrary
import alysson.cirilo.versioncode.utils.getVersion
import alysson.cirilo.versioncode.utils.libs
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

kotlin.jvmToolchain(libs.getIntVersion("java"))

open class FeaturesExtension(
    objects: ObjectFactory,
    private val enableTests: () -> Unit,
) {
    private val unitTestsProperty = objects.property<Boolean>().convention(false)

    var unitTests: Boolean
        get() = unitTestsProperty.get()
        set(value) {
            unitTestsProperty = value
            unitTestsProperty.disallowChanges()
            if (unitTestsProperty.get()) {
                enableTests()
            }
        }
}

extensions.create<FeaturesExtension>("features", objects, ::enableTests)

fun enableTests() {
    @Suppress("UnstableApiUsage")
    configure<TestingExtension> {
        suites {
            val test by getting(JvmTestSuite::class) {
                useJUnitJupiter(libs.getVersion("junit"))

                dependencies {
                    implementation(libs.getLibrary("test.kotest.assertions"))
                }

                targets.all {
                    testTask.configure {
                        testLogging {
                            exceptionFormat = TestExceptionFormat.FULL
                            events = setOf(
                                TestLogEvent.SKIPPED,
                                TestLogEvent.PASSED,
                                TestLogEvent.FAILED,
                                TestLogEvent.STANDARD_OUT,
                                TestLogEvent.STANDARD_ERROR,
                            )
                            showStandardStreams = true
                        }
                    }
                }
            }
        }
    }
}
