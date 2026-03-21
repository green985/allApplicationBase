plugins {
    alias(libs.plugins.android.library)


}

android {
    namespace = "com.oyetech.notificationmodule"

    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()

        consumerProguardFiles("consumer-rules.pro")
    }

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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.inappmessaging)

}