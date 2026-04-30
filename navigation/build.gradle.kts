plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ltthuc.navigation"
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.android.core)
    implementation(libs.coroutines)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
