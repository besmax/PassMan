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
    ksp(libs.androidx.room.compiler)
    implementation(libs.hilt)
    implementation(libs.hilt.compiler)
    implementation(project(":database:api"))

    //Only for purpose of populating database with mockData
    implementation(project(":cipher:impl"))
    implementation(project(":cipher:api"))
}