package alysson.cirilo.versioncode

import alysson.cirilo.versioncode.VersionCode.Bits.Companion.bits
import alysson.cirilo.versioncode.VersionCode.ComponentSchema.Companion.takes

fun String.toVersionCode(): SemanticVersionCode {
    val (major, minor, patch) = this.split(".").map(String::toInt)
    return SemanticVersionCode(major, minor, patch)
}

fun SemanticVersionCode.bumpPatch(): SemanticVersionCode {
    return withPatch(patch.inc())
}

fun SemanticVersionCode.withPatch(patch: Int): SemanticVersionCode {
    return SemanticVersionCode(major = major, minor = minor, patch = patch)
}

fun SemanticVersionCode.bumpMinor(): SemanticVersionCode {
    return withMinor(minor.inc())
}

fun SemanticVersionCode.withMinor(minor: Int): SemanticVersionCode {
    return SemanticVersionCode(major = major, minor = minor, patch = patch)
}

fun SemanticVersionCode.bumpMajor(): SemanticVersionCode {
    return withMajor(major.inc())
}

fun SemanticVersionCode.withMajor(major: Int): SemanticVersionCode {
    return SemanticVersionCode(major = major, minor = minor, patch = patch)
}

class SemanticVersionCode(major: Int, minor: Int, patch: Int) : Comparable<SemanticVersionCode> {
    private val factory = VersionCode.Factory(
        MAJOR_NAME takes MAJOR_BITS,
        MINOR_NAME takes MINOR_BITS,
        PATCH_NAME takes PATCH_BITS,
    )
    private val versionCode = factory.create(major, minor, patch)

    override fun compareTo(other: SemanticVersionCode): Int = this.versionCode.compareTo(other.versionCode)

    override fun toString(): String = versionCode.toString()

    val value = versionCode.value
    val major = versionCode[MAJOR_NAME]!!
    val minor = versionCode[MINOR_NAME]!!
    val patch = versionCode[PATCH_NAME]!!

    companion object {
        private val MAJOR_BITS = 7.bits
        private val MINOR_BITS = 19.bits
        private val PATCH_BITS = 5.bits

        private const val MAJOR_NAME = "Major"
        private const val MINOR_NAME = "Minor"
        private const val PATCH_NAME = "Patch"
    }
}
