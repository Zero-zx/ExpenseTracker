import deps.androidx
import deps.hilt
import deps.room

plugins {
    id(build.BuildPlugins.ANDROID_LIBRARY)
    id(build.BuildPlugins.KOTLIN_ANDROID)
    id(build.BuildPlugins.KSP)
    id(build.BuildPlugins.HILT)

}

android {
    namespace = "com.example.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    androidx()
    hilt()
    room()
//    implementation("androidx.room:room-runtime:2.8.3")
//    implementation("androidx.room:room-ktx:2.8.3")
//    ksp("androidx.room:room-compiler:2.8.3")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}