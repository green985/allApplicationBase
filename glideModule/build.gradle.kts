plugins {
    alias(libs.plugins.android.library)

}

android {
    namespace = "com.oyetech.glideModule"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(libs.glide)
//    ksp(libs.glide.compiler) // Using KSP for annotation processing
    implementation(libs.glide.annotations)
    implementation(libs.glide.okhttp3)

    implementation(libs.timber)
    implementation(project(Modules.domain))
    implementation(project(Modules.tools))
}