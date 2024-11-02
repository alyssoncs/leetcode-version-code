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

    val value: Int = (major shl MAJOR_SHIFT) or (minor shl MINOR_SHIFT) or (patch shl PATCH_SHIFT)

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
        private const val PATCH_BITS = 5
        private const val MINOR_BITS = 19
        private const val MAJOR_BITS = 7

        private const val PATCH_SHIFT = 0
        private const val MINOR_SHIFT = PATCH_SHIFT + PATCH_BITS
        private const val MAJOR_SHIFT = MINOR_SHIFT + MINOR_BITS
    }
}
