plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ltthuc.utils"

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.android.core)
    implementation(libs.timber)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
