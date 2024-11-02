package alysson.cirilo.versioncode

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
        @IntRangeSource(start = 0, end = 31)
        fun `pass on major between valid range`(major: Int) {
            shouldNotThrowAny {
                version.withMajor(major)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = -100, end = -1)
        fun `fail on major below valid range`(major: Int) {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Major should not be negative, but is $major",
            ) {
                version.withMajor(major)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = 32, end = 64)
        fun `fail on major above valid range`(major: Int) {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Major should be no more than 31 (2^5-1), but is $major",
            ) {
                version.withMajor(major)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = 0, end = 100)
        @IntRangeSource(start = 524_200, end = 524_287)
        fun `pass on minor between valid range`(minor: Int) {
            shouldNotThrowAny {
                version.withMinor(minor)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = -100, end = -1)
        fun `fail on minor below valid range`(minor: Int) {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Minor should not be negative, but is $minor",
            ) {
                version.withMinor(minor)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = 524_288, end = 524_400)
        fun `fail on minor above valid range`(minor: Int) {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Minor should be no more than 524287 (2^19-1), but is $minor",
            ) {
                version.withMinor(minor)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = 0, end = 127)
        fun `pass on patch between valid range`(patch: Int) {
            shouldNotThrowAny {
                version.withPatch(patch)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = -100, end = -1)
        fun `fail on patch below valid range`(patch: Int) {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Patch should not be negative, but is $patch",
            ) {
                version.withPatch(patch)
            }
        }

        @ParameterizedTest
        @IntRangeSource(start = 128, end = 200)
        fun `fail on patch above valid range`(patch: Int) {
            shouldThrowWithMessage<IllegalArgumentException>(
                "Patch should be no more than 127 (2^7-1), but is $patch",
            ) {
                version.withPatch(patch)
            }
        }
    }
}
