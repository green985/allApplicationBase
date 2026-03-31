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
