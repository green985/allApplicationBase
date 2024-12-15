


object FirebaseLibrary {
    const val firebaseBom = "com.google.firebase:firebase-bom:33.7.0"
    const val analytics = "com.google.firebase:firebase-analytics-ktx"
    const val admob = "com.google.android.gms:play-services-ads:${Versions.admob}"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val googleServices = "com.google.gms:google-services"
    const val performance = "com.google.firebase:firebase-perf-ktx"
    const val firestoreDB = "com.google.firebase:firebase-firestore-ktx"
    const val messaging = "com.google.firebase:firebase-messaging-ktx"
    const val googleAuth = "com.google.android.gms:play-services-auth:${Versions.googleAuth}"
    const val googleAuthBOM = "com.google.android.gms:play-services-auth"
    const val firebaseAuthBom = "com.google.firebase:firebase-auth"
    const val authKTX = "com.google.firebase:firebase-auth-ktx:${Versions.authKTX}"
    const val locationn = "com.google.android.gms:play-services-location:${Versions.location}"
    const val review = "com.google.android.play:review-ktx:${Versions.review}"
    const val googleBilling = "com.android.billingclient:billing-ktx:${Versions.googleBilling}"
    const val googleMaps = "com.google.android.gms:play-services-maps:${Versions.googleMaps}"
}

object Runner {
    // const val testRunner = "com.oyetech.multimoduleapp.di.UiRunner"
    const val testRunner = "androidx.test.runner.AndroidJUnitRunner"
}

object Versions {
    const val splashscreen: String = "1.1.0-rc01"
    const val balloon = "1.6.3"
    const val kotlin = "2.0.20"
    const val kotlinReflect = "2.0.20"
    const val gradle = "4.1.0"
    const val minSdk = 24
    const val targetSdk = 34
    const val compileSdk = 34
    const val appCompat = "1.2.0"
    const val coreKtx = "1.2.0"
    const val constraintLayout = "1.1.3"
    const val junit = "4.13.2"
    const val androidTestRunner = "1.2.0"
    const val espressoCore = "3.4.0"
    const val vDexMaker = "2.28.1"
    const val dexOpener = "2.0.5"
    const val retrofit = "2.9.0"
    const val retrofitCoroutines = "0.9.2"
    const val retrofitGson = "2.9.0"
    const val gson = "2.8.6"
    const val okHttp = "4.9.3"
    const val coroutines = "1.6.3"
    const val koin = "3.5.0"
    const val signalR = "6.0.2"
    const val lifecycle = "2.7.0"
    const val nav = "2.6.0"
    const val room = "2.2.4"
    const val recyclerview = "1.2.1"
    const val coil = "1.1.1"
    const val swipeRefreshh = "1.1.0"
    const val material = "1.12.0-alpha03"
    const val dataStore = "1.0.0"
    const val daggerVersion = "2.41"
    const val mockwebserver = "2.7.5"
    const val archCoreTest = "2.1.0"
    const val androidJunit = "1.1.3"
    const val mockk = "1.12.2"
    const val fragmentTest = "1.2.5"
    const val databinding = "3.6.1"
    const val testCore = "1.4.0"
    const val timber = "4.7.1"
    const val paging = "3.1.1"
    const val moshi = "1.13.0"
    const val moshiConverter = "2.9.0"
    const val glide = "4.11.0"
    const val emoji = "0.10.0"
    const val dotIndicator = "3.2.0"
    const val circleIndicator = "2.1.6"
    const val easyImage = "3.2.0"
    const val stfalcon = "1.0.1"
    const val exoPlayer = "2.18.1"
    const val location = "21.0.1"
    const val googleAuth = "23.0.0"
    const val authKTX: String = "23.0.0"
    const val admob = "23.4.0"
    const val betterLinkMovement = "2.2.0"
    const val rangeBar = "1.0.0"
    const val shimmer = "0.5.0"
    const val workVersion = "2.7.1"
    const val appodeal = "3.0.0.+"
    const val review = "2.0.0"
    const val googleBilling = "5.0.0"
    const val googleMaps: String = "18.1.0"
    const val osmdroidMap: String = "6.1.17"
    const val mediaX = "1.6.0"

    const val composeCompiler = "1.5.3"
    const val composeBom = "2024.09.02"
    const val activityCompose = "1.9.2"
    const val material3 = "1.3.0" // En güncel Material3 versiyonu
}

object Libraries {

    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeNavigation = "androidx.navigation:navigation-compose${Versions.nav}"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val material3 = "androidx.compose.material3:material3:${Versions.material3}"
    const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"

    // Testing Libraries for Compose
    const val composeUiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeUiTestManifest = "androidx.compose.ui:ui-test-manifest"
    const val koinCompose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"




    const val shimmer = "com.facebook.shimmer:shimmer:${Versions.shimmer}"

    const val appodeal = "com.appodeal.ads:sdk:${Versions.appodeal}"

    const val workVersion = "androidx.work:work-runtime-ktx:${Versions.workVersion}"

    const val osmdroidMap = "org.osmdroid:osmdroid-android:${Versions.osmdroidMap}"
    const val mediaX = "androidx.media:media:${Versions.mediaX}"
    const val splashscreen = "androidx.core:core-splashscreen:${Versions.splashscreen}"

    // KOIN
    const val koin = "io.insert-koin:koin-android:${Versions.koin}"
    const val detekt = "io.gitlab.arturbosch.detekt:detekt-formatting:1.20.0-RC2"
    const val detektCli = "io.gitlab.arturbosch.detekt:detekt-cli:1.20.0-RC2"
    const val koinCore = "io.insert-koin:koin-core:${Versions.koin}"
    const val koinViewModel = "org.koin:koin-android-viewmodel:${Versions.koin}"
    const val signalR = "com.microsoft.signalr:signalr:${Versions.signalR}"
    const val stfalcon = "com.github.stfalcon:stfalcon-imageviewer:${Versions.stfalcon}"
    const val betterLinkMovement =
        "me.saket:better-link-movement-method:${Versions.betterLinkMovement}"
    const val rangeBar = "com.edmodo:rangebar:${Versions.rangeBar}"

    // ROOM
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomRunTime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"

    // RETROFIT
    const val retrofitCoroutineAdapter =
        "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofitCoroutines}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGsonConverter =
        "com.squareup.retrofit2:converter-gson:${Versions.retrofitGson}"
    const val retrofitMoshiConverter =
        "com.squareup.retrofit2:converter-moshi:${Versions.moshiConverter}"
    const val retrofitMoshiConverterKapt =
        "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"

    const val httpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"

    // COIL
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val facebook = "com.facebook.android:facebook-login:latest.release"

    const val daggerr = "com.google.dagger:dagger-android:${Versions.daggerVersion}"
    const val daggerSupport = "com.google.dagger:dagger-android-support:${Versions.daggerVersion}"
    const val daggerProcessor =
        "com.google.dagger:dagger-android-processor:${Versions.daggerVersion}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.daggerVersion}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val emoji = "com.vanniktech:emoji-google:${Versions.emoji}"
    const val dotIndicator = "com.matthew-tamlin:sliding-intro-screen:${Versions.dotIndicator}"
    const val circleIndicator = "me.relex:circleindicator:${Versions.circleIndicator}"
    const val glideComplier = "com.github.bumptech.glide:compiler:${Versions.glide}"
    const val glideAnnotation = "com.github.bumptech.glide:annotations:${Versions.glide}"
    const val glideIntegration = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide}"

    const val easyImage = "com.github.jkwiecien:EasyImage:${Versions.easyImage}"
    const val exoPlayer = "com.google.android.exoplayer:exoplayer:${Versions.exoPlayer}"

    const val balloon = "com.github.skydoves:balloon:${Versions.balloon}"
}

object KotlinLibraries {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val kotlinReflection = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinReflect}"
    const val kotlinCoroutineCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
}

object AndroidLibraries {
    // KOTLIN
    const val kotlinCoroutineAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    // ANDROID
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val lifecycleViewModel =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
    const val navigation = "androidx.navigation:navigation-ui-ktx:${Versions.nav}"
    const val navigationFrag = "androidx.navigation:navigation-fragment-ktx:${Versions.nav}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val dataStore = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
    const val swipeRefresh =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshh}"
    const val paging = "androidx.paging:paging-runtime:${Versions.paging}"
}

object TestLibraries {
    // ANDROID TEST
    const val androidTestRunner = "androidx.test:runner:${Versions.androidTestRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espressoCore}"
    const val testCore = "androidx.test:core:${Versions.testCore}"
    const val archCoreTest = "androidx.arch.core:core-testing:${Versions.archCoreTest}"
    const val junit = "androidx.test.ext:junit:${Versions.androidJunit}"
    const val fragmentNav = "androidx.fragment:fragment-testing:${Versions.fragmentTest}"

    // KOIN
    const val koin = "io.insert-koin:koin-test:${Versions.koin}"

    // MOCK WEBSERVER
    const val mockWebServer = "com.squareup.okhttp:mockwebserver:${Versions.mockwebserver}"

    // MOCK
    const val mockkAndroid = "io.mockk:mockk-android:${Versions.mockk}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"

    // COROUTINE
    const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"

    // DATA BINDING
    const val databinding = "androidx.databinding:databinding-compiler:${Versions.databinding}"
    const val espressoIdling =
        "androidx.test.espresso:espresso-idling-resource:${Versions.espressoCore}"
    const val dexOpener = "com.github.tmurakami:dexopener:${Versions.dexOpener}"
}
