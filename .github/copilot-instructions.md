## Navigation Rules (Navigation 3 — androidx.navigation3)

This project uses **Jetpack Navigation 3**. Never use Navigation 2 APIs.

### 1. Routes are typed `AppRoute` entries — no string routes

All destinations are declared in `composeBase/.../navigator/Route.kt` as `@Serializable`
`data object` or `data class` implementing `AppRoute` (which extends `NavKey`).

```kotlin
// ✅ Correct — typed route
navigationUseCase.navigateTo(AppRoute.UserProfile(receiverUserId = id))
navigationUseCase.navigateTo(AppRoute.QuestionAppHomepage)

// ❌ Wrong — string routes do not exist in this project
navigate("user_profile/$id")
Route("user_profile").withArgs(...)
```

### 2. New destinations must be added to `AppRoute`

Add every new screen as a new entry inside the `AppRoute` sealed interface:

```kotlin
// ✅ No-arg destination
@Keep @Serializable data object MyNewScreen : AppRoute

// ✅ Parameterised destination
@Keep @Serializable data class MyDetail(val itemId: String = "") : AppRoute
```

Then register the screen in the appropriate `EntryProviderScope<NavKey>` extension function
(e.g. `questionAppNavigation()`):

```kotlin
entry<AppRoute.MyNewScreen> { MyNewScreenSetup() }
entry<AppRoute.MyDetail> { MyDetailSetup(itemId = it.itemId) }
```

### 3. ViewModels navigate via `NavigationUseCase` — never touch the backstack directly

```kotlin
// ✅ Correct — VM navigates through the use case
class MyViewModel(
    private val navigationUseCase: NavigationUseCase,
    ...
) : BaseViewModel(appDispatchers) {

    private fun handleItemClick(id: String) {
        navigationUseCase.navigateTo(AppRoute.MyDetail(itemId = id))
    }

    private fun handleBack() {
        navigationUseCase.goBack()
    }
}

// ❌ Wrong — VMs must not hold a reference to NavBackStack
backStack.add(AppRoute.MyDetail(id))
```

### 4. Backstack must never be empty — `NavDisplay` crashes on an empty backstack

- Seed `rememberNavBackStack` with the start destination.
- Guard every pop with a size check:

```kotlin
// ✅ Safe
goBack = { scope.launch { if (backStack.size > 1) backStack.removeLastOrNull() } }

// ❌ Unsafe — if backStack has 1 entry this empties it → crash
goBack = { scope.launch { backStack.removeLastOrNull() } }
```

### 5. Bottom-tab switching uses `navigateToBottomTab(backStack, route)`

Never push a bottom-tab destination via `backStack.add()` directly — use the helper:

```kotlin
// ✅ Correct
navigateToBottomTab(backStack, AppRoute.QuestionAppHomepage)

// ❌ Wrong — duplicates the tab entry without restoring its sub-stack
backStack.add(AppRoute.QuestionAppHomepage)
```

---

## Profile Completion Check

Use a data class extension for profile completion checks (e.g.,
`UserDataProperty.isProfileCompletedForAuth()`); do not implement this check inline in ViewModels.

```kotlin
// ✅ Correct
import com.oyetech.models.firebaseModels.userModel.isProfileCompletedForAuth
userData.isProfileCompletedForAuth()   // checks username, age, gender

// ❌ Wrong — do not inline this logic in a ViewModel or mapper
userData.username.isNotBlank() && userData.age.isNotBlank() && userData.gender.isNotBlank()
```

---

## Color And Typography Rules

### 1. Single source of truth for colors: `AppColorPalette`

All theme colors must be defined in `composeBase/.../theme/AppColors.kt` inside `lightPalette` and
`darkPalette`. Do not define extra `Color(...)` values in screens, components, or ViewModels.

```kotlin
// ✅ Correct
color = AppColors.primary

// ❌ Wrong
color = Color(0xFFECA73B)
```

### 2. Material components should use theme defaults

Prefer Material3 default colors from `MaterialTheme.colorScheme` (already derived from
`AppColorPalette`) for `Button`, `Card`, `TopAppBar`, `LinearProgressIndicator`, etc. Only override
component colors when there is a strict design requirement.

### 3. Text colors use semantic tokens

For text color assignments, use semantic tokens from `AppColors` (`textPrimary`, `textSecondary`,
`primary`, `error`) instead of hardcoded colors.

### 4. Typography uses `AppTextStyles`

Avoid inline `TextStyle(...)` in screen code. Use centralized styles from
`composeBase/.../theme/AppTextStyles.kt`.

```kotlin
// ✅ Correct
Text(text = title, style = AppTextStyles.titleLarge, color = AppColors.textPrimary)

// ❌ Wrong
Text(text = title, style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold))
```

### 5. No direct color or font literals in UI layers

In Compose screens and shared views:

- do not use `Color(...)` literals
- do not use arbitrary inline `fontSize`, `lineHeight`, or `fontWeight`
- use `AppColors` and `AppTextStyles` only

### 6. Spacing and shape tokens are mandatory

Use centralized tokens from `composeBase/.../theme/AppDimensions.kt`:

- spacing: `AppSpacing.*` (`xs`, `sm`, `md`, `lg`, `xl`, etc.)
- radius values: `AppCornerRadius.*`
- ready shapes: `AppShapes.*` (`roundedSmall`, `roundedMedium`, `roundedLarge`, `roundedPill`)

```kotlin
// ✅ Correct
Modifier.padding(AppSpacing.lg)
Card(shape = AppShapes.roundedLarge) { ... }

// ❌ Wrong
Modifier.padding(16.dp)
Card(shape = RoundedCornerShape(16.dp)) { ... }
```

When a custom shape is unavoidable, compose it from `AppCornerRadius.*` values instead of raw `dp`.

---

## Error Handling Rules

### 1. Repository suspend functions must return `Result<T>` — never throw

Wrap the entire body in `try/catch (e: Exception)` and annotate with
`@Suppress("TooGenericExceptionCaught")`. Preserve the **raw** exception message — do **not** call
`ErrorMessage.fetchErrorMessage()` in the repository. The ViewModel is the single normalisation
point. For a try-first-then-fallback chain use `runCatching`:

```kotlin
// ✅ Single operation — keep raw message so developer can read it in logs
@Suppress("TooGenericExceptionCaught")
override suspend fun doSomething(): Result<MyData> {
    return try {
        Result.success(remoteSource.fetch())
    } catch (e: Exception) {
        Result.failure(e)   // raw "[28404] Failed to retrieve an ID token" travels to VM
    }
}

// ✅ Inner helpers that construct exception objects — also keep raw message
} catch (e: SomeSpecificException) {
MyResponseData(errorException = Exception(e.message ?: "fallback message"))
}

// ✅ Fallback chain
private suspend fun getOrCreate(): MyData {
return runCatching { repo.get().first() }.getOrElse { repo.create().first() }
}
```

### 2. ViewModels consume `Result<T>` with `.fold()` — never `OperationState`

Set `isLoading = true` before launching, then resolve **both** branches inside `.fold()`.
Always reset loading in both paths. **Always** pass `error.message` through
`ErrorMessage.fetchErrorMessage()` in `onFailure` — this is the **single normalisation point**
before any error string reaches the UI:

```kotlin
// ✅ Correct
viewModelScope.launch(getDispatcherIo()) {
    myState.updateState { copy(isLoading = true, isError = false, errorMessage = "") }
    repository.doSomething().fold(
        onSuccess = { data ->
            myState.updateState { copy(isLoading = false, /* map data */) }
            uiEvent.emit(MyUiEvent.Success)
        },
        onFailure = { error ->
            myState.updateState {
                copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = ErrorMessage.fetchErrorMessage(error.message)  // "[28404]" → "NO_INTERNET_CONNECTION"
                )
            }
        }
    )
}

// ❌ Wrong — raw message bypasses normalisation (e.g. leaks "[28404]" to UI)
errorMessage = error.message ?: "Login failed"
```

### 3. UiState must carry `isLoading`, `isError`, `errorMessage`

```kotlin
data class XUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",   // use LanguageKey.* constants for user-facing strings
    // ... domain fields
)
```

Always add an `ErrorDismiss` event that clears both flags:

```kotlin
is XEvent.ErrorDismiss -> myState.updateState { copy(isError = false, errorMessage = "") }
```

### 4. Form validation goes in a dedicated private guard function

```kotlin
private fun isFormInvalid(): Boolean {
    if (state.username.isBlank()) {
        myState.updateState { copy(isError = true, errorMessage = LanguageKey.usernameIsEmpty) }
        return true
    }
    return false
}

private fun handleSubmit() {
    if (isFormInvalid()) return
    // proceed
}
```
