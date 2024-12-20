import com.autonomousapps.DependencyAnalysisSubExtension

plugins {
    id("com.alyssoncirilo.detekt")
    id("com.squareup.sort-dependencies")
    id("com.autonomousapps.dependency-analysis")
}

extensions.configure<DependencyAnalysisSubExtension> {
    issues {
        val toFail = arrayOf(
            ::onUnusedDependencies,
            ::onIncorrectConfiguration,
            ::onCompileOnly,
            ::onRuntimeOnly,
            ::onUnusedAnnotationProcessors,
            ::onRedundantPlugins,
            ::onModuleStructure,
        )

        toFail.forEach { issue ->
            issue {
                severity("fail")
            }
        }

        val toIgnore = arrayOf(
            ::onUsedTransitiveDependencies,
        )

        toIgnore.forEach { issue ->
            issue {
                severity("ignore")
            }
        }
    }
}

tasks.named("check") {
    dependsOn("projectHealth")
}
