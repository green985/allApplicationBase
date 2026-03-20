plugins {
    alias(libs.plugins.android.library)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.ksp)
}

android {
    namespace = "com.oyetech.dimodule"
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(project(Modules.domain))
    implementation(project(Modules.remote))
    implementation(project(Modules.local))
    implementation(project(Modules.repository))
    implementation(project(Modules.composeBase))
    implementation(project(Modules.radioService))
    implementation(project(Modules.glideModule))
    implementation(project(Modules.reviewer))
    implementation(project(Modules.googleLogin))
    implementation(project(Modules.languageimp))
    implementation(project(Modules.notificationModule))
    implementation(project(Modules.adsModule))
    implementation(project(Modules.firebaseDB))
    implementation(project(Modules.exoplayerModule))
    implementation(project(Modules.radioOperationModule))
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
