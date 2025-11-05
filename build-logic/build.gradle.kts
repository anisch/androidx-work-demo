plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.13.0")
}

gradlePlugin {
    plugins {
        create("androidPlugin") {
            id = "com.example.android.app"
            implementationClass = "com.example.AndroidBasePlugin"
        }
    }
}
