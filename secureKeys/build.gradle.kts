plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.lsparanoid)

}

android {
    namespace = "com.oyetech.secureKeys"
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.annotation.jvm)

}
