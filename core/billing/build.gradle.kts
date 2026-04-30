plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ltthuc.billing"
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(projects.core.utils)

    implementation(libs.android.core)
    implementation(libs.coroutines)
    implementation(libs.google.billing)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
