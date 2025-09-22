plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

android {
    namespace = "com.oyetech.composebase"
    compileSdk = Versions.compileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"

    }
    buildFeatures {
        compose = true
    }
    lint {
        disable.add("UnusedMaterial3ScaffoldPaddingParameter")
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation(platform("io.insert-koin:koin-bom:3.5.0"))

    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.media:media:1.7.1")

    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.runtime:runtime:1.7.7")
    implementation("androidx.compose.foundation:foundation:1.7.7")
    implementation("androidx.paging:paging-compose:3.3.5")
    implementation("androidx.navigation:navigation-compose:2.8.6")
    implementation("androidx.navigation:navigation-runtime-ktx:2.8.6")

    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-androidx-compose-navigation")

    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation(project(":subImpl:tools"))
    implementation(project(":subImpl:denemeModule"))
    implementation(project(Modules.domain))
    implementation(project(Modules.glideModule))
    implementation(project(Modules.model))
    implementation(project(Modules.radioService))
    implementation(project(Modules.languageModule))



}