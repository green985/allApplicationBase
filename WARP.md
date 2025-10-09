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

Genel mimari kararlar ve kurallar
• Navigasyon: ViewModel içinde NavigationUseCase kullanımı ve navigationUseCase.setNavigator +
navController.navigate/back deseni korundu.
• DI: Koin ile viewModelOf(::YourVm) kullanımı. Firebase repo’ları da Koin modülüne eklendi.
• İsimlendirme: QuestionApp* isimlendirme ve proje/dosya konumlandırmaları korundu.
• UI State: UI katmanında ImmutableList kullanımı tercih edildi (kotlinx.collections.immutable).

Ekranlar ve Navigasyon
• AdminApproveQuestion:
◦ AdminApproveQuestionScreenSetup ve AdminApproveQuestionVm oluşturuldu.
◦ QuestionAppProjectRoutes altında AdminApproveQuestion route eklendi ve QuestionAppNavigation
içinde composable olarak bağlandı.
◦ FacSettings ekranına debug-only menü girdisi eklendi (BuildConfig.DEBUG → true ise görünür),
NavigationUseCase ile admin ekranına yönlendirme yapıyor.
• QuestionList:
◦ QuestionListScreenSetup ve QuestionListVm eklendi.
◦ MessageConversationList pattern’ine uyumlu GenericListState yapısı kullanıldı (
dataFlow/refreshDataFlow).
◦ Liste elemanları QuestionYesNoView ile render ediliyor; olaylar generic olarak işleniyor.
◦ QuestionAppProjectRoutes.QuestionList route eklendi ve NavGraph’a bağlandı.

QuestionYesNoView ve UI Event yapısı
• Soru görünümü:
◦ Başlangıçta Yes/No butonları ve Title edit mantığı vardı; create modunda sadece title
düzenlenebilir, cevaplama modunda Yes/No gösterilir.
◦ Daha sonra generic seçeneğe evrildi: QueOption’lar UIState’e eklendi ve butonlar options üzerinden
oluşturuluyor (dolu değilse YES/NO fallback).
• Event yapısı:
◦ YesClicked/NoClicked kaldırıldı.
◦ Yerine generic QuestionViewEvent.OptionSelected(optionId: String) eklendi.
◦ TitleChanged, SubmitClicked, CancelClicked, OnErrorDismiss korundu.
• UI State:
◦ QuestionViewUiState.options ImmutableList<QueOption> oldu.
◦ Backend modelden UI’a dönüşte options toImmutableList() ile map’leniyor.

Listeleme mimarisi (GenericListState uyumu)
• QuestionListVm, BaseListViewModel<QuestionViewUiState>’ten türetildi.
• listViewState: GenericListState<QuestionViewUiState>
◦ dataFlow/refreshDataFlow: repository.getQuestionList() → List<QuestionOperationResponseBody> →
map → List<QuestionViewUiState>.
• QuestionListScreen:
◦ listViewState.items üzerinden LazyColumn render.
◦ QuestionYesNoView’dan gelen OptionSelected, QuestionListEvent.OnOptionSelected(itemUi, optionId)
olarak VM’e iletiliyor.

Domain ve veri modeli (genişlemeye hazır)
• QuestionOperationResponseBody genişletildi:
◦ payload: QuestionPayload? = null (ileride tip özel konfig için)
◦ options: List<QueOption> = []
◦ constraints: QueConstraints? = null
◦ metadata: Map<String, String> = emptyMap()
◦ version: Int = 1
• Yeni modeller:
◦ QueOption(id, text, value?, order)
◦ QueConstraints(required?, minSelections?, maxSelections?, minValue?, maxValue?, step?)
◦ QueAnswer(questionId, type, selectedOptionIds?, numericValue?, textValue?, userId, submittedAt) —
şimdilik kullanılmıyor
◦ QuestionPayload (placeholder)
• Mapping:
◦ toOperationBody(): YES/NO için iki QueOption (YES, NO) ve QueConstraints(required=true,
minSelections=1, maxSelections=1) ekleniyor.

Repository ve Firestore
• Domain arayüz: FirebaseQuestionOperationRepository
◦ createQuestion(body): Flow<Unit> — hata durumunda exception fırlatıyor.
◦ getQuestionList(): Flow<List<QuestionOperationResponseBody>> eklendi.
• Firebase implemantasyonu:
◦ createQuestion Firestore transaction ile yazıyor; hata durumunda GeneralException.
◦ getQuestionList createdAt’e göre DESC sıralı liste döndürüyor.
• DI: FirebaseDBModule içinde FirebaseQuestionOperationRepositoryImpl binding eklendi.

Refactorlar ve iyileştirmeler
• QuestionAppDebugRoot TODO çözümü: GlobalScope yerine rememberCoroutineScope kullanıldı (
composition-scoped).
• QuestionCreateQuestionVm:
◦ Repository injection ile createQuestion çağrısı yapıyor.
◦ CancelClicked → navigate("back")
◦ Snackbar success/error örnekleri eklendi (snackbarDelegate).
• Navigation/DI kuralları ve naming konvansiyonları korunuyor.

Gelecek adımlar (taslak/hatırlatma)
• Renderer registry: QuestionKind → Composable eşlemesi (henüz yazılmadı).
• Validasyon ve iş kuralları: tip bazlı validator stratejileri (min/max selection, scale aralıkları
vs) — sonra yapılacak.
• Soru akışı (Flow), geçişler ve branching: RuleEngine/DSL — sonra yapılacak.
• Yeni tipler: SingleChoice/MultiChoice/Scale/Likert/Matrix — payload ve renderer ile eklenecek.
• UI’nın options’ı birincil bilgi kaynağı olarak tüketmesini standartlaştırma (YES/NO fallback
kaldırılabilir).

Öğrenilenler ve prensipler
• UI State'te ImmutableList kullanmak stabil ve predictable bir render akışı sağlıyor.
• Event'leri generic (OptionSelected) tutmak, yeni tipleri eklerken UI'ı büyütmeden genişlemeyi
kolaylaştırıyor.
• Repository tarafında Flow<Unit> + exception stratejisi, hata yönetiminde yalın ve net bir yapı
sunuyor.
• NavigationUseCase ViewModel'de tutulduğunda test ve soyutlama avantajı sağlıyor; UI tarafında
navController ile bağlama setNavigator ile yapılmalı.
• Koin viewModelOf kullanımı ile VM bağımlılık zinciri basit ve izlenebilir kalıyor.

Kod yazım kuralları
• Compose kodunda tam paket yolları (fully qualified names) kullanılmamalı:
◦ YANLIŞ: androidx.compose.foundation.layout.Spacer, androidx.compose.material3.Button
◦ DOĞRU: import ile eklenip direkt Spacer, Button kullanılmalı
◦ Bu kural tüm Compose bileşenleri ve Material3 component'leri için geçerlidir.
