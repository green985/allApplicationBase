plugins {
    alias(libs.plugins.android.library)


}

android {
    namespace = "com.oyetech.firebaseDB"
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(project(":domain"))
    implementation(project(":subImpl:tools"))
    implementation(project(":data:models"))
    implementation(project(":languageModule"))

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.timber)


}