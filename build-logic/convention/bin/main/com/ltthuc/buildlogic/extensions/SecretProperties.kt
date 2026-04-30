package com.ltthuc.buildlogic.extensions

import java.io.File
import java.util.Properties
import org.gradle.api.Project

internal fun Project.loadSecretProperties(): Properties {
    val file = File(rootProject.projectDir, "security.properties")
    val fallback = File(rootProject.projectDir, "security.properties.example")
    val source = when {
        file.exists() -> file
        fallback.exists() -> fallback
        else -> null
    }
    val props = Properties()
    source?.inputStream()?.use(props::load)
    return props
}

internal fun Project.getSecurityProperty(key: String, default: String = ""): String {
    val props = loadSecretProperties()
    return props.getProperty(key, default)
}
