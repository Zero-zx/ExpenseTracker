package deps

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.room() {
    implementation(Dependencies.roomKtx)
    implementation(Dependencies.roomRuntime)
    add("ksp", Dependencies.roomCompiler)
}


fun DependencyHandler.hilt() {
    implementation(Dependencies.hiltAndroid)
    add("ksp", Dependencies.hiltCompiler)
}

fun DependencyHandler.hiltFeature() {
    implementation(Dependencies.hiltAndroid)
}


fun DependencyHandler.androidx() {
    implementation(Dependencies.ANDROIDX_CORE)
    implementation(Dependencies.ANDROIDX_JUNIT)
    implementation(Dependencies.ANDROIDX_ESPRESSO_CORE)
    implementation(Dependencies.ANDROIDX_APPCOMPAT)
    implementation(Dependencies.ANDROIDX_RECYCLERVIEW)
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.ANDROIDX_ACTIVITY)
    implementation(Dependencies.ANDROIDX_CONSTRAINT_LAYOUT)
}

fun DependencyHandler.feature() {
    implementation(Dependencies.lifecycle)
    implementation(Dependencies.fragment)
    implementation(Dependencies.cardView)
}