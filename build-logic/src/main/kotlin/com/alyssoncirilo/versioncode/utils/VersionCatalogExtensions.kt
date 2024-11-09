package com.alyssoncirilo.versioncode.utils

import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider

internal fun VersionCatalog.getLibrary(catalogAlias: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(catalogAlias).get()
}

internal fun VersionCatalog.getBundle(catalogAlias: String): Provider<ExternalModuleDependencyBundle> {
    return findBundle(catalogAlias).get()
}

internal fun VersionCatalog.getIntVersion(catalogAlias: String): Int {
    return getVersion(catalogAlias).toInt()
}

internal fun VersionCatalog.getVersion(catalogAlias: String): String {
    return findVersion(catalogAlias).get().requiredVersion
}

