plugins {
    id(PluginId.COMPOSE_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.drawer"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.utils)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.navigation)

    implementation(libs.bundles.compose)
    implementation(libs.compose.icons)
    implementation(libs.compose.viewModel)
    implementation(libs.compose.coil)

    implementation(libs.lifecycle.viewModel)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)
}
