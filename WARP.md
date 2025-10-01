# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

Repository overview

- Android multi-module project (Gradle 8.9, Android Gradle Plugin ~8.7, Kotlin toolchains at 17)
- Primary application modules: radioeveryonee and quoteapplication
- Layered architecture with domain and data modules (models, remote, local, repository) composed
  into apps via a central DI module
- Versions and plugin aliases are defined in both gradle/libs.versions.toml and buildSrc (
  Dependencies.kt, Modules.kt); some modules read from buildSrc Versions while root uses the version
  catalog. When bumping versions, check both sources

Environment prerequisites

- JDK: toolchain set to 17 project-wide
- Android SDK: compileSdk varies by module (buildSrc shows 34; some apps explicitly target 36). Use
  the latest installed to avoid sync errors
- Gradle wrapper: use ./gradlew from this repo (distributionUrl points to gradle-8.9)

Common commands

- Sync and build all modules (Debug)
  ```bash
  ./gradlew assembleDebug
  ```
- Build a specific app (Debug)
  ```bash
  ./gradlew :radioeveryonee:assembleDebug
  ./gradlew :quoteapplication:assembleDebug
  ```
- Install a Debug build on a connected device/emulator
  ```bash
  ./gradlew :radioeveryonee:installDebug
  ./gradlew :quoteapplication:installDebug
  ```
- Clean
  ```bash
  ./gradlew clean
  ```

Lint and static analysis

- Kotlin style (ktlint)
  ```bash
  ./gradlew ktlintCheck          # check only
  ./gradlew ktlintFormat         # auto-format
  ```
- Detekt (defaults; no custom config file detected)
  ```bash
  ./gradlew detekt
  ```
- Android Lint (per app)
  ```bash
  ./gradlew :radioeveryonee:lint
  ./gradlew :quoteapplication:lint
  ```

Testing

- Run all unit tests across modules
  ```bash
  ./gradlew test
  ```
- Module-specific unit tests (Debug)
  ```bash
  ./gradlew :radioeveryonee:testDebugUnitTest
  ./gradlew :quoteapplication:testDebugUnitTest
  ```
- Run a single test (example from radioeveryonee)
  ```bash
  ./gradlew :radioeveryonee:testDebugUnitTest --tests "com.oyetech.radioeveryonee.ExampleUnitTest"
  ```
- Instrumented tests on device/emulator (Debug)
  ```bash
  ./gradlew :radioeveryonee:connectedDebugAndroidTest
  ./gradlew :quoteapplication:connectedDebugAndroidTest
  ```

Coverage (if needed)

- Kover is applied at the root; typical reports
  ```bash
  ./gradlew koverXmlReport
  ./gradlew koverHtmlReport
  ```

High-level architecture

- Apps
    - radioeveryonee and quoteapplication are Android application modules that package features and
      shared libraries. Their build scripts enable Compose and/or Data/View Binding and integrate
      Crashlytics and Google services
    - Release signing is configured in each app’s Gradle file; on machines without the referenced
      keystore, prefer Debug builds or provide your own release signing config
- Dependency Injection and composition
    - diModule centralizes wiring of cross-cutting dependencies and feature modules. It depends on:
      domain, data layers (remote, local, repository), UI base (composeBase), media (radioService),
      auth (googleLogin), Firebase, Ads, ExoPlayer, notifications, and tool/sub-impl modules
    - Koin is used as the DI framework across modules (see android_commons.gradle and
      Dependencies.kt)
- Domain and data layers
    - domain holds core types and contracts (depends on secureKeys)
    - data is split into:
        - models: shared DTOs/entities
        - remote: Retrofit + Moshi/Gson + OkHttp for networking
        - local: Room and Lifecycle components for persistence
        - repository: orchestrates local/remote and exposes APIs to upper layers
- UI/Foundation
    - composeBase provides shared Compose scaffolding and UI dependencies used by apps and features
    - feature modules (e.g., wallpaperList, settingss, imageViewer) provide screen-level or
      domain-specific UI bundles that apps can include
- Media and services
    - radioService and audioPlayerHelper use androidx.media to support playback; exoplayerModule
      provides ExoPlayer integration
- Platform integrations
    - Firebase (analytics, crashlytics, performance), Google login, adsModule, notificationModule,
      languageModule and subImpls are modularized integrations, pulled together via diModule

Key build system notes

- Centralized Gradle config
    - android_commons.gradle applies common Android and lint configs to most library modules (
      Data/View Binding, ktlint/detekt, Java 8 bytecode, etc.)
    - Root build.gradle.kts enforces Kotlin JVM toolchain 17 for Android/JVM plugins and applies
      ktlint, detekt, and kover at the root
- Version management
    - gradle/libs.versions.toml defines AGP/Kotlin plugin aliases and many Compose/Koin libs used by
      the root and some modules
    - buildSrc/Dependencies.kt and buildSrc/Modules.kt contain additional versions and module
      coordinates used by many library modules

Troubleshooting tips specific to this repo

- If you see mismatched compileSdk or Kotlin version errors, reconcile buildSrc Versions with the
  version catalog (libs.versions.toml) or align module-level overrides
- If release builds fail due to signing, build Debug variants or update signingConfigs to use your
  local keystore via secure Gradle properties

Important files

- settings.gradle defines all included modules under the AllApplicationBase root
- buildSrc/src/main/kotlin/Modules.kt centralizes module paths used in Gradle scripts
- buildSrc/src/main/kotlin/Dependencies.kt provides dependency/version constants referenced by many
  modules
- gradle/libs.versions.toml contains the version catalog for plugins and libraries used at the root
  and in Compose-centric modules
