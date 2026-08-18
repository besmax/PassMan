import com.google.protobuf.gradle.id

plugins {
    id("passman.android.library.compose")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "bes.max.passman.features.main"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }
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

    implementation(project(":database:api"))
    implementation(project(":cipher:api"))
    implementation(project(":ui"))

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

protobuf {
    protoc {
        artifact = libs.plugins.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") { option("lite") }
                id("kotlin") { option("lite") }
            }
        }
    }
}

androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            val protoTaskName = "generate" + variant.name.replaceFirstChar { it.uppercaseChar() } + "Proto"
            val protoTask = project.tasks.getByName(protoTaskName) as com.google.protobuf.gradle.GenerateProtoTask

            variant.sources.kotlin?.addGeneratedSourceDirectory(
                project.tasks.named<com.google.protobuf.gradle.GenerateProtoTask>(protoTaskName)
            ) { task ->
                project.objects.directoryProperty().apply {
                    set(project.layout.projectDirectory.dir(task.outputBaseDir))
                }
            }

            val kspTaskName = "ksp" + variant.name.replaceFirstChar { it.uppercaseChar() } + "Kotlin"
            project.tasks.getByName(kspTaskName) {
                dependsOn(protoTask)
            }
        }
    }
}

