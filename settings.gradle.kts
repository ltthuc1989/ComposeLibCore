pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "YammyDelivery"

include(
    ":app",
    ":features:splash",
    ":features:menu",
    ":features:dish",
    ":features:cart",
    ":features:order",
    ":features:auth",
    ":features:profile",
    ":features:notifications",
    ":features:drawer",
    ":features:drawer-api",
    ":services:notification",
    ":services:update",
    ":core:network",
    ":core:database",
    ":core:ui",
    ":core:utils",
    ":core:datastore",
    ":navigation"
)
