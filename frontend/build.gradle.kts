// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("androidx.navigation.safeargs") version "2.7.7" apply false
}

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}