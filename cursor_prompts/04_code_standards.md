# Code Standards - General Patterns

## Development Workflow

1. Create feature branch from main
2. Follow coding standards
3. Update documentation

## Import Rules

**Short imports**: Use specific imports, not wildcards
**No wildcards**: Avoid `import com.oyetech.*`
**Organized imports**: Group by package, alphabetical order

```kotlin
// GOOD
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// BAD
import androidx.compose.*
import androidx.compose.foundation.*
```

## Naming Conventions

### Classes

- **Activities**: `QuestionMainActivity`
- **ViewModels**: `QuestionListVm` (short for ViewModel)
- **Use Cases**: `QuestionUseCase`
- **Repositories**: `QuestionRepository`
- **Data Classes**: `QuestionOperationResponseBody`

### Functions

- **Compose Functions**: PascalCase with descriptive names
- **Private Functions**: camelCase
- **Event Handlers**: `onEvent`

### Variables

- **UI State**: `uiState`
- **Lists**: `questions`, `tags`, `filterOptions`
- **Booleans**: `isLoading`, `hasError`, `isSelected`

## File Organization

### Package Structure

```
com.oyetech.composebase.projectQuestionsFeature/
├── main/
│   ├── QuestionMainActivity.kt
│   └── QuestionMainScreen.kt
├── navigation/
│   ├── QuestionAppNavigation.kt
│   └── QuestionAppProjectRoutes.kt
├── questionScreens/
│   ├── QuestionListScreen.kt
│   ├── QuestionCreateScreen.kt
│   └── QuestionPagerScreen.kt
├── admin/
│   ├── AdminApproveQuestionScreen.kt
│   └── AdminApproveQuestionVm.kt
└── views/
    ├── QuestionCard.kt
    └── QuestionFilterChip.kt
```

### File Naming

- **Screens**: `{Feature}Screen.kt`
- **ViewModels**: `{Feature}Vm.kt`
- **Components**: `{ComponentName}.kt`
- **Routes**: `{App}Routes.kt`

## Code Structure

### Compose Function Structure

```kotlin
@Composable
fun QuestionCard(
    modifier: Modifier = Modifier,
    questionUiState: QuestionUiState,
    onEvent: (QuestionEvent) -> Unit,
) {
    // Implementation
}
```

### ViewModel Structure

```kotlin
class QuestionListVm(
    private val questionUseCase: QuestionUseCase,
    private val navigationUseCase: NavigationUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(QuestionListUiState())

    override fun onEvent(event: Any) {
        if (event is QuestionListEvent) {
            when (event) {
                QuestionListEvent.OnRefreshClicked -> refreshList()
                is QuestionListEvent.OnItemClicked -> onItemClicked(event.item)
                is QuestionListEvent.OnTagFilterChanged -> setTagFilter(event.tag)
            }
        }
    }

    private fun loadQuestions() {
        // Private implementation
    }
}
```

### Data Class Structure

```kotlin
data class QuestionOperationResponseBody(
    val id: String,
    val title: String,
    val content: String,
    val tags: List<String>,
    val type: QuestionType,
    val status: QuestionStatus,
    val createdAt: Long,
    val updatedAt: Long,
)
```

## Error Handling Standards

### Exception Types

```kotlin
throw GeneralException("Network error occurred")
```

### Error Handling in UI

```kotlin
@Composable
fun QuestionListContent(
    uiState: QuestionListUiState,
    onEvent: (QuestionListEvent) -> Unit,
) {
    when {
        uiState.isLoading -> LoadingIndicator()
        uiState.error != null -> ErrorMessage(
            message = uiState.error,
            onRetry = { onEvent(QuestionListEvent.LoadQuestions) }
        )
        uiState.questions.isEmpty() -> EmptyState()
        else -> QuestionList(
            questions = uiState.questions,
            onQuestionClick = { onEvent(QuestionListEvent.SelectQuestion(it)) }
        )
    }
}
```

## State Management Standards

### UiState Pattern

```kotlin
data class QuestionListUiState(
    val questions: ImmutableList<QuestionOperationResponseBody> = persistentListOf(),
    val tags: ImmutableList<String> = persistentListOf(),
    val filterOptions: ImmutableList<FilterOption> = persistentListOf(),
    val currentFilter: QuestionListFilterType = ALL,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedQuestionId: String? = null,
)
```

### Event Pattern

```kotlin
sealed class QuestionListEvent {
    object LoadQuestions : QuestionListEvent()
    data class FilterByTag(val tagId: String) : QuestionListEvent()
    data class SelectQuestion(val questionId: String) : QuestionListEvent()
    object ClearError : QuestionListEvent()
    object Refresh : QuestionListEvent()
}
```

## Performance Standards

### Compose Performance

- Use `ImmutableList` for collections
- Avoid unnecessary recompositions
- Use `remember` for expensive calculations
- Use `LazyColumn` for large lists

### Memory Management

- Dispose resources in ViewModel `onCleared()`
- Use appropriate coroutine scopes
- Avoid memory leaks in long-running operations

## Testing Standards

### Unit Test Structure

```kotlin
class QuestionListVmTest {
    private lateinit var viewModel: QuestionListVm
    private lateinit var mockQuestionUseCase: QuestionUseCase

    @Before
    fun setup() {
        mockQuestionUseCase = mockk()
        viewModel = QuestionListVm(mockQuestionUseCase, mockk(), mockk())
    }

    @Test
    fun `when loadQuestions is called, should update uiState with questions`() {
        // Given
        val mockQuestions = listOf(question1, question2)
        coEvery { mockQuestionUseCase.getQuestions() } returns mockQuestions

        // When
        viewModel.onEvent(QuestionListEvent.LoadQuestions)

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.questions).isEqualTo(mockQuestions.toImmutableList())
    }
}
```

## Documentation Standards

### Function Documentation

```kotlin
/**
 * Displays a list of questions with filtering capabilities
 *
 * @param modifier Modifier for styling
 * @param uiState Current UI state containing questions and filters
 * @param onEvent Event handler for user interactions
 */
@Composable
fun QuestionListContent(
    modifier: Modifier = Modifier,
    uiState: QuestionListUiState,
    onEvent: (QuestionListEvent) -> Unit,
) {
    // Implementation
}
```

### Class Documentation

```kotlin
/**
 * ViewModel for managing question list state and business logic
 *
 * Handles:
 * - Loading questions from repository
 * - Filtering questions by tags
 * - Navigation to question details
 * - Error state management
 */
class QuestionListVm(
    private val questionUseCase: QuestionUseCase,
    private val navigationUseCase: NavigationUseCase,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {
    // Implementation
}
```

## Security Standards

### Data Validation

- Validate all user inputs
- Sanitize data before processing
- Use proper authentication checks

### API Security

- Secure API key management
- Proper error handling without exposing sensitive data
- Input validation on both client and server

## Build and Deployment

### Gradle Configuration

- Use consistent version catalogs
- Proper dependency management
- Build variant configurations

### Code Quality

- Use detekt for static analysis
- Follow linting rules
- Maintain code coverage standards
