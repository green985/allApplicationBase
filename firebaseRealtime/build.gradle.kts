plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.oyetech.firebaserealtime"
    compileSdk = libs.versions.compile.sdk.get().toInt()


}

dependencies {
    implementation(AndroidLibraries.coreKtx)

    implementation(project(Modules.domain))
    implementation(project(Modules.models))
    implementation(project(Modules.languageModule))
    implementation(project(Modules.tools))


    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-database")

    implementation(Libraries.timber)
    api(Libraries.koin)
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:${Versions.koin}"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-androidx-compose-navigation")
}