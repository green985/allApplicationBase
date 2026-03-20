plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)

}

android {
    namespace = "com.oyetech.domain"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    buildFeatures {
        dataBinding = true
        buildConfig = true
        viewBinding = true
    }

}

dependencies {
    api(project(":secureKeys"))
    implementation(project(":data:models"))
    implementation(project(":languageModule"))
    implementation(project(":subImpl:tools"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.timber)
    api(libs.koin.core)
    implementation(libs.androidx.activity.ktx)

    detektPlugins(libs.detekt.formatting)
}
