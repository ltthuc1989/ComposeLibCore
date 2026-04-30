plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.android.retrofit")
}

android {
    namespace = "com.ltthuc.network"
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.datastore)

    implementation(libs.android.core)
    implementation(libs.coroutines)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
