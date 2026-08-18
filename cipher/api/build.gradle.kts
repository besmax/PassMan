plugins {
    id("passman.android.library")
    alias(libs.plugins.hilt)
}

android {
    namespace = "bes.max.passman.cipher.api"

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
}