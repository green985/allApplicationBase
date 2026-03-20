plugins {
    alias(libs.plugins.kotlin.android)


}

android {
    namespace = "com.oyetech.repository"
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data:remote"))
    implementation(project(":data:local"))
    implementation(project(":data:models"))
    implementation(project(":subImpl:tools"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)
    implementation(libs.retrofit)
    implementation(libs.androidx.annotation.jvm)

}
