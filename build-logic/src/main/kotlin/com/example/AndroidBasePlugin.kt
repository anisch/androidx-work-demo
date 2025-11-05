package de.systeon

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.withType
import java.io.File
import java.io.FileInputStream
import java.util.Properties

class AndroidBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        applyPlugins(target)
        applyAndroid(target)
    }

    private fun applyPlugins(target: Project) {
        target.apply {
            plugin("com.android.application")
            plugin("kotlin-android")
            plugin("com.google.devtools.ksp")
            plugin("dagger.hilt.android.plugin")
        }
    }

    private fun applyAndroid(target: Project) {
        target.android().apply {
            compileSdk = 36
            defaultConfig {
                minSdk = 33
                targetSdk = 34
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                versionName = target.getLiquaVersion()

                vectorDrawables {
                    useSupportLibrary = true
                }
            }

            buildTypes {
                debug {
                    isDebuggable = true
                }
                release {
                    signingConfig = signingConfigs.getByName("release")
                    isMinifyEnabled = true

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

            buildFeatures {
                compose = true
                viewBinding = true
                buildConfig = true
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
    }
}
