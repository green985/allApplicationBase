plugins {
    alias(libs.plugins.android.library)


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
    implementation(libs.room.ktx)
    // DATA MODULE
    implementation(project(":data:models"))

    implementation(libs.koin.core)
}
