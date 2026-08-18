plugins {
    id("passman.android.library")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "bes.max.passman.cipher.impl"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt)
    implementation(libs.hilt.compiler)
    implementation(libs.kotlinx.serialization)
    implementation(project(":cipher:api"))
}
