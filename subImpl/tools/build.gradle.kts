plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)


}

android {
    namespace = "com.oyetech.tools"
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(project(Modules.models))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    implementation(libs.kotlin.stdlib)
    implementation(libs.timber)
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.room.runtime)

}