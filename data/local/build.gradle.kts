plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
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
