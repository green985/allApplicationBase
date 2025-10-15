# Kod Kuralları 2 - Question Project Architecture

## Model Hierarchy

### QuestionOperationResponseBody

```kotlin
data class QuestionOperationResponseBody(
    val questionId: String,
    val title: String,
    val type: String,
    val taxonomy: QuestionTaxonomy,
    val options: ImmutableList<QueOption>,
    val constraints: QueConstraints?,
    val metadata: Map<String, Any>?,
    val version: Int,
    val createdAt: Long,
    val status: String, // PENDING, APPROVED, DECLINED, DELETED, REVISION_REQUIRED
    val creatorId: String,
    val approvedBy: String?,
    val declinedReason: String?,
    val approvedAt: Long?,
    val declinedAt: Long?,
)
```

### QueOption

```kotlin
data class QueOption(
    val id: String,
    val text: String,
    val value: String?,
    val order: Int,
)
```

### QueConstraints

```kotlin
data class QueConstraints(
    val required: Boolean?,
    val minSelections: Int?,
    val maxSelections: Int?,
    val minValue: Int?,
    val maxValue: Int?,
    val step: Int?,
)
```

### QueAnswer

```kotlin
data class QueAnswer(
    val questionId: String,
    val type: String,
    val selectedOptionIds: ImmutableList<String>?,
    val numericValue: Int?,
    val textValue: String?,
    val userId: String,
    val submittedAt: Long,
)
```

## Question Types

### TWO_CHOICE

- YES/NO
- UP/DOWN
- GOOD/BAD

### THREE_CHOICE

- LOW/MED/HIGH
- AGREE/NEUTRAL/DISAGREE

### Future Types

- MULTI_CHOICE
- SCALE
- OPEN_ENDED

## Repository Interface

```kotlin
interface QuestionRepository {
    fun getQuestionList(): Flow<List<QuestionOperationResponseBody>>
    fun getQuestionsByStatus(status: String): Flow<List<QuestionOperationResponseBody>>
    fun createQuestion(body: QuestionOperationResponseBody): Flow<Unit>
    fun approveQuestion(questionId: String, adminId: String): Flow<Unit>
    fun declineQuestion(questionId: String, adminId: String, reason: String): Flow<Unit>
    fun approveAllPendingQuestions(adminId: String): Flow<Unit>

    // Extended for new features
    fun sendBackForRevision(questionId: String, adminId: String): Flow<Unit>
    fun moveApprovedToPending(questionId: String, adminId: String): Flow<Unit>
    fun movePendingToDeleted(questionId: String, adminId: String): Flow<Unit>
    fun getQuestionsNotAnsweredByUser(userId: String): Flow<List<QuestionOperationResponseBody>>
    fun removeUserAnswer(questionId: String, userId: String): Flow<Unit>
}
```

## ViewModels

### QuestionListVm

```kotlin
class QuestionListVm(
    private val questionRepository: QuestionRepository,
    appDispatchers: AppDispatchers,
) : BaseListViewModel<QuestionViewUiState>(appDispatchers) {
    // Handles question list display and filtering
}
```

### AdminApproveQuestionVm

```kotlin
class AdminApproveQuestionVm(
    private val questionListVm: QuestionListVm,
    private val questionRepository: QuestionRepository,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {
    // Orchestrates QuestionListVm via DI
    // Handles approve/decline/bulk actions
}
```

### QuestionCreateQuestionVm

```kotlin
class QuestionCreateQuestionVm(
    private val questionRepository: QuestionRepository,
    private val navigationUseCase: NavigationUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {
    // Handles creation + preview
}
```

### New ViewModels

```kotlin
class QuestionTypeSelectionVm(
    private val navigationUseCase: NavigationUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers)

class QuestionStyleSelectionVm(
    private val navigationUseCase: NavigationUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers)

class QuestionPreviewVm(
    private val questionRepository: QuestionRepository,
    private val navigationUseCase: NavigationUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers)
```

## Screens

### Existing Screens

- QuestionCreateQuestionScreenSetup (extends existing)
- AdminApproveQuestionScreenSetup (extends existing)

### New Screens

- QuestionTypeSelectionScreenSetup
- QuestionStyleSelectionScreenSetup
- QuestionPreviewScreenSetup

### Screen Setup Pattern (KRİTİK)

**ONLY wiring:**

```kotlin
@Composable
fun QuestionTypeSelectionScreenSetup() {
    val vm = koinViewModel<QuestionTypeSelectionVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    QuestionTypeSelectionScreen(
        uiState = uiState,
        onEvent = vm::onEvent
    )
}
```

**NO business logic in ScreenSetup:**

```kotlin
// YANLIŞ
@Composable
fun QuestionScreenSetup() {
    val vm = koinViewModel<QuestionVm>()

    when (vm.currentState) {  // YANLIŞ! Logic ScreenSetup'ta
        StateA -> navigateToA()
        StateB -> navigateToB()
    }
}

// DOĞRU
@Composable
fun QuestionScreenSetup() {
    val vm = koinViewModel<QuestionVm>()  // Sadece wiring
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    QuestionScreen(uiState = uiState, onEvent = vm::onEvent)
}
```

**Multiple VMs: Inject into parent ViewModel, NOT ScreenSetup:**

```kotlin
// YANLIŞ
@Composable
fun AdminScreenSetup() {
    val listVm = koinViewModel<QuestionListVm>()  // YANLIŞ!
    val adminVm = koinViewModel<AdminVm>()
}

// DOĞRU
class AdminVm(
    private val questionListVm: QuestionListVm,  // Koin DI
) : BaseViewModel()

@Composable
fun AdminScreenSetup() {
    val vm = koinViewModel<AdminVm>()  // Tek VM
}
```

## UI Event Pattern (KRİTİK)

**Wrong: Multiple callbacks**

```kotlin
// YANLIŞ
@Composable
fun AdminScreen(
    onFilterSelected: (QuestionListFilterType) -> Unit,
    onApproveAll: () -> Unit,
    onDeclineAll: () -> Unit,
)
```

**Right: Sealed class Events**

```kotlin
// DOĞRU
sealed class AdminApproveQuestionEvent : BaseEvent() {
    data class OnFilterSelected(val filterType: QuestionListFilterType) :
        AdminApproveQuestionEvent()
    data object OnApproveAll : AdminApproveQuestionEvent()
    data object OnDeclineAll : AdminApproveQuestionEvent()
    data object OnRefreshClicked : AdminApproveQuestionEvent()
}

@Composable
fun AdminScreen(
    uiState: AdminUiState,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
)
```

## CoroutineScope Management

**Use: ViewModelScopeProviderUseCase**

```kotlin
class SomeVm(
    private val viewModelScopeProviderUseCase: ViewModelScopeProviderUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    fun doAsyncWork() {
        viewModelScopeProviderUseCase.getScope().launch {
            // Work here
        }
    }
}
```

**NOT: GlobalScope**

```kotlin
// YANLIŞ - Memory leak risk
GlobalScope.launch {
    // ...
}
```

**Pattern:**

- ViewModelScopeProviderUseCase injects Activity viewModelScope
- Scope destroyed with Activity, no manual cleanup
- Fallback: applicationScope if MainActivity not initialized

## DI Module Example

```kotlin
val questionDiModule = module {
    // ViewModels
    viewModelOf(::QuestionListVm)
    viewModelOf(::AdminApproveQuestionVm)
    viewModelOf(::QuestionCreateQuestionVm)
    viewModelOf(::QuestionTypeSelectionVm)
    viewModelOf(::QuestionStyleSelectionVm)
    viewModelOf(::QuestionPreviewVm)

    // Repositories injected, not created in VM
    single<QuestionRepository> { QuestionRepositoryImpl(get()) }
}
```

## Navigation Pattern

**Use: NavigationUseCase inside ViewModel**

```kotlin
class QuestionTypeSelectionVm(
    private val navigationUseCase: NavigationUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    override fun onEvent(event: Any) {
        if (event is QuestionTypeSelectionEvent) {
            when (event) {
                is OnTypeSelected -> {
                    // Logic here
                    navigationUseCase.navigate(QuestionAppProjectRoutes.QuestionStyleSelection.route)
                }
            }
        }
    }
}
```

**UI: setNavigator**

```kotlin
@Composable
fun QuestionMainScreen(navigationUseCase: NavigationUseCase) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    navigationUseCase.setNavigator { action ->
        coroutineScope.launch {
            if (action == "back") {
                navController.navigateUp()
            } else {
                navController.navigate(action)
            }
        }
    }
}
```

## UiState Pattern

**Use ImmutableList:**

```kotlin
data class QuestionListUiState(
    val questions: ImmutableList<QuestionOperationResponseBody> = persistentListOf(),
    val filterOptions: ImmutableList<FilterOption> = persistentListOf(),
    val selectedFilter: QuestionListFilterType = QuestionListFilterType.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)
```

**Update in ViewModel:**

```kotlin
uiState.update {
    it.copy(questions = newQuestions.toImmutableList())
}
```

## Status Management Rules

### APPROVED Cannot Be Deleted Directly

```kotlin
// UI: Button disabled for APPROVED
Button(
    enabled = question.status == "PENDING",
    onClick = { onEvent(DeleteQuestion(question.id)) }
)

// Repository: Guard
fun movePendingToDeleted(questionId: String, adminId: String): Flow<Unit> = flow {
    val question = getQuestionById(questionId)
    require(question.status == "PENDING") { "Only PENDING can be deleted" }
    // Delete logic
}
```

### Status Flow

```
PENDING → APPROVED (via approve)
PENDING → DECLINED (via decline)
PENDING → DELETED (via delete)
PENDING → REVISION_REQUIRED (via sendBackForRevision)
APPROVED → PENDING (via moveApprovedToPending for re-review)
```

## Özet: Core Rules

1. **ScreenSetup**: Sadece wiring (koinViewModel, collectAsState, onEvent)
2. **Events**: Sealed class, NO callbacks
3. **Navigation**: NavigationUseCase VM içinde
4. **DI**: Koin viewModelOf; repo VM'e inject
5. **UiState**: ImmutableList kullan
6. **Scope**: ViewModelScopeProviderUseCase, NOT GlobalScope
7. **Multiple VMs**: Parent VM'e inject, NOT ScreenSetup
8. **Status**: APPROVED direkt delete edilemez (UI + repo guard)
