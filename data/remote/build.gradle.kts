plugins {
    alias(libs.plugins.android.library)


    alias(libs.plugins.ksp)

}

android {
    namespace = "com.oyetech.remote"

    // Ortak build dosyasından gelen değerleri libs.versions.toml'dan alıyorum
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(project(":secureKeys"))
    implementation(project(":domain"))
    implementation(project(":data:models"))
    implementation(project(":subImpl:tools"))

    // KOTLIN
    implementation(libs.kotlin.stdlib)

    // NETWORK
    implementation(libs.retrofit.kotlin.coroutines.adapter)
    implementation(libs.retrofit)
    implementation(libs.timber)

    // CONVERTERS & PARSING
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.moshi)


    // Moshi CodeGen for Kotlin
    ksp(libs.moshi.kotlin.codegen)

    // HTTP CLIENT
    implementation(libs.okhttp.logging.interceptor)

    // KOIN
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

}
