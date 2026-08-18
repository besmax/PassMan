plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.hilt)
    id("com.google.devtools.ksp")
}
android {
    namespace = "bes.max.passman.database.impl"
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
        implementation(libs.androidx.room.ktx)
        ksp(libs.androidx.room.compiler)
        implementation(libs.hilt)
        ksp(libs.hilt.compiler)
        implementation(project(":database:api"))
    }
}