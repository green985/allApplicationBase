# Android and Kotlin Multiplatform Architecture Rules

You are a code generation tool.

Follow the rules exactly.

Do not invent architecture.

Do not introduce patterns that are not defined here.

Before editing code, identify the target as one of these scopes:

- Existing Android application/library code
- KMP shared code
- KMP platform-specific code

Apply only the rules for that scope. Do not force Android-only conventions onto KMP code.

---

## Migration Boundary

This repository is adopting Kotlin Multiplatform incrementally.

- Preserve existing Android modules and their public APIs unless the task explicitly requests a
  migration or refactor.
- Do not move existing Android classes into KMP as a side effect of feature work.
- Do not convert an Android module to KMP unless explicitly requested.
- Do not make broad package, dependency, navigation, DI, model, or source-set changes to unify
  Android and KMP.
- New KMP work must be implemented separately from the legacy Android implementation when reuse
  would require a large refactor.
- Temporary duplication between Android and KMP is acceptable when it keeps the change small and
  isolates migration risk.
- Reuse code only when it is already platform-neutral and can be shared without changing Android
  behavior.
- When the requested platform is unclear, infer it from the file/module in scope. Ask only if both
  implementations would be equally plausible and materially different.

---

## Module Ownership

- `quoteapplication`, `wearModule`, `composeBase`, `domain`, `data`, and the existing feature/helper
  modules are Android-first legacy code.
- `kmpFeatures` owns new KMP feature implementations and application flow.
- `viewModule` owns reusable KMP Compose UI primitives and theming.
- `kmpModels` owns platform-neutral models shared by KMP features and Android modules.
- Put shared KMP code in `commonMain`.
- Put Android-specific KMP integrations in `androidMain`.
- Put Wasm-specific integrations in `wasmJsMain`.
- Add another target/source set only when the task explicitly requires that platform.
- KMP modules must not depend on Android-first modules merely to reuse an existing implementation.
- Android-first modules must not be reorganized to depend on KMP modules unless explicitly
  requested.

---

## KMP Model Ownership

Keep shared model types in `kmpModels/src/commonMain`. Organize them by responsibility:

- `response`: remote/API response DTOs
- `postbody`: request body DTOs used by POST, PATCH, or similar write operations
- `entity`: application/domain entities and shared enums or value types
- `ui/state`: feature UI state models
- `ui/event`: feature UI event types

Rules:

- Package and directory names use lowercase Kotlin naming; use `postbody` even when the concept is
  written as “post body”.
- `kmpModels` must stay platform-neutral and must not depend on Android SDK, Compose UI, navigation,
  ViewModels, repositories, or platform-specific resources.
- Add `@Serializable` only to wire/request/response models that are actually serialized. Do not
  add Android `@Keep` to `commonMain` models.
- Entities must not double as response or post-body DTOs when their wire shapes differ.
- UI state and event types may depend on shared entities, but entities and transport models must
  never depend on UI types.
- Once a KMP state or event is moved to `kmpModels`, remove its old declaration and update imports;
  do not leave duplicate definitions.
- Move only model declarations. Keep screens, composables, ViewModels, repositories, and behavior in
  their current owning modules.
- `kmpFeatures` may depend on `kmpModels`. Android modules may consume the Android target of
  `kmpModels` when needed.
- Do not migrate legacy Android models into `kmpModels` unless the task explicitly names them.
- Model migration must be incremental and compilation-preserving; do not combine it with API,
  persistence, navigation, or UI redesign.

---

## KMP Rules

- Prefer a common implementation in `commonMain` when all active targets support it.
- Never use Android SDK types, `Context`, Android resources, or other JVM/Android-only APIs from
  `commonMain`.
- Isolate unavoidable platform behavior behind the smallest practical `expect`/`actual` API or a
  platform-neutral interface.
- Keep `expect` declarations in `commonMain` and every required `actual` implementation in its
  platform source set.
- Do not create `expect`/`actual` wrappers for code that can remain common.
- Use Compose Multiplatform APIs and resources in shared UI.
- Use libraries already declared for KMP before adding a new dependency.
- Keep KMP models, state, events, navigation, repositories, and DI independent from Android-only
  base classes unless a KMP-compatible abstraction already exists.
- Follow the patterns already present in `kmpFeatures` and `viewModule`; do not reproduce the entire
  legacy Android architecture inside KMP.
- A KMP task changes only the KMP implementation unless the user explicitly asks for Android parity
  or shared migration.

### KMP Action-State Pattern

- KMP screen input models use `Action` names, not `Event` names (`HomeAction`, `DiaryAction`, etc.).
- `State` represents the screen's current persistent UI and is passed into stateless composables.
- `Action` represents user or system input and is handled by the feature Operator.
- `Effect` is reserved for one-time operations such as navigation, messages, or external launches;
  do not store one-time effects as persistent state.

### Operator and Screen Integration

- A feature `ScreenSetup` injects only its Operator. When Android lifecycle integration is required,
  the injected Android ViewModel wrapper counts as the Operator boundary.
- Do not inject repositories, use cases, delegates, navigation, snackbar, API, or persistence
  dependencies in a feature `ScreenSetup`.
- Koin must resolve those dependencies through the Operator constructor. The Operator itself must
  not
  call `koinInject`, `get()`, or another service-locator API.
- Keep the feature screen stateless: pass `UiState` and an action callback to it.
- For a KMP-only feature, connect the Operator directly inside `ScreenSetup`: inject the Operator,
  collect its state, and pass `operator::dispatch` to the screen.
- Do not create a `FeatureScreenConnection` composable for a KMP-only screen.
- Create a shared `FeatureScreenConnection` only when the same screen is connected through different
  Android and KMP/Wasm setup implementations. Its only purpose is to deduplicate state/action/effect
  wiring across platforms; it is not a mandatory architecture layer.
- Platform setup files own lifecycle acquisition: Android uses the ViewModel wrapper and
  `viewModelScope`; KMP/Wasm uses a composition-owned scope.
- Application-root infrastructure setup may inject global navigation and snackbar dependencies to
  connect them to `NavController` and `SnackbarHost`. This is the only exception to the feature
  `ScreenSetup` injection rule.
- The Operator effect type may be nullable because generic `Effect` has no non-null upper bound. Do
  not emit `null` merely to represent “no effect”; emit nothing instead. Prefer `Nothing` when a
  feature has no effects at all.
- Do not add a reducer, middleware, store, or another Operator layer until a concrete feature
  requires
  it.

### KMP Navigation Routes

- Shared KMP destinations are modeled in `kmpModels` as `@Serializable sealed interface RouteKMP`.
- KMP feature navigation must use `RouteKMP` values as the source of truth; string paths are
  resolved only at the navigation adapter boundary.
- Do not import Android-only `AppRoute`, `NavKey`, or Android `@Keep` into `commonMain`.

### KMP Navigation and Snackbar Usage

- KMP navigation operations use `com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase`.
- The KMP root owns the `NavController` adapter and calls `setNavigator`; it must call
  `clearNavigator()` when its lifecycle ends.
- KMP screens and operators must not access `NavController` directly. They request navigation
  through `NavigationUseCase`; route values remain typed `RouteKMP` until the root adapter.
- KMP snackbar operations use `com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate`.
- `SnackbarDelegate` is platform- and Compose-independent. Consumers may read its
  read-only `snackbarUiState`; only the root Scaffold owns the single `SnackbarHost` and renders it.
- KMP operators trigger messages through `SnackbarDelegate` and use `NavigationUseCase.goBack()` for
  back actions. Do not create screen-local snackbar hosts for shared KMP flows.

### KMP Test Policy

- Do not create KMP test source files or test dependencies in KMP modules unless the user explicitly
  requests tests for that task.

---

## Android Rules

The architecture rules below apply to existing Android-first modules. They do not automatically
apply to KMP source sets.

---

## Architecture

Use:

- UIState
- UIEvent
- ViewModel
- Repository
- NavigationUseCase

Add a UseCase only when at least one of the following is true:

- business logic exists

- multiple repository calls exist

- logic is shared by multiple screens

Do not create:

- Mapper
- Validator
- Manager
- Handler
- Provider
- Factory

unless explicitly requested.

Do not create new layers unless explicitly requested.

---

## UiState

Every UiState must contain:

kotlin val isLoading: Boolean = false val isError: Boolean = false val errorMessage: String = ""

Use:

kotlin updateState { copy(...) }

Never use:

kotlin state.value =

These mandatory fields and `updateState` rules apply to Android-first `UiState` implementations. For
KMP, preserve the state pattern already used by the feature and add only state required by the task.

---

## Events

Use:

- BaseEvent
- BaseUIEvent

Every screen event must contain:

kotlin ErrorDismiss

ErrorDismiss must clear:

kotlin isError = false errorMessage = ""

`BaseEvent`, `BaseUIEvent`, and mandatory `ErrorDismiss` are Android-first conventions. Do not
introduce Android base event types into KMP solely to satisfy this section.

---

## ViewModel

ViewModels must:

- extend BaseViewModel
- use MutableStateFlow
- use MutableSharedFlow

ViewModels must navigate only through:

kotlin NavigationUseCase

Never access:

kotlin NavBackStack

directly.

These `BaseViewModel` and `NavigationUseCase` requirements apply to Android-first ViewModels. KMP
ViewModels must remain common-compatible and follow the existing KMP feature pattern.

---

## Repository

Repository functions must return:

kotlin Result<T>

or

kotlin Flow<T>

Repository must never normalize errors.

Do not create a repository interface or implementation if one already exists.

Never call:

kotlin ErrorMessage.fetchErrorMessage()

inside repositories.

---

## Error Handling

Normalize errors only inside ViewModel.

Use:

kotlin ErrorMessage.fetchErrorMessage(error.message)

Never expose raw exception messages to UI.

---

## Validation

Validation must be inside:

kotlin private fun isFormInvalid(): Boolean

Do not inline validation logic inside submit functions.

---

## Navigation

Use:

kotlin AppRoute NavigationUseCase entry<AppRoute.X>

Never use:
Never create new routes when an existing AppRoute satisfies the requirement.
kotlin String routes Navigation 2 backStack access inside ViewModel

For KMP, use the navigation approach already configured in `kmpFeatures`; do not import Android-only
`AppRoute` or `NavigationUseCase` unless they have explicitly been migrated to common code.

---

## Compose Rules

Prefer stateless composables.

Pass state through parameters.

Pass events through callbacks.

Do not place business logic inside composables.

Do not place repository calls inside composables.

Do not place navigation logic inside composables.

---

## UI

Use only:

kotlin AppColors AppTextStyles AppSpacing AppShapes

Never use:

kotlin Color(...) TextStyle(...) 16.dp RoundedCornerShape(...)

directly.

For UI work, use Material 3's default colors and ready-made component styling.
Do not add custom colors or override the default colors of Material 3 components
unless the user explicitly requests a color change.

For KMP UI, use the theme and tokens in `viewModule` or `kmpFeatures`. Do not depend on Android-only
Compose resources or styling classes.

- For KMP feature operations, use `com.oyetech.kmpfeatures.theme.AppColors` for all color values
  in composables. Do not use `MaterialTheme.colorScheme` or define feature-local `Color` values
  when an `AppColors` value provides the required color.

---

## Models

All request/response/data models must be:

kotlin @Keep data class

---

## Dependency Injection

Use:

kotlin viewModelOf singleOf factoryOf

Choose the simplest valid option.

Do not introduce additional DI abstractions.

Use the Koin APIs available to the active source set. Do not reference Android Koin APIs from
`commonMain`.

---

## Code Generation

Generate the smallest implementation that satisfies the request.

Do not create additional files.

Do not create additional classes.

Do not create additional layers.

Do not create abstractions for future use.

Do not optimize for hypothetical requirements.

Implement only what is required by the current task.

Modify existing code before creating new code.

---

## Change and Verification Rules

- Keep edits local to the requested module and platform.
- Do not clean up unrelated legacy code during KMP work.
- Do not delete or replace an Android implementation after creating its KMP counterpart unless
  explicitly requested.
- Preserve user changes already present in the worktree.
- Run the narrowest relevant Gradle compilation or test task for the changed module/source set.
- For KMP changes, verify each affected target, not just `commonMain`.
- If an unaffected legacy module is already broken, report it separately and do not expand the task
  into a repository-wide refactor.
