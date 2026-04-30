import com.android.build.api.dsl.LibraryExtension
import com.ltthuc.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("composetemplate.android.library")
                apply("composetemplate.android.hilt")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }

            dependencies {
                add("implementation", libs.findLibrary("template-ui").get())
                add("implementation", libs.findLibrary("template-utils").get())
                add("implementation", libs.findLibrary("template-navigation").get())

                add("implementation", libs.findBundle("compose").get())
                add("implementation", libs.findLibrary("compose-viewModel").get())
                add("implementation", libs.findLibrary("compose-navigationHilt").get())
                add("implementation", libs.findLibrary("lifecycle-viewModel").get())
                add("implementation", libs.findLibrary("android-core").get())
            }
        }
    }
}
