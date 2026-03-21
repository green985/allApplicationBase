plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.oyetech.radiooperationmodule"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data:models"))

    implementation(libs.androidx.core.ktx)
}

