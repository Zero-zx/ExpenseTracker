import deps.androidx
import deps.hilt

plugins {
    id(build.BuildPlugins.ANDROID_LIBRARY)
    id(build.BuildPlugins.KOTLIN_ANDROID)
    id(build.BuildPlugins.HILT)
    id(build.BuildPlugins.KSP)
}

android {
    namespace = "com.example.common"
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

    buildFeatures {
        viewBinding = true
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
    implementation(libs.androidx.navigation.fragment)
    implementation("androidx.navigation:navigation-ui-ktx:2.9.6")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    androidx()
    hilt()
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}