plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.ltthuc.ui"
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
    implementation(projects.core.utils)

    implementation(libs.bundles.compose)
    implementation(libs.compose.icons)
    implementation(libs.compose.iconsExtended)
    implementation(libs.compose.constraintLayout)
    implementation(libs.compose.lottie)
    implementation(libs.compose.coil)
    implementation(libs.compose.viewModel)
    implementation(libs.compose.navigation)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewModel)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
