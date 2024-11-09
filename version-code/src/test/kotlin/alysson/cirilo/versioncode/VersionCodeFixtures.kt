package alysson.cirilo.versioncode

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
    private val versionCode = VersionCode(
        schema = listOf(
            MAJOR_NAME to MAJOR_BITS,
            MINOR_NAME to MINOR_BITS,
            PATCH_NAME to PATCH_BITS,
        ),
        major,
        minor,
        patch,
    )

    override fun compareTo(other: SemanticVersionCode): Int = this.versionCode.compareTo(other.versionCode)

    override fun toString(): String = versionCode.toString()

    val value = versionCode.value
    val major = versionCode[MAJOR_NAME]!!
    val minor = versionCode[MINOR_NAME]!!
    val patch = versionCode[PATCH_NAME]!!

    companion object {
        private const val MAJOR_BITS = 7
        private const val MINOR_BITS = 19
        private const val PATCH_BITS = 5

        private const val MAJOR_NAME = "Major"
        private const val MINOR_NAME = "Minor"
        private const val PATCH_NAME = "Patch"
    }
}
