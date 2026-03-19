plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.oyetech.languageimp"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(project(Modules.domain))
    implementation(project(Modules.models))
    implementation(project(Modules.languageModule))
    implementation(project(Modules.tools))

    implementation(libs.timber)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)
}
