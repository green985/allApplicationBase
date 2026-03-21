plugins {
    alias(libs.plugins.android.library)


}

android {
    namespace = "com.oyetech.radioservice"
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data:models"))
    implementation(project(":subImpl:tools"))
    implementation(libs.mediaX)
    implementation(libs.timber)

}