plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.oyetech.exoplayermodule"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(libs.exoplayer)
    implementation(project(":domain"))
    implementation(project(":data:models"))
    implementation(project(":languageModule"))
    implementation(project(":subImpl:tools"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.timber)
    api(libs.koin.core)
    detektPlugins(libs.detekt.formatting)
}
