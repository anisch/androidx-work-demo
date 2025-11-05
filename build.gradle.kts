// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.tools.build.gradle)
        classpath(libs.jetbrains.kotlin.gradle.plugin)
        classpath(libs.google.dagger.gradle.plugin)
        classpath(libs.google.devtools.ksp.plugin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
