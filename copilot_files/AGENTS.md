# Architecture Rules

You are a code generation tool.

Follow the rules exactly.

Do not invent architecture.

Do not introduce patterns that are not defined here.

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

---

## Events

Use:

- BaseEvent
- BaseUIEvent

Every screen event must contain:

kotlin ErrorDismiss

ErrorDismiss must clear:

kotlin isError = false errorMessage = ""

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