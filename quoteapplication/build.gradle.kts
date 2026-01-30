@file:Suppress("UnstableApiUsage")

import jdk.tools.jlink.resources.plugins
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    // Eski dosyada vardı; gerekiyorsa tut
    kotlin("kapt")
    alias(libs.plugins.kotlin.parcelize)

    // Quality / tooling
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.lsparanoid)

    // Google / Firebase
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

private fun artifactName(versionName: String, versionCode: Int): String {
    val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    val versionSafe = versionName.replace(".", "_")
    val project = "quoteApplication"
    return "${project}_${versionSafe}_${versionCode}_$date"
}

android {
    namespace = "com.oyetech.quoteApplication"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.oyetech.quoteApplication"

        // Projede buildSrc/konstantlar varsa onları korudum:
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        versionCode = QuoteReleaseProperty.versionCode
        versionName = QuoteReleaseProperty.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    // Compose Compiler artık plugin ile yönetiliyor; yine de istersen option ekleyebilirsin.

    packaging {
        resources {
            excludes += setOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1"
            )
        }
    }

    lint {
        abortOnError = false
        checkAllWarnings = false
        checkReleaseBuilds = false
        ignoreWarnings = true
        quiet = true
    }

    signingConfigs {
        create("release") {
            // gradle.properties veya environment ile besle:
            // RELEASE_STORE_FILE=/path/to/keystore
            // RELEASE_STORE_PASSWORD=...
            // RELEASE_KEY_ALIAS=...
            // RELEASE_KEY_PASSWORD=...

            val storeFilePath = (findProperty("RELEASE_STORE_FILE") as String?)
                ?: System.getenv("RELEASE_STORE_FILE")
            val storePasswordValue = (findProperty("RELEASE_STORE_PASSWORD") as String?)
                ?: System.getenv("RELEASE_STORE_PASSWORD")
            val keyAliasValue =
                (findProperty("RELEASE_KEY_ALIAS") as String?) ?: System.getenv("RELEASE_KEY_ALIAS")
            val keyPasswordValue = (findProperty("RELEASE_KEY_PASSWORD") as String?)
                ?: System.getenv("RELEASE_KEY_PASSWORD")

            if (!storeFilePath.isNullOrBlank()) storeFile = file(storeFilePath)
            if (!storePasswordValue.isNullOrBlank()) storePassword = storePasswordValue
            if (!keyAliasValue.isNullOrBlank()) keyAlias = keyAliasValue
            if (!keyPasswordValue.isNullOrBlank()) keyPassword = keyPasswordValue
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")

            // Eski dosyadaki ndk debugSymbolLevel
            ndk {
                debugSymbolLevel = "FULL"
            }

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // APK adlandırma: modern varyant API
    androidComponents {
        onVariants(selector().all()) { variant ->
            val vName = variant.versionName.orNull ?: "0.0.0"
            val vCode = variant.versionCode.orNull ?: 1
            val name = artifactName(vName, vCode)

            variant.outputs.forEach { output ->
                // AGP 8.x: outputFileName property Android Gradle Plugin internal API olabilir.
                // Çalışıyorsa kullan, çalışmazsa bu blok kaldırılır ve CI tarafında artifact rename yapılır.
                output.outputFileName.set("$name.apk")
            }
        }
    }

    // Derleme hedefleri
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(Modules.diModule))
    implementation(project(Modules.composeBase))
}

ktlint {
    android.set(true)
    outputColorName.set("RED")

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}