import com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask
import deps.androidx
import deps.feature
import deps.hilt
import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id(build.BuildPlugins.ANDROID_LIBRARY)
    id(build.BuildPlugins.KOTLIN_ANDROID)
    id(build.BuildPlugins.HILT)
    id(build.BuildPlugins.KSP)
}

android {
    namespace = "com.example.transaction"
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
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

// Configure KSP to properly integrate with data binding
androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            val variantName = variant.name.capitalized()
            val kspTaskName = "ksp${variantName}Kotlin"
            val dataBindingTaskName = "dataBindingGenBaseClasses${variantName}"

            val dataBindingTask =
                tasks.named(dataBindingTaskName, DataBindingGenBaseClassesTask::class.java)

            tasks.named(kspTaskName).configure {
                // Make KSP depend on data binding generation
                dependsOn(dataBindingTask)
            }

            // Wire data binding output as input to KSP kotlin compilation
            kotlin.sourceSets.getByName(variant.name) {
                kotlin.srcDir(files(dataBindingTask.get().sourceOutFolder).builtBy(dataBindingTask))
            }
        }
    }
}

dependencies {
//    implementation(libs.glide)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    androidx()
    hilt()
    feature()
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":common"))
    implementation(project(":domain"))
}

