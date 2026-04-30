import com.android.build.api.dsl.LibraryExtension
import com.ltthuc.buildlogic.extensions.configureKotlinAndroid
import com.ltthuc.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }

            dependencies {
                add("debugImplementation", libs.findLibrary("leakCanary").get())
                libs.findBundle("testing").ifPresent { bundle ->
                    add("testImplementation", bundle)
                }
            }
        }
    }
}
