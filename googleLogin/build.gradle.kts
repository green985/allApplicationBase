plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.oyetech.googlelogin"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data:models"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.credentials)
    implementation(libs.timber)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
}

