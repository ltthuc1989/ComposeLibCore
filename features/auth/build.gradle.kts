plugins {
    id(PluginId.COMPOSE_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.auth"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.utils)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.features.drawerApi)
    implementation(projects.services.update)
    implementation(projects.navigation)

    implementation(libs.android.core)
    implementation(libs.android.appcompat)

    implementation(libs.bundles.compose)
    implementation(libs.compose.viewModel)
    implementation(libs.compose.navigationHilt)

    implementation(libs.lifecycle.livedata)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)

    implementation(libs.coroutines)
}
