plugins {
    `kotlin-dsl`
}

group = "com.ltthuc.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.plugin.androidGradle)
    compileOnly(libs.plugin.kotlinGradle)
    compileOnly(libs.plugin.composeCompiler)
    compileOnly(libs.plugin.ksp)
    compileOnly(libs.plugin.hilt)
    compileOnly(libs.plugin.google.services)
    compileOnly(libs.plugin.detekt)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "composetemplate.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "composetemplate.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("kotlinMultiplatformLibrary") {
            id = "composetemplate.kotlin.multiplatform.library"
            implementationClass = "KotlinMultiplatformLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "composetemplate.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "composetemplate.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "composetemplate.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidRetrofit") {
            id = "composetemplate.android.retrofit"
            implementationClass = "AndroidRetrofitConventionPlugin"
        }
        register("androidApplicationFirebase") {
            id = "composetemplate.android.firebase"
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }
    }
}
