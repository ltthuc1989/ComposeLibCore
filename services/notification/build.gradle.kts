plugins {
    id(PluginId.LIBRARY_CONVENTION)
    id(PluginId.KSP)
    id(PluginId.DAGGER_HILT)
}

android {
    namespace = "com.dvm.notification"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.utils)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.navigation)

    implementation(libs.android.core)
    implementation(libs.coroutines)

    implementation(libs.hilt.library)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
}
