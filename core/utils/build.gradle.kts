plugins {
    id(PluginId.COMPOSE_CONVENTION)
}

android {
    namespace = "com.dvm.utils"
}

dependencies {
    implementation(projects.core.ui)

    implementation(libs.android.core)

    implementation(libs.bundles.compose)
    implementation(libs.compose.navigation)
}
