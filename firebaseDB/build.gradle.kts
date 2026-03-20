plugins {
    alias(libs.plugins.kotlin.android)


}

android {
    namespace = "com.oyetech.firebaseDB"
    compileSdk = libs.versions.compile.sdk.get().toInt()

}

dependencies {
    implementation(project(Modules.domain))
    implementation(project(Modules.tools))
    implementation(project(Modules.models))
    implementation(project(Modules.languageModule))

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.timber)


}