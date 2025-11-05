import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")

    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.example.androidx_work_demo"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.androidx_work_demo"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)

            optIn.add("kotlin.RequiresOptIn")
            optIn.add("kotlin.Experimental")
            optIn.add("kotlin.time.ExperimentalTime")

            freeCompilerArgs.addAll(
                "-Xjspecify-annotations=strict",
                "-Xtype-enhancement-improvements-strict-mode",
            )
        }
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serial.json)

    // ktor
    implementation(project.dependencies.enforcedPlatform(libs.ktor.bom))
    implementation(libs.bundles.ktor.client)
    implementation(libs.ktor.serialization.kotlinx.json)

    // android
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.work.runtime.ktx)
//    implementation(libs.google.guava)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.google.material)

    // hilt dependency injection
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.work)

    // logger
    implementation(libs.timber)

    // crash reports
    implementation(libs.bundles.acra)

    // test
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.androidx.room.testing)

    // instrumented test on a device
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlin.test.junit)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.androidx.room.testing)

    androidTestImplementation(libs.dagger.hilt.android.testing)
    kspAndroidTest(libs.dagger.hilt.compiler)

    // debug
    debugImplementation(composeBom)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}