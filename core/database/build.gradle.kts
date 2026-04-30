plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.android.room")
}

android {
    namespace = "com.ltthuc.database"
}

dependencies {
    implementation(libs.android.core)
}
