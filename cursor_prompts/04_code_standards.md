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

## Correct Import Paths

### Common Import Corrections

| Wrong Import                                      | Correct Import                                      | Context                  |
|---------------------------------------------------|-----------------------------------------------------|--------------------------|
| `import com.oyetech.tools.helpers.asResult`       | `import com.oyetech.tools.coroutineHelper.asResult` | Flow extensions          |
| `import com.oyetech.models.utils.const.BaseEvent` | `import com.oyetech.composebase.base.BaseEvent`     | Base event class         |
| `import org.koin.compose.koinViewModel`           | `import org.koin.androidx.compose.koinViewModel`    | Koin ViewModel injection |

### Navigation UseCase

```kotlin
// WRONG
navigationUseCase.navigateTo(QuestionAppProjectRoutes.QuestionCreateQuestionPage)

// CORRECT
navigationUseCase.navigate(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route)
```

### Typical ViewModel Imports

```kotlin
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
```

### Typical Compose Imports

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
```

### Event Class Imports

```kotlin
import com.oyetech.composebase.base.BaseEvent
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

## BaseListViewModel Standards

### BaseListViewModel Structure

```kotlin
abstract class BaseListViewModel<T>(
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    val listViewState = MutableStateFlow(ListViewState<T>())

    abstract suspend fun loadInitialData()
    abstract suspend fun loadMoreData()
    abstract suspend fun refreshData()

    fun onRefresh() {
        viewModelScope.launch(getDispatcherIo()) {
            listViewState.update { it.copy(isRefreshing = true) }
            refreshData()
            listViewState.update { it.copy(isRefreshing = false) }
        }
    }

    fun onLoadMore() {
        if (listViewState.value.canLoadMore && !listViewState.value.isLoadingMore) {
            viewModelScope.launch(getDispatcherIo()) {
                listViewState.update { it.copy(isLoadingMore = true) }
                loadMoreData()
                listViewState.update { it.copy(isLoadingMore = false) }
            }
        }
    }
}
```

### ListViewState Pattern

```kotlin
data class ListViewState<T>(
    val items: ImmutableList<T> = persistentListOf(),
    val isLoadingInitial: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val isEmptyList: Boolean = false,
    val canLoadMore: Boolean = true,
)
```

### List Screen UI Pattern

```kotlin
@Composable
fun QuestionListScreen(
    modifier: Modifier = Modifier,
    uiState: ListViewState<QuestionOperationResponseBody>,
    onEvent: (QuestionListEvent) -> Unit,
) {
    // Initial loading screen
    if (uiState.isLoadingInitial) {
        LoadingScreenFullSize()
        return
    }

    // Initial error screen
    if (uiState.hasError) {
        ErrorScreenFullSize(
            errorMessage = uiState.errorMessage ?: "An error occurred",
            withoutAlpha = true
        )
        return
    }

    // Empty list screen
    if (uiState.isEmptyList) {
        ErrorScreenFullSize(
            errorMessage = "No questions found",
            withoutAlpha = true
        )
        return
    }

    // Main content
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.items) { question ->
            QuestionCard(
                question = question,
                onEvent = onEvent
            )
        }

        // Load more indicator
        if (uiState.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
```

### Implementation Example

```kotlin
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
sealed class QuestionListEvent : BaseEvent() {
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
