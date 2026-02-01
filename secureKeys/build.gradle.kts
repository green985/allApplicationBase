plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.lsposed.lsparanoid")
}

android {
    namespace = "com.oyetech.secureKeys"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.annotation.jvm)
}
