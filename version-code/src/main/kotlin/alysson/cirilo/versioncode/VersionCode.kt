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

    private val majorBits = Array(MAJOR_BITS) { idx ->
        major.getComponentBit(MAJOR_BITS, idx)
    }

    private val minorBits = Array(MINOR_BITS) { idx ->
        minor.getComponentBit(MINOR_BITS, idx)
    }

    private val patchBits = Array(PATCH_BITS) { idx ->
        patch.getComponentBit(PATCH_BITS, idx)
    }

    private val bits = majorBits + minorBits + patchBits

    private fun Int.getComponentBit(componentSize: Int, idx: Int): Boolean {
        return this[Int.SIZE_BITS - (componentSize - idx)]
    }

    private operator fun Int.get(index: Int): Boolean {
        val bit = this shr (Int.SIZE_BITS - index - 1) and 1
        return bit == 1
    }

    private fun Array<Boolean>.toInt(): Int {
        return this
            .map { bool -> if (bool) 1 else 0 }
            .foldIndexed(0) { idx, acc, bit ->
                acc + bit * (2 toThe (lastIndex - idx))
            }
    }

    val value: Int = bits.toInt()

    override fun compareTo(other: VersionCode): Int {
        return this.value - other.value
    }

    override fun toString(): String {
        return "$value ($major.$minor.$patch)"
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
    }

    companion object {
        private const val PATCH_BITS = 5
        private const val MINOR_BITS = 19
        private const val MAJOR_BITS = 7
    }
}

private infix fun Int.toThe(exponent: Int): Int {
    return toDouble().pow(exponent).toInt()
}
