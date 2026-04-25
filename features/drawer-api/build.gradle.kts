plugins {
    id(PluginId.COMPOSE_CONVENTION)
}

android {
    namespace = "com.dvm.drawer_api"
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.features.drawer)

    implementation(libs.bundles.compose)
}
