plugins {
    id(PluginId.COMPOSE_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.ui"
}

dependencies {
    implementation(libs.bundles.compose)
    implementation(libs.compose.icons)
    implementation(libs.compose.constraintLayout)
    implementation(libs.compose.lottie)
    implementation(libs.compose.coil)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)
}
