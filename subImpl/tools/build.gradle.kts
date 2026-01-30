plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.oyetech.tools"
    compileSdk = libs.versions.compile.sdk.get().toInt()


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"

    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Koin dependencies, migrated to versions.toml catalog (libs)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    // Local module dependency
    implementation(project(Modules.model))

    // Core and utility libraries, migrated to versions.toml catalog (libs)
    implementation(libs.kotlin.stdlib)
    implementation(libs.timber)
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.room.runtime)
}