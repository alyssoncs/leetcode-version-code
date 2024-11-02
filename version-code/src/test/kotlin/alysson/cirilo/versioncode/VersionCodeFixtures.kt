package alysson.cirilo.versioncode

fun String.toVersionCode(): VersionCode {
    return SemanticVersion.from(this).toVersionCode()
}

private data class SemanticVersion(val major: Int, val minor: Int, val patch: Int) {
    fun toVersionCode(): VersionCode {
        return VersionCode(major, minor, patch)
    }

    companion object {
        fun from(str: String): SemanticVersion {
            val (major, minor, patch) = str.split(".").map(String::toInt)
            return SemanticVersion(major, minor, patch)
        }

        fun from(versionCode: VersionCode): SemanticVersion {
            return with(versionCode) { SemanticVersion(major, minor, patch) }
        }
    }
}

fun VersionCode.withPatch(patch: Int): VersionCode {
    return change { copy(patch = patch) }
}

fun VersionCode.withMinor(minor: Int): VersionCode {
    return change { copy(minor = minor) }
}

fun VersionCode.withMajor(major: Int): VersionCode {
    return change { copy(major = major) }
}

private fun VersionCode.change(block: SemanticVersion.() -> SemanticVersion): VersionCode {
    return SemanticVersion.from(this).block().toVersionCode()
}
