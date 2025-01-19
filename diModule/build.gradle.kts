plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.oyetech.dimodule"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}


dependencies {
    // Project Modules
    implementation(project(Modules.base))
    implementation(project(Modules.domain))
    // implementation(project(Modules.navigation))
    implementation(project(Modules.core))
    implementation(project(Modules.remote))
    implementation(project(Modules.local))
    implementation(project(Modules.repository))
    implementation(project(Modules.composeBase))
    implementation(project(Modules.radioService))
    implementation(project(Modules.glideModule))
    implementation(project(Modules.reviewer))
    implementation(project(Modules.googleLogin))
    implementation(project(Modules.languageimp))

    // Firebase Libraries
    implementation(platform(FirebaseLibrary.firebaseBom))
    implementation(FirebaseLibrary.performance)
    // implementation(FirebaseLibrary.messaging)
    implementation(FirebaseLibrary.crashlytics)
    implementation(FirebaseLibrary.analytics)
    // implementation(project(Modules.reviewer))

    // Additional Modules
    // implementation(project(Modules.remote))
    // implementation(project(Modules.notifications))
    // implementation(project(Modules.loginRegister))
    // implementation(project(Modules.repository))
    // implementation(project(Modules.profile))
    // implementation(project(Modules.editProfileInfo))
    // implementation(project(Modules.connectList))
    // implementation(project(Modules.languageSelect))
    // implementation(project(Modules.splash))
    // implementation(project(Modules.appBrowser))
    // implementation(project(Modules.filter))
    // implementation(project(Modules.settings))

    // Google Specific Libraries
    // implementation(project(Modules.analyticsModule))
    // implementation(project(Modules.reviewer))
    // implementation(project(Modules.paymentModule))
    // implementation(FirebaseLibrary.googleBilling)
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

//    ksp(Libraries.retrofitMoshiConverterKapt)
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
