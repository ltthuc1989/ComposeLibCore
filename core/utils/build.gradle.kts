plugins {
    id("composetemplate.kotlin.multiplatform.library")
}

@Suppress("UnstableApiUsage")
kotlin {
    android {
        namespace = "com.ltthuc.utils"
        compileSdk = 35
        minSdk = 24
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.android.core)
            implementation(libs.timber)
        }
    }
}
