package deps

object Dependencies {

    const val ANDROIDX_CORE = "androidx.core:core-ktx:${DependenciesVersions.CORE_KTX}"
    const val ANDROIDX_JUNIT = "androidx.test.ext:junit:${DependenciesVersions.JUNIT_VERSION}"
    const val ANDROIDX_ESPRESSO_CORE =
        "androidx.test.espresso:espresso-core:${DependenciesVersions.ESPRESSO_CORE}"
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${DependenciesVersions.APPCOMPAT}"
    const val ANDROIDX_RECYCLERVIEW = "androidx.recyclerview:recyclerview:${DependenciesVersions.RECYCLERVIEW}"
    const val MATERIAL = "com.google.android.material:material:${DependenciesVersions.MATERIAL}"
    const val ANDROIDX_ACTIVITY = "androidx.activity:activity:${DependenciesVersions.ACTIVITY}"
    const val ANDROIDX_CONSTRAINT_LAYOUT =
        "androidx.constraintlayout:constraintlayout:${DependenciesVersions.CONSTRAINT_LAYOUT}"
    const val hiltAndroid = "com.google.dagger:hilt-android:${DependenciesVersions.HILT}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${DependenciesVersions.HILT}"
    const val hiltAgp = "com.google.dagger:hilt-android-gradle-plugin:${DependenciesVersions.HILT}"
    const val roomRuntime = "androidx.room:room-runtime:${DependenciesVersions.ROOM}"
    const val roomCompiler = "androidx.room:room-compiler:${DependenciesVersions.ROOM}"
    const val roomKtx = "androidx.room:room-ktx:${DependenciesVersions.ROOM}"
    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${DependenciesVersions.LIFE_CYCLE}"
    const val fragment = "androidx.fragment:fragment-ktx:${DependenciesVersions.FRAGMENT}"
    const val cardView = "androidx.cardview:cardview:${DependenciesVersions.CARD_VIEW}"
}