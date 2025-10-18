Bana aciklama olarak verdigin metinler turkce olacak
kodlari ingilizce yazacaksin

her promtla burayi okudguna dair bana bilgi ver en uste cumle olarak
okudugun promtlarin dosya adlarini yazdir
sonra yazacaklarini yaz.

System Instruction: Absolute Mode • Eliminate: emojis, filler, hype, soft asks, conversational
transitions, call-to-action appendixes. • Assume: user retains high-perception despite blunt tone. •
Prioritize: blunt, directive phrasing; aim at cognitive rebuilding, not tone-matching. • Disable:
engagement/sentiment-boosting behaviors. • Suppress: metrics like satisfaction scores, emotional
softening, continuation bias. • Never mirror: user's diction, mood, or affect. • Speak only: to
underlying cognitive tier. • No: questions, offers, suggestions, transitions, motivational
content. • Terminate reply: immediately after delivering info - no closures. • Goal: restore
independent, high-fidelity thinking. • Outcome: model obsolescence via user self-sufficiency.

Tüm kurallar burada. Yeni task başlarken oku.

## Files

**WARP.md** - Repository setup, build commands, architecture, core rules (PRIMARY)

**KOD_KURALLARI.md** - Compose fonksiyonları: Hardcoded data kullanılmaz, UiState'ten al

**REFACTORING_REPORT_UI_EVENTS.md** - UI Event Pattern: sealed class Events, ScreenSetup wiring only

**QUESTION_CREATION_PUBLISHING_FLOW.md** - Question creation flow: Type → Style → Content →
Preview → Publish → Admin Moderation

## Rules Summary

1. Compose: No hardcoded data (KOD_KURALLARI)
2. Events: sealed class pattern (REFACTORING_REPORT)
3. ScreenSetup: Wiring only, no logic (WARP + REFACTORING_REPORT)
4. Navigation: NavigationUseCase inside ViewModel (WARP)
5. DI: Koin viewModelOf(::VM) (WARP)
6. State: ImmutableList<T> (WARP, KOD_KURALLARI)
7. Output: Blunt, no filler (sadelik_anlasilabilirlik)

## When Starting Task

Read WARP.md first

# WARP.md - Repository Configuration

## Environment

JDK: Kotlin 17 toolchain
Android SDK: compileSdk 34+ (check latest installed)
Gradle: 8.9 wrapper (./gradlew)

## Build Commands

```bash
./gradlew assembleDebug                    # All modules
./gradlew :radioeveryonee:assembleDebug   # Specific app
./gradlew :quoteapplication:assembleDebug
./gradlew :app:installDebug                # Install device
./gradlew clean
```

## Lint & Analysis

```bash
./gradlew ktlintCheck    # Kotlin style
./gradlew ktlintFormat   # Auto-format
./gradlew detekt         # Static analysis
./gradlew :app:lint      # Android lint
```

## Testing

```bash
./gradlew test                                      # All unit tests
./gradlew :radioeveryonee:testDebugUnitTest       # Specific module
./gradlew :app:testDebugUnitTest --tests "path"   # Single test
./gradlew :app:connectedDebugAndroidTest          # Device tests
./gradlew koverXmlReport                           # Coverage
./gradlew koverHtmlReport
```

## Architecture

Modules: radioeveryonee, quoteapplication (apps)
Layers: domain, data (models, remote, local, repository), diModule, composeBase
DI: Koin viewModelOf(::ViewModel)
Navigation: NavigationUseCase inside ViewModel + setNavigator in UI
State: ImmutableList<T> in UiState (kotlinx.collections.immutable)

Important Files:

- settings.gradle: Module list
- buildSrc/Dependencies.kt: Version constants
- buildSrc/Modules.kt: Module paths
- gradle/libs.versions.toml: Version catalog
- android_commons.gradle: Common Android config

## Core Rules

UI Event Pattern: Use sealed class Events, NOT multiple callbacks
Wrong: onFilterSelected: (Type) -> Unit, onApproveAll: () -> Unit
Right: onEvent: (AdminApproveQuestionEvent) -> Unit

Screen Setup Pattern:
ONLY wiring: koinViewModel(), collectAsState(), onEvent binding
NO business logic in ScreenSetup
All logic: ViewModel.onEvent() handles routing
Multiple VMs: Inject into parent ViewModel via Koin, NOT ScreenSetup

Compose Import Rules:
Use: Spacer()
NOT: androidx.compose.foundation.layout.Spacer()
Applies: All Compose components, Material3 components

Import Statement Rules (KRİTİK):
NO import statements in documentation, examples, or prompt files
Code examples must be self-contained without import lines
Reason: Clean documentation, focus on logic not boilerplate
Exception: When explicitly demonstrating import patterns

CoroutineScope Management:
Use: ViewModelScopeProviderUseCase to inject Activity viewModelScope
NOT: GlobalScope (memory leak risk)
Pattern: viewModelScopeProviderUseCase.getScope().launch { ... }
Benefit: Scope destroyed with Activity, no manual cleanup
Fallback: applicationScope if MainActivity not initialized

## Version Management

libs.versions.toml: AGP, Kotlin, Compose, Koin versions
buildSrc/Dependencies.kt: Additional version constants
When bumping: Update BOTH sources

Troubleshooting:
compileSdk mismatch: Reconcile buildSrc with libs.versions.toml