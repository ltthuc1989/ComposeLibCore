plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ltthuc.appupdate"
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
    // ProcessLifecycleOwner — the check runs per process foreground, not per Activity creation.
    api(libs.lifecycle.process)
    api(libs.google.play.app.update)

    testImplementation(libs.bundles.testing)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
