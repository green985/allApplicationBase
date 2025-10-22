# Floating Action Button Pattern

## Overview

This document describes the standard pattern for implementing Floating Action Buttons (FAB) in the
project, using FloatingAskQuestionBar as a reference implementation.

## Architecture

### Files Structure

```
sharedViews/floating/
├── FloatingAskQuestionBar.kt          # Compose UI component
├── FloatingAskQuestionBarVm.kt        # ViewModel with business logic
├── FloatingAskQuestionBarUiState.kt   # UI state data class
└── FloatingAskQuestionBarEvent.kt     # Sealed event class
```

## Implementation Pattern

### 1. UiState Definition

```kotlin
data class FloatingAskQuestionBarUiState(
    val isVisible: Boolean = false,
)
```

**Rules:**

- Simple data class with visibility state
- Can include additional UI-related properties
- Use `Boolean` for visibility control

### 2. Event Definition

```kotlin
import com.oyetech.composebase.base.BaseEvent

sealed class FloatingAskQuestionBarEvent : BaseEvent() {
    object OnFabClicked : FloatingAskQuestionBarEvent()
}
```

**Rules:**

- Extend `BaseEvent()` from `com.oyetech.composebase.base.BaseEvent`
- Use sealed class for type safety
- Name events with `On` prefix

### 3. ViewModel Implementation

```kotlin
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class FloatingAskQuestionBarVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val firebaseUserRepository: FirebaseUserRepository,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(FloatingAskQuestionBarUiState())

    init {
        observeLoginStatus()
    }

    override fun onEvent(event: Any) {
        if (event is FloatingAskQuestionBarEvent) {
            when (event) {
                FloatingAskQuestionBarEvent.OnFabClicked -> navigateToCreateQuestion()
            }
        }
    }

    private fun observeLoginStatus() {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseUserRepository.userDataStateFlow.asResult().collectLatest { result ->
                result.fold(
                    onSuccess = { userData ->
                        val isVisible = userData.isProfileComplete()
                        uiState.value = uiState.value.copy(isVisible = isVisible)
                        Timber.d("FloatingAskQuestionBar visibility: $isVisible")
                    },
                    onFailure = { exception ->
                        uiState.value = uiState.value.copy(isVisible = false)
                        Timber.e("Failed to observe login status: ${exception.message}")
                    }
                )
            }
        }
    }

    private fun navigateToCreateQuestion() {
        navigationUseCase.navigate(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route)
    }
}
```

**Rules:**

- Extend `BaseViewModel(appDispatchers)`
- Observe user authentication status in `init`
- Handle navigation in ViewModel, not in Compose
- Use `collectLatest` for Flow observation
- Update state immutably with `copy()`

### 4. Compose Component

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun FloatingAskQuestionBar(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<FloatingAskQuestionBarVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    FloatingAskQuestionBarContent(
        modifier = modifier,
        uiState = uiState,
        onEvent = vm::onEvent
    )
}

@Composable
private fun FloatingAskQuestionBarContent(
    modifier: Modifier = Modifier,
    uiState: FloatingAskQuestionBarUiState,
    onEvent: (FloatingAskQuestionBarEvent) -> Unit,
) {
    if (uiState.isVisible) {
        FloatingActionButton(
            onClick = { onEvent(FloatingAskQuestionBarEvent.OnFabClicked) },
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ask Question"
            )
        }
    }
}
```

**Rules:**

- Separate setup function from content function
- Setup function: `koinViewModel` from `org.koin.androidx.compose.koinViewModel`
- State collection: `collectAsStateWithLifecycle`
- Content function: Private, receives `uiState` and `onEvent`
- `modifier: Modifier = Modifier` at the top
- No business logic in Compose functions
- Visibility controlled by `uiState.isVisible`

### 5. Koin Registration

```kotlin
object QuestionProjectModule {
    val module = module {
        viewModelOf(::FloatingAskQuestionBarVm)
    }
}
```

**Rules:**

- Register ViewModel in appropriate module
- Use `viewModelOf` for ViewModels

## Usage Example

```kotlin
@Composable
fun QuestionMainScreen() {
    Scaffold(
        floatingActionButton = {
            FloatingAskQuestionBar()
        }
    ) { paddingValues ->
        // Screen content
    }
}
```

## Key Benefits

1. **Separation of Concerns**: UI logic separated from business logic
2. **Testability**: ViewModel can be unit tested
3. **Reusability**: Component can be reused across screens
4. **Type Safety**: Sealed events provide compile-time safety
5. **State Management**: Centralized state in ViewModel
6. **Authentication Aware**: Automatically shows/hides based on login status

## Authentication Integration

The FAB observes user authentication status through:

- `FirebaseUserRepository.userDataStateFlow`
- Checks `userData.isProfileComplete()`
- Updates `isVisible` state accordingly

## Navigation Integration

Navigation is handled through:

- `NavigationUseCase.navigateTo()`
- Route: `QuestionAppProjectRoutes.QuestionCreateQuestionPage`
- Navigation logic in ViewModel, not Compose

## Common Patterns

### Show/Hide Based on Authentication

```kotlin
private fun observeLoginStatus() {
    viewModelScope.launch(getDispatcherIo()) {
        firebaseUserRepository.userDataStateFlow.asResult().collectLatest { result ->
            result.fold(
                onSuccess = { userData ->
                    uiState.value = uiState.value.copy(
                        isVisible = userData.isProfileComplete()
                    )
                },
                onFailure = {
                    uiState.value = uiState.value.copy(isVisible = false)
                }
            )
        }
    }
}
```

### Handle Click Events

```kotlin
override fun onEvent(event: Any) {
    if (event is FloatingAskQuestionBarEvent) {
        when (event) {
            FloatingAskQuestionBarEvent.OnFabClicked -> handleClick()
        }
    }
}
```

### Navigate on Click

```kotlin
private fun navigateToCreateQuestion() {
    navigationUseCase.navigate(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route)
}
```

**Note:** Use `.navigate()` with `.route` property, not `.navigateTo()` directly with Route object.

## Checklist

- [ ] UiState created with visibility property
- [ ] Event sealed class extends BaseEvent
- [ ] ViewModel extends BaseViewModel
- [ ] ViewModel observes authentication status
- [ ] Navigation handled in ViewModel
- [ ] Compose function separated (setup + content)
- [ ] modifier: Modifier = Modifier at top
- [ ] ViewModel registered in Koin module
- [ ] No business logic in Compose
- [ ] State collected with collectAsStateWithLifecycle
