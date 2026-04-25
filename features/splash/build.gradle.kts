plugins {
    id(PluginId.COMPOSE_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.splash"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.datastore)
    implementation(projects.services.update)
    implementation(projects.navigation)

    implementation(libs.android.core)
    implementation(libs.android.appcompat)

    implementation(libs.bundles.compose)
    implementation(libs.compose.lottie)
    implementation(libs.compose.navigationHilt)

    implementation(libs.lifecycle.viewModel)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)
}
