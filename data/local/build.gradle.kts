plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.oyetech.local"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    // ROOM
    ksp(libs.room.compiler)
    implementation(libs.room.runtime)
    // DATA MODULE
    implementation(project(Modules.models))

    implementation(libs.koin.core)
}
