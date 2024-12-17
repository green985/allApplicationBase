plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.oyetech.revenuecatoperationmodule"
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xcontext-receivers"
    }

}

dependencies {

    implementation(KotlinLibraries.kotlin)
    implementation(AndroidLibraries.coreKtx)
    implementation("com.revenuecat.purchases:purchases:7.0.0")


    implementation(project(Modules.domain))
    implementation(project(Modules.model))
    implementation(Libraries.timber)
    api(Libraries.koin)

    implementation(project.dependencies.platform("io.insert-koin:koin-bom:${Versions.koin}"))
    implementation("io.insert-koin:koin-core")

}



