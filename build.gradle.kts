// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
    }
}

//
//task clean(type: Delete) {
//    delete rootProject.buildDir
//}

plugins {
    id("com.android.application") version "8.7.3" apply false
}
