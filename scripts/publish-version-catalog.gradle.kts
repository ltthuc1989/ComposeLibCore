// Publishes `gradle/libs.versions.toml` as a reusable version catalog artifact.
// Apply from the ROOT build.gradle.kts:
//   apply(from = "$rootDir/scripts/publish-version-catalog.gradle.kts")
//
// Publishes:
//   io.github.ltthuc.template:version-catalog:<templateLibVersion>
//
// Consumers use it from their settings.gradle.kts:
//   dependencyResolutionManagement {
//       repositories { mavenLocal(); google(); mavenCentral() }
//       versionCatalogs {
//           create("libs") {
//               from("io.github.ltthuc.template:version-catalog:1.0.0")
//           }
//       }
//   }

plugins.apply("version-catalog")
plugins.apply("maven-publish")

extensions.configure<org.gradle.api.plugins.catalog.CatalogPluginExtension>("catalog") {
    versionCatalog {
        from(files("$rootDir/gradle/libs.versions.toml"))
    }
}

afterEvaluate {
    extensions.configure<PublishingExtension>("publishing") {
        publications {
            register<MavenPublication>("versionCatalog") {
                from(components.getByName("versionCatalog"))

                groupId = "io.github.ltthuc.template"
                artifactId = "version-catalog"
                version = rootProject.extensions
                    .getByType<VersionCatalogsExtension>()
                    .named("libs")
                    .findVersion("templateLibVersion")
                    .get()
                    .requiredVersion

                pom {
                    name.set("Compose Template — version catalog")
                    description.set("Shared Gradle version catalog for the Compose Template libraries.")
                    url.set("https://github.com/REPLACE_ME/ComposeTemplate")
                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }

        repositories {
            mavenLocal()
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/REPLACE_ME/ComposeTemplate")
                credentials {
                    username = System.getenv("GITHUB_USER")
                        ?: providers.gradleProperty("gpr.user").orNull
                    password = System.getenv("GITHUB_TOKEN")
                        ?: providers.gradleProperty("gpr.token").orNull
                }
            }
        }
    }
}
