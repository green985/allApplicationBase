# Architecture Rules - Clean Architecture & MVVM

## Code Standards

**Compose Functions**: No hardcoded data, use UiState

**State Management**: ImmutableList<T> for collections

**Event Handling**: Sealed class pattern

**Screen Setup**: Wiring only, no business logic

**Navigation**: NavigationUseCase inside ViewModels

**Import Rules**: Short imports, no wildcards

## Clean Architecture Principles

### Layer Separation

1. **Presentation Layer** (composeBase)
    - UI Components (Compose)
    - ViewModels
    - UI State Management

2. **Domain Layer** (domain)
    - Use Cases
    - Business Logic
    - Repository Interfaces

3. **Data Layer** (data)
    - Repository Implementations
    - Local Database (Room)
    - Remote API (Firebase)

### MVVM Pattern

**Model**: Data classes, entities
**View**: Compose UI components
**ViewModel**: State management, business logic coordination

## State Management Rules

### UiState Pattern

```kotlin
data class QuestionListUiState(
    val questions: ImmutableList<QuestionOperationResponseBody> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentFilter: QuestionListFilterType = ALL,
)
```

### Event Handling

```kotlin
sealed class QuestionListEvent : BaseEvent() {
    object LoadQuestions : QuestionListEvent()
    data class FilterByTag(val tagId: String) : QuestionListEvent()
    data class SelectQuestion(val questionId: String) : QuestionListEvent()
}
```

### ViewModel Structure

```kotlin
class QuestionListVm(
    private val questionUseCase: QuestionUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(QuestionListUiState())

    fun onEvent(event: QuestionListEvent) {
        when (event) {
            is QuestionListEvent.LoadQuestions -> loadQuestions()
            is QuestionListEvent.FilterByTag -> filterByTag(event.tagId)
            is QuestionListEvent.SelectQuestion -> selectQuestion(event.questionId)
        }
    }

    private fun loadQuestions() {
        viewModelScope.launch(getDispatcherIo()) {
            uiState.update { it.copy(isLoading = true) }
            try {
                val questions = questionUseCase.getQuestions()
                uiState.update {
                    it.copy(
                        questions = questions.toImmutableList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                uiState.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }
}
```

## Navigation Rules

### Parameterized Routes

```kotlin
object QuestionAppProjectRoutes {
    const val QUESTION_LIST = "question_list/{tagId?}"
    const val QUESTION_DETAIL = "question_detail/{questionId}"
    const val ADMIN_APPROVE = "admin_approve/{filterType?}"
}
```

### Navigation in ViewModels

```kotlin
class QuestionListVm(
    private val navigationUseCase: NavigationUseCase,
    // ... other dependencies
) : BaseViewModel(appDispatchers) {

    fun navigateToQuestionDetail(questionId: String) {
        navigationUseCase.navigateTo(
            QuestionAppProjectRoutes.QUESTION_DETAIL.replace("{questionId}", questionId)
        )
    }
}
```

## Repository Pattern

### Interface Definition

```kotlin
interface QuestionRepository {
    suspend fun getQuestions(): List<QuestionOperationResponseBody>
    suspend fun getQuestionsByTag(tagId: String): List<QuestionOperationResponseBody>
    suspend fun createQuestion(question: QuestionOperationResponseBody): Result<Unit>
    suspend fun updateQuestion(question: QuestionOperationResponseBody): Result<Unit>
    suspend fun deleteQuestion(questionId: String): Result<Unit>
}
```

### Implementation

```kotlin
class QuestionRepositoryImpl(
    private val localDataSource: QuestionLocalDataSource,
    private val remoteDataSource: QuestionRemoteDataSource,
) : QuestionRepository {

    override suspend fun getQuestions(): List<QuestionOperationResponseBody> {
        return try {
            val questions = remoteDataSource.getQuestions()
            localDataSource.cacheQuestions(questions)
            questions
        } catch (e: Exception) {
            localDataSource.getCachedQuestions()
        }
    }
}
```

## Dependency Injection (Koin)

### Module Definition

```kotlin
val questionModule = module {
    single<QuestionRepository> { QuestionRepositoryImpl(get(), get()) }
    single { QuestionUseCase(get()) }
    viewModel { QuestionListVm(get(), get()) }
}
```

### Usage in Activity

### Usage in compose screen

```kotlin
class QuestionMainActivity : ComponentActivity() {
    private val viewModel: QuestionListVm by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... setup
    }
}


@Composable
fun AdminApproveQuestionScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<AdminApproveQuestionVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

```

## Error Handling

### Result Pattern

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### Error Handling in ViewModels

```kotlin
private fun handleError(exception: Throwable) {
    val errorMessage = when (exception) {
        is NetworkException -> "Network error occurred"
        is ValidationException -> exception.message
        else -> "An unexpected error occurred"
    }
    uiState.update { it.copy(error = errorMessage) }
}
```

## Performance Considerations

### State Updates

- Use `ImmutableList` for collections
- Update state immutably with `copy()`
- Avoid unnecessary recompositions

### Memory Management

- Proper ViewModel lifecycle
- Dispose resources in `onCleared()`
- Use appropriate coroutine scopes

