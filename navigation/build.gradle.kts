plugins {
    id(PluginId.LIBRARY_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.navigation"
}

dependencies {
    implementation(libs.android.core)
    implementation(libs.coroutines)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)
}
