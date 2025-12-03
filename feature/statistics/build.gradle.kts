import deps.androidx
import deps.feature
import deps.hilt

plugins {
    id(build.BuildPlugins.ANDROID_LIBRARY)
    id(build.BuildPlugins.KOTLIN_ANDROID)
    id(build.BuildPlugins.HILT)
    id(build.BuildPlugins.KSP)
}

android {
    namespace = "com.example.statistics"
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
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    androidx()
    hilt()
    feature()
    
    // MPAndroidChart for line charts
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":common"))
    implementation(project(":domain"))
}
