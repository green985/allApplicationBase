# AGENTS.md — AllApplicationBase

## Architecture Overview

Multi-module Android monorepo sharing a single library stack across multiple apps (
`quoteapplication`, `questionapplication`). Follows Clean Architecture:

| Module             | Role                                                               |
|--------------------|--------------------------------------------------------------------|
| `:domain`          | Repository interfaces + use cases (no implementations)             |
| `:data:models`     | Pure data/request/response models                                  |
| `:data:remote`     | Retrofit data sources                                              |
| `:data:local`      | Room data sources                                                  |
| `:data:repository` | Repository implementations                                         |
| `:composeBase`     | Shared Compose UI, ViewModels, navigation, base classes            |
| `:diModule`        | Koin DI wiring — assembles all modules in `AppComponent.kt`        |
| `:subImpl:tools`   | Utilities: coroutine helpers, string helpers, dispatchers          |
| Feature libs       | `:radioService`, `:adsModule`, `:firebaseDB`, `:googleLogin`, etc. |

Feature modules must only depend on `:domain` and `:data:models`. Only `:diModule` depends on all
other modules.

## Dependency Injection (Koin 4.x)

All modules are assembled in `diModule/src/.../koins/AppComponent.kt`. Registration conventions:

- `singleOf(::ClassName)` — shared-state VMs and services (e.g., `AuthOperationVM`,
  `SnackbarDelegate`)
- `viewModelOf(::ClassName)` — lifecycle-scoped ViewModels
- `factoryOf(::ClassName)` — new instance per injection (e.g.,
  `GetQuestionsPagedByCreatedAtUseCase`)

## ViewModel Pattern

All ViewModels extend `BaseViewModel(appDispatchers: AppDispatchers)` in
`composeBase/base/BaseViewModel.kt`.

- **State**: `MutableStateFlow<XUiState>` — mutate with the `updateState { copy(...) }` extension (
  `composeBase/base/UIState.kt`)
- **Events**: `MutableSharedFlow<XUiEvent>` for one-shot UI events
- **Global event bus**: `TestEventNavigator` (Koin singleton) broadcasts `BaseEvent` to every active
  VM via `onEvent()`. VMs receive these automatically in `BaseViewModel.init`.
- **Mappers**: State-mapping logic is extracted to `*Mappers.kt` files (e.g.,
  `AuthOperationMappers.kt`) — keep VMs lean.

## Profile Completion Check — REQUIRED PATTERN

> **From `.github/copilot-instructions.md`**: Use a data class extension for profile completion
> checks; do not implement this check inline in ViewModels.

```kotlin
// ✅ Correct
import com.oyetech.models.firebaseModels.userModel.isProfileCompletedForAuth
userData.isProfileCompletedForAuth()   // checks username, age, gender

// ❌ Wrong — do not inline this logic in a ViewModel or mapper
userData.username.isNotBlank() && userData.age.isNotBlank() && userData.gender.isNotBlank()
```

## Color And Typography — REQUIRED PATTERN

### Color rules

- Single source of truth: `composeBase/.../theme/AppColors.kt`
- Define all light/dark values in `lightPalette` and `darkPalette` (`AppColorPalette`)
- Use `AppColors.*` for semantic UI colors (especially text/content color decisions)
- Do **not** use `Color(...)` literals in screen code
- Let Material components use defaults from `MaterialTheme.colorScheme` (mapped from palette)

```kotlin
// ✅ Correct
Text(text = title, color = AppColors.textPrimary)

// ❌ Wrong
Text(text = title, color = Color(0xFF1A1917))
```

### Typography rules

- Single source of truth: `composeBase/.../theme/AppTextStyles.kt`
- Use `AppTextStyles` (`titleLarge`, `titleMedium`, `titleSmall`, `body`, `bodySecondary`,
  `label`, `button`)
- Do **not** define inline `TextStyle(...)` in screens unless there is a strict exception
- Keep hierarchy consistent: title -> body -> secondary/label

```kotlin
// ✅ Correct
Text(text = description, style = AppTextStyles.bodySecondary, color = AppColors.textSecondary)

// ❌ Wrong
Text(text = description, style = TextStyle(fontSize = 13.sp, lineHeight = 20.sp))
```

### Spacing and shape rules

- Single source of truth: `composeBase/.../theme/AppDimensions.kt`
- Use `AppSpacing.*` for paddings, gaps, and sizes (`xs`, `sm`, `md`, `lg`, `xl`, ...)
- Use `AppShapes.*` for component shapes (`roundedSmall`, `roundedMedium`, `roundedLarge`, `roundedPill`)
- If you must build a custom `RoundedCornerShape`, use `AppCornerRadius.*` tokens
- Do **not** use raw `dp` literals for spacing/radius in screen code unless there is a strict exception

```kotlin
// ✅ Correct
Modifier.padding(AppSpacing.lg)
Card(shape = AppShapes.roundedLarge) { ... }

// ✅ Acceptable custom shape with tokens
RoundedCornerShape(AppCornerRadius.small)

// ❌ Wrong
Modifier.padding(16.dp)
RoundedCornerShape(16.dp)
```

## Navigation — Navigation 3 (androidx.navigation3)

This project uses **Jetpack Navigation 3** (`androidx.navigation3`). Do **not** use Navigation 2
APIs (`NavController`, `NavHost`, `NavHostFragment`, string routes, `navigate(route: String)`).

### Core concepts

| Concept | Nav 3 implementation |
|---|---|
| Route type | `AppRoute` — `sealed interface` implementing `NavKey` (`:composeBase/navigator/Route.kt`) |
| Route declaration | `@Serializable data object` / `@Serializable data class` nested in `AppRoute` |
| Backstack | `NavBackStack<NavKey>` — a `MutableList` you own; created with `rememberNavBackStack(start)` |
| Screen renderer | `NavDisplay(backStack = ..., entryProvider = entryProvider { ... })` |
| Screen registration | `entry<AppRoute.X> { ... }` inside an `EntryProviderScope<NavKey>` extension function |
| Navigation bridge | `NavigationUseCase` (Koin singleton) — wraps `backStack.add()` / `backStack.removeLastOrNull()` |
| Bottom tabs | `navigateToBottomTab(backStack, route)` helper pops to existing tab or pushes a new one |

### Route declaration (AppRoute)

All destinations live in `composeBase/.../navigator/Route.kt` as entries of the `AppRoute` sealed
interface. Parameter-less destinations are `data object`; parameterised destinations are
`data class`:

```kotlin
// ✅ Correct — typed, serializable, implements NavKey via AppRoute
@Keep @Serializable data object QuestionAppHomepage : AppRoute
@Keep @Serializable data class UserProfile(val receiverUserId: String = "") : AppRoute

// ❌ Wrong — string routes are Navigation 2, not used here
navigateTo("user_profile/{userId}")
Route("user_profile").withArgs("userId" to id)
```

### NavigationUseCase — VM-side navigation

`NavigationUseCase` is a Koin `single`. Inject it in ViewModels and call:

```kotlin
// ✅ Navigate forward
navigationUseCase.navigateTo(AppRoute.UserProfile(receiverUserId = id))

// ✅ Pop back
navigationUseCase.goBack()
```

`setNavigator(navigateTo, goBack)` is wired **once** in the root Composable (`QuestionMainScreen`)
where the `NavBackStack` lives:

```kotlin
// ✅ Root composable — wires NavigationUseCase to the real backstack
val backStack = rememberNavBackStack(startDestination)
val scope = rememberCoroutineScope()

navigationUseCase.setNavigator(
    navigateTo = { route -> scope.launch { backStack.add(route as NavKey) } },
    goBack = { scope.launch { backStack.removeLastOrNull() } }
)
```

### NavDisplay and entry registration

Register screens with typed `entry<T>` blocks grouped into extension functions on
`EntryProviderScope<NavKey>`:

```kotlin
// ✅ Feature navigation file
fun EntryProviderScope<NavKey>.questionAppNavigation() {
    entry<AppRoute.QuestionAppHomepage> { QuestionsHomeScreenSetup() }
    entry<AppRoute.UserProfile> { User2ProfileScreenSetup(receiverUserId = it.receiverUserId) }
}

// ✅ NavDisplay wiring in root composable
NavDisplay(
    backStack = backStack,
    entryProvider = entryProvider {
        navHostScreenSetup(navigationUseCase)
        questionAppNavigation()
    }
)
```

### Backstack safety rules — REQUIRED

- **Always seed** `rememberNavBackStack` with at least one start destination.
  `NavDisplay` **crashes** if the backstack is empty.
- **Never pop the last entry** — guard with `backStack.size > 1` before any pop operation.
- **Bottom-tab navigation** — use `navigateToBottomTab(backStack, route)`:
  - destination already in stack → pop entries above it (restores sub-stack)
  - destination is new → push it

```kotlin
// ✅ Safe pop guard
goBack = {
    scope.launch {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }
}

// ❌ Wrong — removeLastOrNull() on a single-entry stack empties it → NavDisplay crash
goBack = { scope.launch { backStack.removeLastOrNull() } }
```

### Object alias references (QuestionAppProjectRoutes)

`QuestionAppProjectRoutes` provides convenience aliases for the most common routes. Prefer
`AppRoute.*` directly in new code.

## Error Handling — REQUIRED PATTERN

Canonical reference: `AuthOperationRepositoryImpl` (`:googleLogin`) + `AuthOperationVM` (
`:composeBase`).

### Repository layer — return `Result<T>`, never throw

Use `try/catch (e: Exception)` + `@Suppress("TooGenericExceptionCaught")`. Preserve the **raw**
exception message so developers can read it in logs. Do **not** call
`ErrorMessage.fetchErrorMessage()` here — normalisation happens in the ViewModel. For fallback
chains use `runCatching`:

```kotlin
// ✅ Single operation — keep raw message for debugging
@Suppress("TooGenericExceptionCaught")
override suspend fun loginWithGoogleAndSyncUser(): Result<UserDataProperty> {
    return try {
        val user = signInWithGoogle()
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)   // raw "[28404] Failed to retrieve an ID token" travels to VM
    }
}

// ✅ Inner helpers — also keep raw message
} catch (e: SomeSpecificException) {
MyResponseData(errorException = Exception(e.message ?: "fallback message"))
}

// ✅ Fallback chain
private suspend fun loginOrRegisterUser(googleUser: GoogleUserResponseData): UserDataProperty {
return runCatching {
questionSupabaseRepository.getUserWithToken(...).first()
}.getOrElse {
questionSupabaseRepository.registerGoogleUser(...).first()
}
}
```

### ViewModel layer — `.fold()` on `Result<T>`, errors live in UiState

**Never** use `OperationState` for auth/profile flows. Store errors as `isError: Boolean` +
`errorMessage: String` fields directly in the UiState data class. Always reset loading in both
branches. **Always** pass `error.message` through `ErrorMessage.fetchErrorMessage()` in the
ViewModel's `onFailure` — this is the **single normalisation point** before any error is shown to
the user:

```kotlin
// ✅ Correct
onFailure = { error ->
    authOperationState.updateState {
        copy(
            isLoading = false,
            isError = true,
            errorMessage = ErrorMessage.fetchErrorMessage(error.message)  // "[28404]" → "NO_INTERNET_CONNECTION"
        )
    }
}

// ❌ Wrong — raw message bypasses normalisation (e.g. leaks "[28404]" to UI)
errorMessage = error.message ?: "Login failed"
```

```kotlin
// ✅ Dismiss pattern — always wire an ErrorDismiss event
AuthOperationEvent.ErrorDismiss -> authOperationState.updateState {
    copy(isError = false, errorMessage = "")
}
```

### Form validation — extract to a private guard function

```kotlin
// ✅ Extract validation, return early
private fun isErrorInProfileForm(): Boolean {
    if (state.username.isBlank()) {
        authOperationState.updateState {
            copy(
                isError = true,
                errorMessage = LanguageKey.usernameIsEmpty
            )
        }
        return true
    }
    // ... more checks
    return false
}

private fun handleSubmitProfile() {
    if (isErrorInProfileForm()) return
    // proceed
}
```

### UiState structure for any async operation

```kotlin
data class XUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",   // use LanguageKey.* constants for user-facing messages
    // ... domain fields
)
```

> `OperationState<T>` (`data/models/.../helpers/OperationState.kt`) exists but is **not** the
> pattern used in auth/login/profile flows. Prefer `Result<T>` + `.fold()` + inline UiState flags.

## Snackbar

Inject `SnackbarDelegate` (Koin single) and call `snackbarDelegate.triggerSnackbarState(message)`.
Never emit snackbar messages directly from the UI layer.

## Code Quality

- **Detekt** + **ktlint** run on every module; config in `detekt.yml` (max line length 120).
- `FunctionNaming` rule is relaxed inside `composeBase/**` to allow PascalCase Composable functions.
- String obfuscation via `lsparanoid` plugin; apply `@Obfuscate` to sensitive string literals.
- Version catalog: all dependency versions live in `gradle/libs.versions.toml` — do not hardcode
  versions in module `build.gradle.kts`.

## Build Commands

```bash
# Full build
./gradlew assembleDebug

# Run detekt on all modules
./gradlew detekt

# Run ktlint check
./gradlew ktlintCheck

# Auto-fix ktlint
./gradlew ktlintFormat

# Run unit tests
./gradlew test
```

## Adding a New Module

1. Create the module directory and `build.gradle.kts` using `alias(libs.plugins.android.library)`.
2. Add `include ':yourModule'` to `settings.gradle`.
3. Add a Koin `object YourModule { val module = module { ... } }`.
4. Register it in `diModule/koins/AppComponent.appComponentt`.

