plugins {
    id("composetemplate.android.feature")
}

android {
    namespace = "com.ltthuc.settings"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.template.billing)
    implementation(libs.compose.iconsExtended)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.runtime.compose)
}
