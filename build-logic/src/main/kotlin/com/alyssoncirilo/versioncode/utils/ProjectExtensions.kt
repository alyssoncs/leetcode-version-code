package com.alyssoncirilo.versioncode.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

internal val Project.libs: VersionCatalog
    get() = this.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
