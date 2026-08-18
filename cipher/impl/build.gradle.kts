plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "bes.max.passman.cipher.impl"
    compileSdk = 37

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.hilt)
        implementation(libs.hilt.compiler)
        implementation(libs.kotlinx.serialization)
        implementation(project(":cipher:api"))
    }
}
