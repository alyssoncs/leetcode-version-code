package alysson.cirilo.versioncode

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest

class VersionCodeTest {
    private val version = "1.2.3".toVersionCode()

    @Nested
    inner class Retrieval {
        @Test
        fun `encodes major`() {
            version.major shouldBe 1
        }

        @Test
        fun `encodes minor`() {
            version.minor shouldBe 2
        }

        @Test
        fun `encodes patch`() {
            version.patch shouldBe 3
        }
    }

    @Nested
    inner class Comparison {

        @Nested
        inner class SegmentBump {
            @Test
            fun `generates greater value for greater patch`() {
                version.withPatch(version.patch.inc()) shouldBeGreaterThan version
            }

            @Test
            fun `generates greater value for greater minor`() {
                version.withMinor(version.minor.inc()) shouldBeGreaterThan version
            }

            @Test
            fun `generates greater value for greater major`() {
                version.withMajor(version.major.inc()) shouldBeGreaterThan version
            }
        }

        @Nested
        inner class RelativeSignificance {
            @Test
            fun `minor trumps over patch`() {
                version.withMinor(3) shouldBeGreaterThan version.withPatch(20)
            }

            @Test
            fun `major trumps over patch`() {
                version.withMajor(2) shouldBeGreaterThan version.withPatch(20)
            }

            @Test
            fun `major trumps over minor`() {
                version.withMajor(2) shouldBeGreaterThan version.withMinor(20)
            }
        }
    }

    @Nested
    inner class SegmentRange {
        @ParameterizedTest
        @IntRangeSource(start = -100, end = -1)
        @IntRangeSource(start = 32, end = 64)
        fun `fail on out of range major`(major: Int) {
            assertThrows<IllegalArgumentException> {
                version.withMajor(major)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = 0, end = 31)
        fun `pass on correct range major`(major: Int) {
            assertDoesNotThrow {
                version.withMajor(major)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = -100, end = -1)
        @IntRangeSource(start = 524_288, end = 524_400)
        fun `fail on out of range minor`(minor: Int) {
            assertThrows<IllegalArgumentException> {
                version.withMinor(minor)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = 0, end = 100)
        @IntRangeSource(start = 524_200, end = 524_287)
        fun `pass on correct range minor`(minor: Int) {
            assertDoesNotThrow {
                version.withMinor(minor)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = -100, end = -1)
        @IntRangeSource(start = 128, end = 200)
        fun `fail on out of range patch`(patch: Int) {
            assertThrows<IllegalArgumentException> {
                version.withPatch(patch)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = 0, end = 127)
        fun `pass on correct range patch`(patch: Int) {
            assertDoesNotThrow {
                version.withPatch(patch)
            }
        }
    }
}
