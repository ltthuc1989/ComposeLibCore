// Per-module publishing config for `core:*` and `navigation` libraries.
// Apply at the bottom of a module's build.gradle.kts:
//   apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
//
// Publishes to:
//   • mavenLocal()      — `./gradlew publishToMavenLocal`
//   • GitHub Packages   — `./gradlew publishAllPublicationsToGithubPackagesRepository`
//                          (needs GITHUB_USER + GITHUB_TOKEN env vars or gpr.user/gpr.token gradle props)

plugins.apply("maven-publish")

afterEvaluate {
    extensions.configure<PublishingExtension>("publishing") {
        publications.create<MavenPublication>("release") {
            from(components.getByName("release"))

            groupId = "io.github.ltthuc.template"
            artifactId = project.path.substringAfterLast(":")
            version = project.findProperty("publishVersion") as? String
                ?: rootProject.extensions.getByType<VersionCatalogsExtension>()
                    .named("libs")
                    .findVersion("publishVersion")
                    .get()
                    .requiredVersion

            pom {
                name.set(artifactId)
                description.set("Compose Template — ${artifactId}")
                url.set("https://github.com/REPLACE_ME/ComposeTemplate")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
