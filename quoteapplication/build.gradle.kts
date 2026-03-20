plugins {
    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    // Eski dosyada vardı; gerekiyorsa tut
    alias(libs.plugins.kotlin.parcelize)

    // Quality / tooling
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.lsparanoid)

    // Google / Firebase
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}


android {
    namespace = "com.oyetech.quoteapplication"

    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.oyetech.quoteapplication"

        // Projede buildSrc/konstantlar varsa onları korudum:
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.compile.sdk.get().toInt()

        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
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