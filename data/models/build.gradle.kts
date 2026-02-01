plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.lsposed.lsparanoid")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp") // Eğer room veya başka bir ksp gerektiren library varsa
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
    implementation(libs.firebase.firestore.ktx)
}
