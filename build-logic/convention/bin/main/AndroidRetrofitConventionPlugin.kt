import com.ltthuc.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRetrofitConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("retrofit").get())
                add("implementation", libs.findLibrary("gsonConverter").get())
                add("implementation", libs.findLibrary("gson").get())
                add("implementation", libs.findLibrary("loggingInterceptor").get())
            }
        }
    }
}
