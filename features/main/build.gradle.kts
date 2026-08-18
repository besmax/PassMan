plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "bes.max.passman.features.main"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.compose.ui)
        implementation(libs.androidx.activity.compose)
        implementation(libs.compose.tooling)
        implementation(libs.compose.foundation)
        implementation(libs.compose.lifecycle)
        implementation(libs.compose.livedata)
        implementation(libs.androidx.material3)
        implementation(libs.hilt)
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.androidx.datastore)
        implementation(libs.kotlin.immutable.collections)
        ksp(libs.hilt.compiler)
        implementation(libs.hilt.nav.compose)
        implementation(libs.lifecycle.viewmodel)
        implementation(libs.coil)
        implementation(libs.biometric.ktx)
        implementation(libs.accompanist.permissions)
        implementation(libs.kotlinx.serialization)
        implementation(libs.protobuf.javalite)
        implementation(libs.protobuf.kotlin.lite)
        implementation(libs.androidx.compose.material.icons.extended)
        implementation(platform(libs.androidx.compose.bom))

        implementation(project(":database:api"))
        implementation(project(":cipher:api"))
        implementation(project(":ui"))

        testImplementation(libs.junit)

        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }
}
