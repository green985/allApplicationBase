plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.oyetech.radioservice"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(project(Modules.domain))
    implementation(project(Modules.models))
    implementation(project(Modules.tools))
    implementation(libs.mediaX)
    implementation(libs.timber)
}