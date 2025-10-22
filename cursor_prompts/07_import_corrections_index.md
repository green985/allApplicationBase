# Import Corrections Index

## Overview

This document indexes all correct import paths based on actual implementation corrections. Use this
as a reference when writing new code.

## Critical Import Corrections

### Flow Extensions

```kotlin
// WRONG
import com.oyetech.tools.helpers.asResult

// CORRECT
import com.oyetech.tools.coroutineHelper.asResult
```

**Context:** Used for Flow.asResult() extension function in ViewModels

### Base Event

```kotlin
// WRONG
import com.oyetech.models.utils.const.BaseEvent

// CORRECT
import com.oyetech.composebase.base.BaseEvent
```

**Context:** Base class for all sealed event classes

### Koin ViewModel Injection

```kotlin
// WRONG
import org.koin.compose.koinViewModel

// CORRECT
import org.koin.androidx.compose.koinViewModel
```

**Context:** Compose function for injecting ViewModels with Koin

## Navigation UseCase

### Method Signature

```kotlin
// WRONG
navigationUseCase.navigateTo(QuestionAppProjectRoutes.QuestionCreateQuestionPage)

// CORRECT
navigationUseCase.navigate(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route)
```

**Note:** Use `.navigate()` method with `.route` property extraction

## Standard Import Templates

### ViewModel File Template

```kotlin
package com.oyetech.composebase.[feature]

import androidx . lifecycle . viewModelScope
        import com . oyetech . composebase . base . BaseViewModel
        import com . oyetech . composebase . projectQuestionsFeature . navigation . QuestionAppProjectRoutes
        import com . oyetech . domain . repository . firebase . FirebaseUserRepository
        import com . oyetech . domain . useCases . NavigationUseCase
        import com . oyetech . tools . coroutineHelper . AppDispatchers
        import com . oyetech . tools . coroutineHelper . asResult
        import kotlinx . coroutines . flow . MutableStateFlow
        import kotlinx . coroutines . flow . collectLatest
        import kotlinx . coroutines . flow . update
        import kotlinx . coroutines . launch
        import timber . log . Timber

class [Feature]Vm(
appDispatchers: AppDispatchers,
private val navigationUseCase: NavigationUseCase,
// ... other dependencies
) : BaseViewModel(appDispatchers) {
    // Implementation
}
```

### Compose File Template

```kotlin
package com.oyetech.composebase.[feature]

import androidx . compose . foundation . layout . Box
        import androidx . compose . foundation . layout . Column
        import androidx . compose . foundation . layout . fillMaxSize
        import androidx . compose . foundation . layout . padding
        import androidx . compose . material . icons . Icons
        import androidx . compose . material . icons . filled . Add
        import androidx . compose . material3 . FloatingActionButton
        import androidx . compose . material3 . Icon
        import androidx . compose . material3 . MaterialTheme
        import androidx . compose . material3 . Scaffold
        import androidx . compose . material3 . Text
        import androidx . compose . runtime . Composable
        import androidx . compose . runtime . getValue
        import androidx . compose . ui . Modifier
        import androidx . compose . ui . unit . dp
        import androidx . lifecycle . compose . collectAsStateWithLifecycle
        import org . koin . androidx . compose . koinViewModel

        @Composable
        fun [ Feature]Screen(
modifier: Modifier = Modifier,
) {
    // Implementation
}
```

### Event File Template

```kotlin
package com.oyetech.composebase.[feature]

import com . oyetech . composebase . base . BaseEvent

        sealed class [Feature]Event : BaseEvent() {
    object OnSomeAction : [Feature]Event()
    data class OnItemClicked(val id: String) : [Feature]Event()
}
```

### UiState File Template

```kotlin
package com.oyetech.composebase.[feature]

import androidx . compose . runtime . Immutable
        import kotlinx . collections . immutable . ImmutableList
        import kotlinx . collections . immutable . persistentListOf

        @Immutable
        data class [ Feature]UiState(
val items: ImmutableList<Item> = persistentListOf(),
val isLoading: Boolean = false,
val hasError: Boolean = false,
val errorMessage: String? = null,
)
```

## Package Structure Reference

### ViewModel Layer

- `com.oyetech.composebase.base.BaseViewModel`
- `com.oyetech.composebase.base.BaseEvent`

### Domain Layer

- `com.oyetech.domain.useCases.*`
- `com.oyetech.domain.repository.*`

### Tools Layer

- `com.oyetech.tools.coroutineHelper.AppDispatchers`
- `com.oyetech.tools.coroutineHelper.asResult`

### Koin

- `org.koin.androidx.compose.koinViewModel` (Compose)
- `org.koin.androidx.viewmodel.dsl.viewModelOf` (Module registration)
- `org.koin.core.module.dsl.singleOf` (Singleton registration)

### Lifecycle

- `androidx.lifecycle.viewModelScope`
- `androidx.lifecycle.compose.collectAsStateWithLifecycle`

### Coroutines

- `kotlinx.coroutines.flow.MutableStateFlow`
- `kotlinx.coroutines.flow.StateFlow`
- `kotlinx.coroutines.flow.collectLatest`
- `kotlinx.coroutines.flow.update`
- `kotlinx.coroutines.launch`

### Immutable Collections

- `kotlinx.collections.immutable.ImmutableList`
- `kotlinx.collections.immutable.ImmutableMap`
- `kotlinx.collections.immutable.persistentListOf`
- `kotlinx.collections.immutable.persistentMapOf`
- `kotlinx.collections.immutable.toImmutableList`
- `kotlinx.collections.immutable.toImmutableMap`

## Common Mistakes and Fixes

| Mistake                              | Reason                      | Fix                              |
|--------------------------------------|-----------------------------|----------------------------------|
| Using `tools.helpers` package        | Package moved/renamed       | Use `tools.coroutineHelper`      |
| Using `models.utils.const.BaseEvent` | Wrong module                | Use `composebase.base.BaseEvent` |
| Using `org.koin.compose`             | Wrong Koin package          | Use `org.koin.androidx.compose`  |
| Using `navigateTo()`                 | API changed                 | Use `navigate()` with `.route`   |
| Using mutable `List<T>` in UiState   | Causes recomposition issues | Use `ImmutableList<T>`           |
| Importing wildcards                  | Bad practice                | Import specific classes          |

## Verification Checklist

When writing new code, verify:

- [ ] ViewModel extends `BaseViewModel` from `composebase.base`
- [ ] Event extends `BaseEvent` from `composebase.base`
- [ ] Using `asResult` from `tools.coroutineHelper`
- [ ] Using `koinViewModel` from `org.koin.androidx.compose`
- [ ] Navigation uses `.navigate()` with `.route`
- [ ] Collections are `ImmutableList` or `ImmutableMap`
- [ ] No wildcard imports
- [ ] All imports are specific and organized

## Quick Reference

```kotlin
// ViewModel Basics
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

// Event
import com.oyetech.composebase.base.BaseEvent

// Compose
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Flow Extensions
import com.oyetech.tools.coroutineHelper.asResult

// Navigation
navigationUseCase.navigate(route.route)

// Immutable Collections
import kotlinx . collections . immutable . ImmutableList
        import kotlinx . collections . immutable . persistentListOf
```

## Notes

- Always check actual implementation for latest patterns
- IDE auto-import may suggest wrong packages
- Prefer explicit imports over wildcards
- Group imports by package (Android → Project → Third-party)

