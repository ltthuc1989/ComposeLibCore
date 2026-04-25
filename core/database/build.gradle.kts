plugins {
    id(PluginId.LIBRARY_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.database"
}

dependencies {
    implementation(projects.core.network)

    implementation(libs.android.core)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)
}
