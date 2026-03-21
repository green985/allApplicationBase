plugins {
    alias(libs.plugins.android.library)


}

android {
    namespace = "com.oyetech.languageimp"
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(project(":domain"))
    implementation(project(":data:models"))
    implementation(project(":languageModule"))
    implementation(project(":subImpl:tools"))

    implementation(libs.timber)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

}
