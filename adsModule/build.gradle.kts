plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.oyetech.adsModule"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data:models"))
    implementation(project(":languageModule"))
    implementation(project(":subImpl:tools"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.kotlin.stdlib)
    implementation(libs.timber)
    api(libs.koin.core)
    implementation(libs.play.services.ads.api)

    detektPlugins(libs.detekt.formatting)
}
