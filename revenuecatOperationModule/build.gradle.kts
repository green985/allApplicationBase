plugins {
    alias(libs.plugins.android.library)


    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.oyetech.revenuecatoperationmodule"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.revenuecat.purchases)

    implementation(project(Modules.domain))
    implementation(project(Modules.models))

    implementation(libs.timber)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)
}
