plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
}

android.namespace = "bes.max.cipher.api"

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt)
    implementation(libs.hilt.compiler)
}