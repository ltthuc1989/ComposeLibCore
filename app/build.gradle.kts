plugins {
    id("composetemplate.android.application")
    id("composetemplate.android.hilt")
    id("composetemplate.android.firebase")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.trithuc.app"

    defaultConfig {
        applicationId = "com.trithuc.app"
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
}

dependencies {
    implementation(libs.template.ui)
    implementation(libs.template.utils)
    implementation(libs.template.network)
    implementation(libs.template.datastore)
    implementation(libs.template.ads)
    implementation(libs.template.billing)
    implementation(libs.template.rating)
    implementation(libs.template.navigation)
    implementation(projects.core.database)
    implementation(projects.features.splash)
    implementation(projects.features.home)
    implementation(projects.features.settings)
    implementation(projects.features.testcomponent)

    implementation(libs.android.core)
    implementation(libs.android.appcompat)
    implementation(libs.android.material)
    implementation(libs.android.activityCompose)
    implementation(libs.android.splashScreen)

    implementation(libs.bundles.compose)
    implementation(libs.compose.navigation)
    implementation(libs.compose.viewModel)
    implementation(libs.compose.navigationHilt)
    implementation(libs.compose.iconsExtended)
    implementation(libs.google.ads)

    implementation(libs.lifecycle.runtime)

    implementation(libs.timber)

    debugImplementation(libs.leakCanary)
}
