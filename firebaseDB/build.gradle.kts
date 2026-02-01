plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.oyetech.firebaseDB"
    compileSdk = libs.versions.compile.sdk.get().toInt()
}

dependencies {
    implementation(project(Modules.domain))

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
}