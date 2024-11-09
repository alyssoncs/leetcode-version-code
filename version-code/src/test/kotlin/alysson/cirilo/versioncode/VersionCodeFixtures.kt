package alysson.cirilo.versioncode

fun String.toVersionCode(): VersionCode {
    val (major, minor, patch) = this.split(".").map(String::toInt)
    return VersionCode(major, minor, patch)
}

fun VersionCode.bumpPatch(): VersionCode {
    return withPatch(patch.inc())
}

fun VersionCode.withPatch(patch: Int): VersionCode {
    return VersionCode(major = major, minor = minor, patch = patch)
}

fun VersionCode.bumpMinor(): VersionCode {
    return withMinor(minor.inc())
}

fun VersionCode.withMinor(minor: Int): VersionCode {
    return VersionCode(major = major, minor = minor, patch = patch)
}

fun VersionCode.bumpMajor(): VersionCode {
    return withMajor(major.inc())
}

fun VersionCode.withMajor(major: Int): VersionCode {
    return VersionCode(major = major, minor = minor, patch = patch)
}
