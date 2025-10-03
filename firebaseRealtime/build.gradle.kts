plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.oyetech.firebaserealtime"
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk

        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(AndroidLibraries.coreKtx)

    implementation(project(Modules.domain))
    implementation(project(Modules.model))
    implementation(project(Modules.languageModule))
    implementation(project(Modules.tools))


    implementation(platform(FirebaseLibrary.firebaseBom))
    implementation("com.google.firebase:firebase-database")

    implementation(Libraries.timber)
    api(Libraries.koin)
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:${Versions.koin}"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-androidx-compose-navigation")
}