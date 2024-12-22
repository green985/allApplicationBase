plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.oyetech.composebase"
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xcontext-receivers"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        disable.add("UnusedMaterial3ScaffoldPaddingParameter")
    }
}

dependencies {

    implementation(AndroidLibraries.coreKtx)

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(Libraries.lifecycleRuntimeKtx)
    implementation(Libraries.activityCompose)

    // Compose dependencies
    implementation(platform(Libraries.composeBom))
    implementation(Libraries.composeUi)
    implementation(Libraries.composeUiGraphics)
    implementation(Libraries.composeUiToolingPreview)
    debugImplementation(Libraries.composeUiTooling)
    debugImplementation(Libraries.composeUiTestManifest)
    implementation(Libraries.material3)


    implementation(project(":core"))

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(Libraries.composeUiTestJunit4)
    // Debug dependencies

    implementation(project(Modules.domain))
    implementation(project(Modules.glideModule))
    implementation(project(Modules.model))
    implementation(project(Modules.radioService))
    implementation(project(Modules.languageModule))

    implementation(Libraries.timber)
    api(Libraries.koin)

    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.8")

    implementation("androidx.compose.runtime:runtime:1.7.6")

    implementation("androidx.navigation:navigation-compose:2.8.1")
    implementation("androidx.compose.foundation:foundation:1.7.5")
    implementation("androidx.navigation:navigation-runtime-ktx:2.8.1")
    implementation(Libraries.koinCompose)
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:${Versions.koin}"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-androidx-compose-navigation")

    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")

}