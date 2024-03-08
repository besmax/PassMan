plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    id("com.google.devtools.ksp")
}
android.namespace = "bes.max.database.impl"

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(project(":database:api"))
    ksp(libs.androidx.room.compiler)
    implementation(libs.hilt)
    implementation(libs.hilt.compiler)
}