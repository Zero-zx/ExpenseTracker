import deps.androidx
import deps.hilt
import deps.room
import build.BuildConfig
import release.ReleaseConfig
import test.TestBuildConfig

plugins {
    id(build.BuildPlugins.ANDROID_APPLICATION)
    id(build.BuildPlugins.KOTLIN_ANDROID)
    id(build.BuildPlugins.HILT)
    id(build.BuildPlugins.KSP)
}

android {
    namespace = BuildConfig.APP_ID
    compileSdk {
        version = release(BuildConfig.COMPILE_SDK_VERSION)
    }

    defaultConfig {
        applicationId = BuildConfig.APP_ID
        minSdk = BuildConfig.MIN_SDK_VERSION
        targetSdk = BuildConfig.TARGET_SDK_VERSION
        versionCode = ReleaseConfig.VERSION_CODE
        versionName = ReleaseConfig.VERSION_NAME

        testInstrumentationRunner = TestBuildConfig.TEST_INSTRUMENTATION_RUNNER
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}