plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
}

apply(from = "$rootDir/scripts/publish-version-catalog.gradle.kts")

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

tasks.register("publishLocal") {
    group = "publishing"
    description = "Publish all library modules + version catalog to mavenLocal (~/.m2/repository)"
    dependsOn(
        subprojects
            .filter { it.path.startsWith(":core:") || it.path == ":navigation" }
            .filter { it.path != ":core:database" }
            .map { "${it.path}:publishToMavenLocal" }
    )
    dependsOn(":publishVersionCatalogPublicationToMavenLocal")
}
