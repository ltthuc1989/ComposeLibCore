plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(lib.plugin.androidGradle)
    implementation(lib.plugin.kotlinGradle)
    implementation(lib.plugin.composeCompiler)
    implementation(lib.plugin.ksp)
    implementation(lib.plugin.hilt)
    implementation(lib.plugin.safeargs)
    implementation(lib.plugin.google.services)
    implementation(lib.plugin.detekt)
}
