plugins {
    id(PluginId.LIBRARY_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.network"
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.datastore)

    implementation(libs.android.core)

    implementation(libs.coroutines)

    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    implementation(libs.gson)
    implementation(libs.loggingInterceptor)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)
}
