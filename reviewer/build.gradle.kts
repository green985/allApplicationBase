plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.oyetech.reviewer"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data:models"))
    implementation(libs.play.review.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)
}

