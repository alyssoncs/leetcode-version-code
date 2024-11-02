package alysson.cirilo.versioncode

import kotlin.math.pow

class VersionCode(val major: Int, val minor: Int, val patch: Int) : Comparable<VersionCode> {
    init {
        VersionComponent.entries.forEach(::checkVersionComponent)
    }

    private fun checkVersionComponent(component: VersionComponent) {
        val version = component.from(this)
        require(component.isValid(version)) {
            val violation = if (version < 0)
                "not be negative"
            else
                "be no more than ${component.maxValue} (2^${component.bits}-1)"

            "${component.displayName} should $violation, but is $version"
        }
    }

    val value: Int = major * MAJOR_WEIGHT + minor * MINOR_WEIGHT + patch * PATCH_WEIGHT

    override fun compareTo(other: VersionCode): Int {
        return this.value - other.value
    }

    private enum class VersionComponent(
        val bits: Int,
        val displayName: String,
    ) {
        MAJOR(MAJOR_BITS, "Major") {
            override fun from(version: VersionCode): Int = version.major
        },
        MINOR(MINOR_BITS, "Minor") {
            override fun from(version: VersionCode): Int = version.minor
        },
        PATCH(PATCH_BITS, "Patch") {
            override fun from(version: VersionCode): Int = version.patch
        },
        ;

        abstract fun from(version: VersionCode): Int

        val maxValue = (2 toThe bits) - 1

        fun isValid(version: Int) = version in 0..maxValue

        private infix fun Int.toThe(exponent: Int): Int {
            return toDouble().pow(exponent).toInt()
        }
    }

    companion object {
        private const val MAJOR_BITS = 7
        private const val MAJOR_WEIGHT = 10_000

        private const val MINOR_BITS = 19
        private const val MINOR_WEIGHT = 100

        private const val PATCH_BITS = 5
        private const val PATCH_WEIGHT = 1
    }
}
