plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.comet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.comet"
        minSdk = 26
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "33.0.1"
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.0.0")
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    implementation ("androidx.palette:palette:1.0.0")
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media:media:1.7.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    implementation("androidx.media3:media3-common:1.2.0")
    implementation("androidx.media3:media3-session:1.2.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
repositories {
    google()
    mavenCentral()
//    maven {
//        url "https://plugins.gradle.org/m2/"
//    }
}
