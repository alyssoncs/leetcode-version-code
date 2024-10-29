package alysson.cirilo.versioncode.utils

import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider

fun VersionCatalog.getLibrary(catalogAlias: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(catalogAlias).get()
}

fun VersionCatalog.getBundle(catalogAlias: String): Provider<ExternalModuleDependencyBundle> {
    return findBundle(catalogAlias).get()
}

fun VersionCatalog.getIntVersion(catalogAlias: String): Int {
    return getVersion(catalogAlias).toInt()
}

fun VersionCatalog.getVersion(catalogAlias: String): String {
    return findVersion(catalogAlias).get().requiredVersion
}

