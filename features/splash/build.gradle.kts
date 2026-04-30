plugins {
    id("composetemplate.android.feature")
}

android {
    namespace = "com.ltthuc.splash"
}

dependencies {
    implementation(libs.compose.lottie)
    implementation(libs.android.appcompat)
}
