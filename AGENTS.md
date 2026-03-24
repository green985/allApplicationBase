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

## Navigation

`NavigationUseCase` (Koin singleton) is the cross-VM navigation bridge — inject it and call
`navigateTo(route)`. Routes are `Route("path")` data objects declared in `*Routes.kt` files (e.g.,
`QuestionAppProjectRoutes`). Use `Route.withArgs()` for query parameters.

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

