plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {

    namespace = "com.oyetech"

    defaultConfig {
        applicationId = "com.oyetech.quoteapplication"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.compile.sdk.get().toInt()

        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }

    compileSdk = libs.versions.compile.sdk.get().toInt()

    buildFeatures {
        compose = true
    }
    lint {
        disable.add("UnusedMaterial3ScaffoldPaddingParameter")
    }

}

dependencies {

    implementation(project(":diModule"))
    implementation(project(":domain"))
    implementation(project(":composeBase"))
    implementation(project(":data:repository"))
    implementation(project(":data:local"))
    implementation(project(":subImpl:languageImp"))
    implementation(project(":subImpl:tools"))

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)

    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)


    implementation(platform(libs.compose.bom))
    implementation(platform(libs.koin.bom))
    implementation(libs.androidx.activity.ktx)

    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.core.splashscreen)
    implementation(libs.play.services.wearable)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.androides.ui.tooling)


    implementation(libs.timber)
}
