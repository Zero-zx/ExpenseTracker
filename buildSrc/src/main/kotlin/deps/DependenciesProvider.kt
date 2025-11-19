package deps

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

fun DependencyHandler.room() {
    implementation(Dependencies.roomKtx)
    implementation(Dependencies.roomRuntime)
    add("ksp", Dependencies.roomCompiler)
}


fun DependencyHandler.hilt() {
    implementation(Dependencies.hiltAndroid)
    add("ksp", Dependencies.hiltCompiler)
}

fun DependencyHandler.androidx() {
    implementation(Dependencies.ANDROIDX_CORE)
    implementation(Dependencies.ANDROIDX_JUNIT)
    implementation(Dependencies.ANDROIDX_ESPRESSO_CORE)
    implementation(Dependencies.ANDROIDX_APPCOMPAT)
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.ANDROIDX_ACTIVITY)
    implementation(Dependencies.ANDROIDX_CONSTRAINT_LAYOUT)
}

fun DependencyHandler.homeModule() {
    moduleImplementation(project(":home-ui"))
    moduleImplementation(project(":home-component"))
}

fun DependencyHandler.noteModule() {
    moduleImplementation(project(":note-ui"))
    moduleImplementation(project(":note-component"))
}