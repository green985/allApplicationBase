plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.oyetech.dimodule"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}


dependencies {

    implementation(project(Modules.domain))
    // implementation(project(Modules.navigation))
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

    // Firebase Libraries
    implementation(platform(FirebaseLibrary.firebaseBom))
    implementation(FirebaseLibrary.performance)
    // implementation(FirebaseLibrary.messaging)
    implementation(FirebaseLibrary.crashlytics)
    implementation(FirebaseLibrary.analytics)

    implementation(project(Modules.adsModule))
    implementation(project(Modules.firebaseDB))
    implementation(project(Modules.exoplayerModule))
    implementation(project(Modules.radioOperationModule))

    // Retrofit Libraries
    implementation(Libraries.retrofitCoroutineAdapter)
    implementation(Libraries.gson)
    implementation(Libraries.moshi)
    implementation(Libraries.moshiKotlin)
    implementation(Libraries.retrofit)
    implementation(Libraries.retrofitGsonConverter)
    implementation(Libraries.retrofitMoshiConverter)
    implementation(Libraries.timber)
    implementation(project(":data:models"))
    implementation(project(":subImpl:tools"))
    implementation(project(":firebaseRealtime"))



    ksp(Libraries.retrofitMoshiConverterKapt)
    implementation(Libraries.httpLoggingInterceptor)

    // Gson and Moshi
    implementation(Libraries.gson)
    implementation(Libraries.moshi)
    implementation(Libraries.moshiKotlin)

    // Android Libraries
    implementation(AndroidLibraries.appCompat)
    implementation(AndroidLibraries.navigation)
    implementation(AndroidLibraries.navigationFrag)
    implementation(Libraries.splashscreen)

    implementation("androidx.compose.runtime:runtime:1.7.6")

    // Feature Modules (commented out for now)
    // implementation(project(Modules.featureFeedList))
    // implementation(project(Modules.messages))
    // implementation(project(Modules.imageViewer))
    // implementation(project(Modules.userNotification))
}
