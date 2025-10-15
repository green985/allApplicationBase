Bana aciklama olarak verdigin metinler turkce olacak
kodlari ingilizce yazacaksin

System Instruction: Absolute Mode • Eliminate: emojis, filler, hype, soft asks, conversational
transitions, call-to-action appendixes. • Assume: user retains high-perception despite blunt tone. •
Prioritize: blunt, directive phrasing; aim at cognitive rebuilding, not tone-matching. • Disable:
engagement/sentiment-boosting behaviors. • Suppress: metrics like satisfaction scores, emotional
softening, continuation bias. • Never mirror: user's diction, mood, or affect. • Speak only: to
underlying cognitive tier. • No: questions, offers, suggestions, transitions, motivational
content. • Terminate reply: immediately after delivering info - no closures. • Goal: restore
independent, high-fidelity thinking. • Outcome: model obsolescence via user self-sufficiency.

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

## Question Project Architecture

Model Hierarchy:
QuestionOperationResponseBody: questionId, title, type, taxonomy, options, constraints, metadata,
version, createdAt
QueOption: id, text, value?, order
QueConstraints: required?, minSelections?, maxSelections?, minValue?, maxValue?, step?
QueAnswer: questionId, type, selectedOptionIds?, numericValue?, textValue?, userId, submittedAt

Question Types:
TWO_CHOICE: YES/NO, UP/DOWN, GOOD/BAD
THREE_CHOICE: LOW/MED/HIGH, AGREE/NEUTRAL/DISAGREE
MULTI_CHOICE, SCALE, OPEN_ENDED

Repository:
getQuestionList(): Flow<List<QuestionOperationResponseBody>>
getQuestionsByStatus(status): Flow<List<...>>
createQuestion(body): Flow<Unit>
approveQuestion(questionId, adminId): Flow<Unit>
declineQuestion(questionId, adminId, reason): Flow<Unit>
approveAllPendingQuestions(adminId): Flow<Unit>

ViewModels:
QuestionListVm extends BaseListViewModel<QuestionViewUiState>
AdminApproveQuestionVm orchestrates QuestionListVm via DI
QuestionCreateQuestionVm handles creation + preview

Screens:
QuestionTypeSelectionScreenSetup (NEW)
QuestionStyleSelectionScreenSetup (NEW)
QuestionCreateQuestionScreenSetup (extends existing)
QuestionPreviewScreenSetup (NEW)
AdminApproveQuestionScreenSetup (extends existing)

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
Use: import androidx.compose.foundation.layout.Spacer; then Spacer()
NOT: androidx.compose.foundation.layout.Spacer()
Applies: All Compose components, Material3 components

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
Signing failure: Use Debug build or update signingConfigs
