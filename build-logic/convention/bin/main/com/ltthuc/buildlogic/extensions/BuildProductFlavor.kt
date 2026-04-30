package com.ltthuc.buildlogic.extensions

import com.android.build.api.dsl.ApplicationExtension

enum class FlavorDimension(val dimensionName: String) {
    Version("version"),
}

enum class TemplateFlavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
    val isAdsEnabled: Boolean = true,
) {
    Staging(FlavorDimension.Version, applicationIdSuffix = ".staging", isAdsEnabled = false),
    Free(FlavorDimension.Version, applicationIdSuffix = null, isAdsEnabled = true),
    Paid(FlavorDimension.Version, applicationIdSuffix = ".paid", isAdsEnabled = false),
}

fun configureFlavors(extension: ApplicationExtension) {
    val dimensions = TemplateFlavor.values().map { it.dimension.dimensionName }.distinct()
    extension.flavorDimensions += dimensions

    TemplateFlavor.values().forEach { flavor ->
        extension.productFlavors.create(flavor.name.lowercase()) {
            dimension = flavor.dimension.dimensionName
            flavor.applicationIdSuffix?.let { applicationIdSuffix = it }
            buildConfigField(
                "boolean",
                "FORCE_HIDE_ADS",
                (!flavor.isAdsEnabled).toString(),
            )
        }
    }
}
