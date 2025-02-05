package com.alyssoncirilo.versioncode

import com.alyssoncirilo.versioncode.VersionCode.Bits.Companion.bits
import com.alyssoncirilo.versioncode.VersionCode.ComponentSchema.Companion.takes

fun String.toSemanticVersion(): SemanticVersion {
    val (major, minor, patch) = this.split(".").map(String::toInt)
    return SemanticVersion(major, minor, patch)
}

class SemanticVersion(major: Int, minor: Int, patch: Int) : Comparable<SemanticVersion> {
    private val factory = VersionCode.Factory(
        MAJOR_NAME takes MAJOR_BITS,
        MINOR_NAME takes MINOR_BITS,
        PATCH_NAME takes PATCH_BITS,
    )
    private val versionCode = factory.create(major, minor, patch)

    override fun compareTo(other: SemanticVersion): Int = this.versionCode.compareTo(other.versionCode)

    override fun toString(): String = versionCode.toString()

    val value = versionCode.value
    val major = versionCode[MAJOR_NAME]!!
    val minor = versionCode[MINOR_NAME]!!
    val patch = versionCode[PATCH_NAME]!!

    fun bumpPatch(): SemanticVersion {
        return withPatch(patch.inc())
    }

    fun withPatch(patch: Int): SemanticVersion {
        return SemanticVersion(major = major, minor = minor, patch = patch)
    }

    fun bumpMinor(): SemanticVersion {
        return withMinor(minor.inc())
    }

    fun withMinor(minor: Int): SemanticVersion {
        return SemanticVersion(major = major, minor = minor, patch = patch)
    }

    fun bumpMajor(): SemanticVersion {
        return withMajor(major.inc())
    }

    fun withMajor(major: Int): SemanticVersion {
        return SemanticVersion(major = major, minor = minor, patch = patch)
    }

    companion object {
        private val MAJOR_BITS = 7.bits
        private val MINOR_BITS = 19.bits
        private val PATCH_BITS = 5.bits

        private const val MAJOR_NAME = "Major"
        private const val MINOR_NAME = "Minor"
        private const val PATCH_NAME = "Patch"
    }
}
