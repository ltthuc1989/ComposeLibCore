import java.io.FileInputStream
import java.util.Properties

plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.ltthuc.ads"

    defaultConfig {
        // Google test app ID — overridden in release flavor via security.properties
        resValue("string", "ADS_APP_ID", "ca-app-pub-3940256099942544~3347511713")
        buildConfigField("String", "ADS_APPLICATION_ID", "\"ca-app-pub-3940256099942544~3347511713\"")
        buildConfigField("String", "ADS_APP_OPEN_ID", "\"ca-app-pub-3940256099942544/9257395921\"")
        buildConfigField("String", "ADS_BANNER_ID", "\"ca-app-pub-3940256099942544/2014213617\"")
        buildConfigField("String", "ADS_FULL_SCREEN_ID", "\"ca-app-pub-3940256099942544/1033173712\"")
        buildConfigField("String", "ADS_NATIVE_ID", "\"ca-app-pub-3940256099942544/2247696110\"")
        buildConfigField("String", "ADS_REWARD_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
    }

    buildTypes {
        getByName("release") {
            val securityFile = File(rootProject.rootDir, "security.properties")
            val fallbackFile = File(rootProject.rootDir, "security.properties.example")
            val source = if (securityFile.exists()) securityFile else fallbackFile
            val prop = Properties().apply {
                if (source.exists()) FileInputStream(source).use(::load)
            }
            resValue(
                "string",
                "ADS_APP_ID",
                prop.getProperty("ADS_APPLICATION_ID", "ca-app-pub-3940256099942544~3347511713"),
            )
            buildConfigField(
                "String",
                "ADS_APPLICATION_ID",
                "\"${prop.getProperty("ADS_APPLICATION_ID", "ca-app-pub-3940256099942544~3347511713")}\"",
            )
            buildConfigField(
                "String",
                "ADS_APP_OPEN_ID",
                "\"${prop.getProperty("ADS_APP_OPEN_ID", "ca-app-pub-3940256099942544/9257395921")}\"",
            )
            buildConfigField(
                "String",
                "ADS_BANNER_ID",
                "\"${prop.getProperty("ADS_BANNER_ID", "ca-app-pub-3940256099942544/2014213617")}\"",
            )
            buildConfigField(
                "String",
                "ADS_FULL_SCREEN_ID",
                "\"${prop.getProperty("ADS_INTERSTITIAL_ID", "ca-app-pub-3940256099942544/1033173712")}\"",
            )
            buildConfigField(
                "String",
                "ADS_NATIVE_ID",
                "\"${prop.getProperty("ADS_NATIVE_ID", "ca-app-pub-3940256099942544/2247696110")}\"",
            )
            buildConfigField(
                "String",
                "ADS_REWARD_ID",
                "\"${prop.getProperty("ADS_REWARDED_ID", "ca-app-pub-3940256099942544/5224354917")}\"",
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(projects.core.utils)

    implementation(libs.android.core)
    implementation(libs.bundles.compose)
    implementation(libs.google.ads)
    implementation(libs.google.ump)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.process)
}

apply(from = "$rootDir/scripts/publish-lib.gradle.kts")
