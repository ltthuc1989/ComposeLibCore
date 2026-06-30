pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "YammyDelivery"

include(
    ":app",
    ":features:splash",
    ":features:home",
    ":features:settings",
    ":features:testcomponent",
    ":core:network",
    ":core:database",
    ":core:ui",
    ":core:utils",
    ":core:datastore",
    ":core:ads",
    ":core:billing",
    ":core:rating",
    ":core:settings-common",
    ":navigation"
)

val useLocalLibs: String =
    (settings.providers.gradleProperty("useLocalLibs").orNull) ?: "true"

if (useLocalLibs.toBoolean()) {
    gradle.allprojects {
        configurations.all {
            resolutionStrategy.dependencySubstitution {
                substitute(module("io.github.ltthuc.template:ui"))
                    .using(project(":core:ui"))
                substitute(module("io.github.ltthuc.template:utils"))
                    .using(project(":core:utils"))
                substitute(module("io.github.ltthuc.template:network"))
                    .using(project(":core:network"))
                substitute(module("io.github.ltthuc.template:datastore"))
                    .using(project(":core:datastore"))
                substitute(module("io.github.ltthuc.template:ads"))
                    .using(project(":core:ads"))
                substitute(module("io.github.ltthuc.template:billing"))
                    .using(project(":core:billing"))
                substitute(module("io.github.ltthuc.template:rating"))
                    .using(project(":core:rating"))
                substitute(module("io.github.ltthuc.template:settings-common"))
                    .using(project(":core:settings-common"))
                substitute(module("io.github.ltthuc.template:navigation"))
                    .using(project(":navigation"))
            }
        }
    }
}
