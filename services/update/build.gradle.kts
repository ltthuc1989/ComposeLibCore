plugins {
    id(PluginId.LIBRARY_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.updateservice"
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.datastore)

    implementation(libs.android.core)
    implementation(libs.coroutines)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)
}
