plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.oyetech.dimodule"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data:remote"))
    implementation(project(":data:local"))
    implementation(project(":data:repository"))
    implementation(project(":composeBase"))
    implementation(project(":glideModule"))
    implementation(project(":reviewer"))
    implementation(project(":googleLogin"))
    implementation(project(":subImpl:languageImp"))
    implementation(project(":notificationModule"))
    implementation(project(":adsModule"))
    implementation(project(":firebaseDB"))
    implementation(project(":exoplayerModule"))
    implementation(project(":data:models"))
    implementation(project(":subImpl:tools"))
    implementation(project(":firebaseRealtime"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.perf)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // Network
    implementation(libs.retrofit.kotlin.coroutines.adapter)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.core.ktx)

    ksp(libs.moshi.kotlin.codegen)

    // Parsing
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)

    // Android
    implementation(libs.androidx.appcompat)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.compose.runtime)
    implementation(libs.core.splashscreen)
    implementation(libs.timber)
}
