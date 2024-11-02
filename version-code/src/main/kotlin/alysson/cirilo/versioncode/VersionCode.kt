package alysson.cirilo.versioncode

import kotlin.math.pow

class VersionCode(val major: Int, val minor: Int, val patch: Int) : Comparable<VersionCode> {
    init {
        require(major in 0..<(2 toThe MAJOR_BITS)) {
            if (major < 0)
                "Major should not be negative, but is $major"
            else
                "Major should be no more than ${(2 toThe MAJOR_BITS) - 1} (2^$MAJOR_BITS-1), but is $major"
        }
        require(minor in 0..<(2 toThe MINOR_BITS)) {
            if (minor < 0)
                "Minor should not be negative, but is $minor"
            else
                "Minor should be no more than ${(2 toThe MINOR_BITS) - 1} (2^$MINOR_BITS-1), but is $minor"
        }
        require(patch in 0..<(2 toThe PATCH_BITS)) {
            if (patch < 0)
                "Patch should not be negative, but is $patch"
            else
                "Patch should be no more than ${(2 toThe PATCH_BITS) - 1} (2^$PATCH_BITS-1), but is $patch"
        }
    }

    val value: Int = major * MAJOR_WEIGHT + minor * MINOR_WEIGHT + patch * PATCH_WEIGHT

    override fun compareTo(other: VersionCode): Int {
        return this.value - other.value
    }

    private infix fun Int.toThe(exponent: Int): Int {
        return toDouble().pow(exponent).toInt()
    }

    companion object {
        private const val MAJOR_BITS = 5
        private const val MAJOR_WEIGHT = 10_000

        private const val MINOR_BITS = 19
        private const val MINOR_WEIGHT = 100

        private const val PATCH_BITS = 7
        private const val PATCH_WEIGHT = 1
    }
}
