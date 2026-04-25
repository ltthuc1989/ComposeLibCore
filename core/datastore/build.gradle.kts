plugins {
    id(PluginId.LIBRARY_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.preferences"
}

dependencies {
    implementation(projects.core.utils)

    implementation(libs.android.core)
    implementation(libs.datastore)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)
}
