plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ltthuc.settings_common"
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
    implementation(libs.compose.iconsExtended)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.runtime.compose)

    // Sibling template libs (resolved via substitution in dev, mavenLocal in CI/release)
    implementation(libs.template.ads)
    implementation(libs.template.billing)
    implementation(libs.template.utils)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
