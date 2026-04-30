plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ltthuc.preferences"
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(projects.core.utils)

    implementation(libs.android.core)
    implementation(libs.datastore)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
