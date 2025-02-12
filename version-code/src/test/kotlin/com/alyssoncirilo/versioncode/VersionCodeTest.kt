package com.alyssoncirilo.versioncode

import com.alyssoncirilo.versioncode.VersionCode.Bits.Companion.bit
import com.alyssoncirilo.versioncode.VersionCode.Bits.Companion.bits
import com.alyssoncirilo.versioncode.VersionCode.ComponentSchema.Companion.takes
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class VersionCodeTest {
    @Nested
    inner class SchemaValidations {
        @Test
        fun `fail on empty schema`() {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Schema should not be empty",
            ) {
                val empty = emptyArray<VersionCode.ComponentSchema>()
                VersionCode.Factory(*empty)
            }
        }

        @IntRangeSource(start = 32, end = 40)
        @ParameterizedTest
        fun `fail on component size too large`(size: Int) {
            shouldThrowWithMessage<IllegalArgumentException>(
                "All components combined should not take more than 31 bits, but total is $size",
            ) {
                VersionCode.Factory("Major" takes size.bits)
            }

            shouldThrowWithMessage<IllegalArgumentException>(
                "All components combined should not take more than 31 bits, but total is ${size + 2}",
            ) {
                VersionCode.Factory(
                    "Major" takes 2.bits,
                    "Minor" takes size.bits,
                )
            }
        }

        @IntRangeSource(start = -10, end = -1)
        @ParameterizedTest
        fun `fail on negative component size`(size: Int) {
            shouldThrowWithMessage<IllegalArgumentException>(
                "No component should have negative size, but Minor is $size",
            ) {
                VersionCode.Factory(
                    "Major" takes 2.bits,
                    "Minor" takes size.bits,
                )
            }

            shouldThrowWithMessage<IllegalArgumentException>(
                "No component should have negative size, but Patch is $size",
            ) {
                VersionCode.Factory(
                    "Major" takes 2.bits,
                    "Minor" takes 3.bits,
                    "Patch" takes size.bits,
                )
            }
        }

        @Test
        fun `fail on zero component size`() {
            shouldThrowWithMessage<IllegalArgumentException>(
                "All components should have positive sizes, but Patch is zero",
            ) {
                VersionCode.Factory(
                    "Major" takes 2.bits,
                    "Minor" takes 2.bits,
                    "Patch" takes 0.bits,
                )
            }

            shouldThrowWithMessage<IllegalArgumentException>(
                "All components should have positive sizes, but Major is zero",
            ) {
                VersionCode.Factory(
                    "Major" takes 0.bits,
                    "Minor" takes 2.bits,
                    "Patch" takes 4.bits,
                )
            }
        }

        @ValueSource(
            strings = [
                "10, 10, 10, 2",
                "31, 1",
                "30, 1, 1",
                "30, 40",
            ],
        )
        @ParameterizedTest
        fun `fail on components size sum too large`(csv: String) {
            val componentSizes = csv.split(",").map(String::trim).map(String::toInt)

            shouldThrowWithMessage<IllegalArgumentException>(
                "All components combined should not take more than 31 bits, but total is ${componentSizes.sum()}",
            ) {
                val schema = componentSizes.mapIndexed { idx, value ->
                    idx.toString() takes value.bits
                }.toTypedArray()

                VersionCode.Factory(*schema)
            }
        }

        @Test
        fun `fail on repeated names`() {
            shouldThrowWithMessage<IllegalArgumentException>(
                "No component should have duplicate names",
            ) {
                VersionCode.Factory(
                    "Major" takes 2.bits,
                    "Major" takes 4.bits,
                )
            }
        }
    }

    @Nested
    inner class ComponentsValidations {
        private val factory = VersionCode.Factory(
            "Component0" takes 4.bits,
            "Component1" takes 4.bits,
            "Component2" takes 4.bits,
            "Component3" takes 4.bits,
            "Component4" takes 4.bits,
            "Component5" takes 4.bits,
            "Component6" takes 4.bits,
            "Component7" takes 3.bits,
        )

        @Test
        fun `fail on missing components`() {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Missing value for: Component7",
            ) {
                factory.create(0, 1, 2, 3, 4, 5, 6)
            }

            shouldThrowWithMessage<IllegalArgumentException>(
                "Missing value for: Component6 and Component7",
            ) {
                factory.create(0, 1, 2, 3, 4, 5)
            }

            shouldThrowWithMessage<IllegalArgumentException>(
                "Missing value for: Component5, Component6 and Component7",
            ) {
                factory.create(0, 1, 2, 3, 4)
            }
        }

        @Test
        fun `fail on too many components`() {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Expected 8 components, but got 9",
            ) {
                factory.create(0, 1, 2, 3, 4, 5, 6, 7, 8)
            }

            shouldThrowWithMessage<IllegalArgumentException>(
                "Expected 8 components, but got 10",
            ) {
                factory.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            }
        }
    }

    @Nested
    inner class Syntax {
        @Test
        fun `should accept components with no name`() {
            shouldNotThrowAny {
                VersionCode.Factory(
                    30.bits,
                    1.bits,
                )
            }
        }

        @Test
        fun `should name unnamed components`() {
            val version = VersionCode.Factory(
                9.bits,
                9.bits,
                9.bits,
                4.bits,
            ).create(1, 2, 3, 4)

            version["Component 0"] shouldBe 1
            version["Component 1"] shouldBe 2
            version["Component 2"] shouldBe 3
            version["Component 3"] shouldBe 4
        }
    }

    @Nested
    inner class DifferentSchemaComparison {
        @Nested
        inner class SameSchemaSize {
            @ParameterizedTest
            @ValueSource(strings = ["5.2", "6.0", "2.5"])
            fun `should compare equals if the version reads the same`(versionStr: String) {
                val firstFactory = VersionCode.Factory(3.bits, 3.bits)
                val secondFactory = VersionCode.Factory(4.bit, 4.bits)

                val firstVersion = versionStr.toVersionCode(firstFactory)
                val secondVersion = versionStr.toVersionCode(secondFactory)

                firstVersion.value shouldNotBe secondVersion.value
                comparisonChecks(firstVersion, "==", secondVersion)
            }

            @ParameterizedTest
            @CsvSource(
                "1.2.3, <, 2.2.3",
                "1.2.3, >, 1.1.3",
                "1.2.3, <, 1.2.4",
                "2.2.3, >, 2.2.2",
            )
            fun `should not compare equals if the version does not read the same`(
                firstVersionStr: String,
                relationship: String,
                secondVersionStr: String,
            ) {
                val firstFactory = VersionCode.Factory(5.bits, 5.bits, 5.bits)
                val secondFactory = VersionCode.Factory(4.bit, 6.bits, 7.bits)

                val firstVersion = firstVersionStr.toVersionCode(firstFactory)
                val secondVersion = secondVersionStr.toVersionCode(secondFactory)

                comparisonChecks(firstVersion, relationship, secondVersion)
            }
        }

        @Nested
        inner class DifferentSchemaSize {
            @Test
            fun `should compare different even with same internal representation`() {
                val singleComponentFactory = VersionCode.Factory(31.bits)
                val doubleComponentFactory = VersionCode.Factory(1.bit, 30.bits)

                val singleComponentVersion = singleComponentFactory.create(5)
                val doubleComponentVersion = doubleComponentFactory.create(0, 5)

                singleComponentVersion.value shouldBe doubleComponentVersion.value shouldBe 5
                comparisonChecks(singleComponentVersion, "!=", doubleComponentVersion)
            }

            @ParameterizedTest
            @CsvSource(
                "5, 5.0",
                "1.2, 1.2.0",
                "1.2, 1.2.0.0.0",
                "0.1.2, 0.1.2.0.0.0",
            )
            fun `should compare equals if versions are the same when zero-padded`(
                firstVersionStr: String,
                secondVersionStr: String,
            ) {
                fun String.factory(): VersionCode.Factory {
                    val componentSize = count { it == '.' }.inc()
                    val schema = Array(componentSize) {
                        (31 / componentSize).bits
                    }
                    return VersionCode.Factory(*schema)
                }

                val firstVersion = firstVersionStr.toVersionCode(firstVersionStr.factory())
                val secondVersion = secondVersionStr.toVersionCode(secondVersionStr.factory())

                comparisonChecks(firstVersion, "==", secondVersion)
            }
        }

        private fun String.toVersionCode(factory: VersionCode.Factory): VersionCode {
            val components = this.split(".")
                .map { it.toInt() }
                .toIntArray()

            return factory.create(*components)
        }
    }
}
