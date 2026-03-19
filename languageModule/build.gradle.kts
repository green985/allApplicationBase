plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.oyetech.languageModule"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(project(":data:models"))
    implementation(project(":subImpl:tools"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.timber)

    api(libs.koin.core)

    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.room.runtime)
    implementation(libs.androidx.core)
}
