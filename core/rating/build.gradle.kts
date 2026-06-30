plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ltthuc.rating"
    buildFeatures {
        compose = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.android.core)
    implementation(libs.coroutines)
    implementation(libs.datastore)
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.runtime)
    implementation(libs.google.play.review)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
