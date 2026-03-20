plugins {
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.lsparanoid)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)

}

android {
    namespace = "com.oyetech.models"

    // Ortak build dosyasından gelen değerleri libs.versions.toml'dan alıyorum
    compileSdk = libs.versions.compile.sdk.get().toInt()

    buildFeatures {
        dataBinding = true
        buildConfig = true
        viewBinding = true
    }

}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.timber)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    // Serialization & Parsing
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Room
    implementation(libs.room.runtime)

    // Firebase
    implementation(libs.firebase.firestore)

}
