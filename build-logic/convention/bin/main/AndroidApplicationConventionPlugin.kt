import com.android.build.api.dsl.ApplicationExtension
import com.ltthuc.buildlogic.extensions.configureFlavors
import com.ltthuc.buildlogic.extensions.configureKotlinAndroid
import com.ltthuc.buildlogic.extensions.getSecurityProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
                buildFeatures.buildConfig = true

                defaultConfig.buildConfigField(
                    "String",
                    "API_SERVICE_URL",
                    "\"${getSecurityProperty("API_SERVICE_URL_PROD", "https://api.example.com/")}\"",
                )
                defaultConfig.buildConfigField(
                    "String",
                    "ADS_APPLICATION_ID",
                    "\"${getSecurityProperty("ADS_APPLICATION_ID")}\"",
                )
                defaultConfig.buildConfigField(
                    "String",
                    "ADS_BANNER_ID",
                    "\"${getSecurityProperty("ADS_BANNER_ID", "ca-app-pub-3940256099942544/2014213617")}\"",
                )
                defaultConfig.buildConfigField(
                    "String",
                    "ADS_FULL_SCREEN_ID",
                    "\"${getSecurityProperty("ADS_INTERSTITIAL_ID", "ca-app-pub-3940256099942544/1033173712")}\"",
                )
                defaultConfig.buildConfigField(
                    "String",
                    "ADS_APP_OPEN_ID",
                    "\"${getSecurityProperty("ADS_APP_OPEN_ID", "ca-app-pub-3940256099942544/9257395921")}\"",
                )
                defaultConfig.buildConfigField(
                    "String",
                    "ADS_REWARD_ID",
                    "\"${getSecurityProperty("ADS_REWARDED_ID", "ca-app-pub-3940256099942544/5224354917")}\"",
                )
                defaultConfig.buildConfigField(
                    "String",
                    "ADS_NATIVE_ID",
                    "\"${getSecurityProperty("ADS_NATIVE_ID", "ca-app-pub-3940256099942544/2247696110")}\"",
                )
                defaultConfig.buildConfigField(
                    "boolean",
                    "ADS_ENABLED",
                    getSecurityProperty("ADS_ENABLED", "false"),
                )

                configureFlavors(this)
            }
        }
    }
}
