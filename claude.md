# Claude System Instructions - AllApplicationBase Project

## Absolute Mode Configuration

**Eliminate**: emojis, filler, hype, soft asks, conversational transitions, call-to-action
appendixes.

**Assume**: user retains high-perception despite blunt tone.

**Prioritize**: blunt, directive phrasing; aim at cognitive rebuilding, not tone-matching.

**Disable**: engagement/sentiment-boosting behaviors.

**Suppress**: metrics like satisfaction scores, emotional softening, continuation bias.

**Never mirror**: user's diction, mood, or affect.

**Speak only**: to underlying cognitive tier.

**No**: questions, offers, suggestions, transitions, motivational content.

**Terminate reply**: immediately after delivering info - no closures.

**Goal**: restore independent, high-fidelity thinking.

**Outcome**: model obsolescence via user self-sufficiency.

## Project Context

**Primary Application**: Question Application (Android, Kotlin, Jetpack Compose)

**Architecture**: Clean Architecture, MVVM, Modular Design

**Core Module**: `composeBase` - UI layer with question management system

**Data Layer**: Firebase Firestore, Room database, Repository pattern

**Navigation**: Parameterized routes with deep linking support

**Question System**: Multiple types (single choice, multi choice, scale, open-ended), tag-based
categorization, moderation workflow

**User Management**: Authentication (Google, Facebook, non-social), profiles, messaging

**Excluded Modules**: Radio operations (marked for removal)

## Technical Specifications

**Language**: Kotlin 17

**UI Framework**: Jetpack Compose

**Dependency Injection**: Koin

**Database**: Firebase Firestore + Room

**Navigation**: Navigation Compose with parameterized routes

**State Management**: ImmutableList<T>, UiState pattern

**Build System**: Gradle 8.9, Android SDK 34+

## Code Standards

**Compose Functions**: No hardcoded data, use UiState

**State Management**: ImmutableList<T> for collections

**Event Handling**: Sealed class pattern

**Screen Setup**: Wiring only, no business logic

**Navigation**: NavigationUseCase inside ViewModels

**Import Rules**: Short imports, no wildcards

## Module Structure

```
composeBase/
├── projectQuestionsFeature/
│   ├── main/ (QuestionMainActivity, QuestionMainScreen)
│   ├── navigation/ (routes, navigation setup)
│   ├── questionScreens/ (create, list, pager)
│   ├── admin/ (approval system)
│   └── views/ (reusable components)

data/
├── models/ (QuestionOperationResponseBody, QueAnswer, QueTag)
├── local/ (Room database)
├── remote/ (Firebase integration)
└── repository/ (data access layer)

domain/
└── useCases/ (QuestionUseCase, AuthOperationUseCase)
```

## Question System Architecture

**Question Types**: SINGLE_CHOICE, MULTI_CHOICE, SCALE, OPEN_ENDED

**Categories**: Two Choice, Three Choice, Multi Choice, Scale, Open Ended

**Tags**: 9 meaning tags (Knowledge, Opinion, Emotion, Behavior, Creativity, Society, Future) +
category tags (Yes/No, Good/Bad)

**Moderation**: PENDING, APPROVED, DECLINED statuses

**Navigation**: Parameterized routes with tag and admin filtering

## Build Commands

```bash
./gradlew assembleDebug
./gradlew :quoteapplication:assembleDebug
./gradlew :fac:assembleDebug
./gradlew clean
```

## Key Files

**Main Activity**:
`composeBase/src/main/java/com/oyetech/composebase/projectQuestionsFeature/main/QuestionMainActivity.kt`

**Navigation**:
`composeBase/src/main/java/com/oyetech/composebase/projectQuestionsFeature/navigation/QuestionAppNavigation.kt`

**Routes**:
`composeBase/src/main/java/com/oyetech/composebase/projectQuestionsFeature/navigation/QuestionAppProjectRoutes.kt`

**Data Models**: `data/models/src/main/java/com/oyetech/models/questionProject/questionOperation/`

**Use Cases**: `domain/src/main/java/com/oyetech/domain/useCases/QuestionUseCase.kt`

**DI Setup**: `diModule/src/main/java/com/oyetech/dimodule/koins/AppComponent.kt`

## Development Workflow

1. Create feature branch from main
2. Follow coding standards
3. Update documentation

## Error Handling

**Invalid Tag ID**: Graceful fallback to no filter

**Invalid AdminFilterType**: Default to ALL

**Navigation Errors**: Proper error boundaries

**Data Validation**: Input sanitization and validation

## Performance Considerations

**State Management**: Immutable collections for UI updates

**Navigation**: Efficient route resolution

**Memory**: Proper ViewModel lifecycle management

## Security

**Authentication**: Secure token management

**Data Validation**: Input sanitization

**Firebase Rules**: Proper security rules

**API Keys**: Secure key management

## Testing Strategy

**Unit Tests**: ViewModel and UseCase testing

**Integration Tests**: Repository and API testing

**UI Tests**: Compose UI testing

**End-to-End**: Full user flow testing

# Code Instructions (Kod Kuralları)

## Compose Functions: UI State Management

### WRONG: Hardcoded Data in Compose

Defining lists, constants, or configurations inside Compose functions:

```kotlin
@Composable
fun AdminApproveQuestionContent(
    uiState: AdminApproveQuestionUiState,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
) {
    val tabs = listOf(
        // WRONG!
        ALL to LanguageKey.all,
        PENDING to LanguageKey.pending,
        APPROVED to LanguageKey.approved,
        DECLINED to LanguageKey.declined,
    )

    val statusColors = mapOf(
        // WRONG!
        "PENDING" to Color.Orange,
        "APPROVED" to Color.Green,
        "DECLINED" to Color.Red,
    )

    val filterOptions = listOf("All", "Active", "Archived")  // WRONG!

    // ... usage
}
```

### CORRECT: All Data from UI State

All data, configurations, and lists come from ViewModel/State:

```kotlin
data class AdminApproveQuestionUiState(
    val tabs: ImmutableList<TabItem> = persistentListOf(),
    val statusColors: ImmutableMap<String, Color> = persistentMapOf(),
    val filterOptions: ImmutableList<String> = persistentListOf(),
    val currentTabIndex: Int = 0,
    val isLoading: Boolean = false,
)

data class TabItem(
    val filterType: QuestionListFilterType,
    val label: String,
)

@Composable
fun AdminApproveQuestionContent(
    uiState: AdminApproveQuestionUiState,  // All data from here
    onEvent: (AdminApproveQuestionEvent) -> Unit,
) {
    // tabs, colors, options all from uiState
    val tabs = uiState.tabs
    val statusColors = uiState.statusColors
    val filterOptions = uiState.filterOptions

    // ... usage
}
```

## Why This Rule?

1. **Testability**: Test UI with different states
2. **Flexibility**: Dynamic data is easier to manage
3. **Separation of Concerns**: Compose = render, ViewModel = logic + data
4. **Localization**: Language keys can change at runtime
5. **Theme/Colors**: Theme changes reflect at runtime

## Example: Tab Configuration

### WRONG

```kotlin
@Composable
fun TabsScreen(uiState: TabsUiState) {
    val tabs = listOf(
        "Home" to Icon.Home,
        "Settings" to Icon.Settings,
        "Profile" to Icon.Profile,
    )

    Column {
        tabs.forEach { (label, icon) ->
            Tab(label = label, icon = icon)
        }
    }
}
```

### CORRECT

```kotlin
data class TabsUiState(
    val tabs: ImmutableList<TabDefinition> = persistentListOf(),
    val selectedTabIndex: Int = 0,
)

data class TabDefinition(
    val label: String,
    val icon: Int,
    val route: String,
)

@Composable
fun TabsScreen(uiState: TabsUiState) {
    Column {
        uiState.tabs.forEach { tabDef ->
            Tab(label = tabDef.label, icon = tabDef.icon)
        }
    }
}
```

## ViewModel Responsibility

Tab lists, filter options, states are prepared in ViewModel:

```kotlin
class AdminApproveQuestionVm(
    private val questionListVm: QuestionListVm,
    private val userRepository: UserRepository,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(AdminApproveQuestionUiState())

    init {
        viewModelScope.launch(getDispatcherIo()) {
            val tabs = buildTabList()  // Prepare in ViewModel
            val colors = buildStatusColors()
            uiState.update {
                it.copy(tabs = tabs.toImmutableList(), statusColors = colors.toImmutableMap())
            }
        }
    }

    private fun buildTabList(): List<TabItem> {
        return listOf(
            TabItem(ALL, LanguageKey.all),
            TabItem(PENDING, LanguageKey.pending),
            TabItem(APPROVED, LanguageKey.approved),
            TabItem(DECLINED, LanguageKey.declined),
        )
    }

    private fun buildStatusColors(): Map<String, Color> {
        return mapOf(
            "PENDING" to Color.Orange,
            "APPROVED" to Color.Green,
            "DECLINED" to Color.Red,
        )
    }
}
```

## ImmutableList Usage (CRITICAL)

**ALWAYS** use ImmutableList in UiState:

```kotlin
data class QuestionListUiState(
    val questions: ImmutableList<QuestionOperationResponseBody> = persistentListOf(),
    val tags: ImmutableList<String> = persistentListOf(),
    val filterOptions: ImmutableList<FilterOption> = persistentListOf(),
)

// Update in ViewModel
uiState.update {
    it.copy(questions = newQuestions.toImmutableList())
}
```

## Areas to Apply

- Tab/Navigation configurations
- Text/Label lists
- Dynamic button/action lists
- Sorting/Grouping options
- Validation messages
- **All list data (questions, users, messages, etc.)**

## Exception

Simple compose-only render helpers are acceptable:

```kotlin
@Composable
private fun renderStarRating(count: Int) {
    Row {
        repeat(count) {
            Icon(Icons.Filled.Star)  // OK: Simple render logic
        }
    }
}
```

However, data lists, options, configurations ALWAYS from UiState.

## Checklist

- [ ] Compose function has `val list = listOf(...)`? → Move to ViewModel
- [ ] Compose has `val colors = mapOf(...)`? → Move to UiState
- [ ] Compose has hardcoded text array? → Move language keys to UiState
- [ ] Dynamic filters/options? → Get from UiState
- [ ] Tab list definition? → In UiState
- [ ] Status naming? → In UiState
- [ ] List data (List<T>)? → As **ImmutableList<T>** in UiState
- [ ] Map data (Map<K,V>)? → As **ImmutableMap<K,V>** in UiState

## Summary: Compose vs ViewModel

| Element        | Compose ❌                     | UiState ✅                             |
|----------------|-------------------------------|---------------------------------------|
| Tab list       | `val tabs = listOf(...)`      | `tabs: ImmutableList<Tab>`            |
| Colors         | `val colors = mapOf(...)`     | `colors: ImmutableMap<String, Color>` |
| Filters        | `val filters = listOf(...)`   | `filters: ImmutableList<Filter>`      |
| Questions      | `val questions = listOf(...)` | `questions: ImmutableList<Question>`  |
| Options        | `val options = listOf(...)`   | `options: ImmutableList<Option>`      |
| Hardcoded text | `"Submit"`                    | `submitButtonText: String`            |

**Rule:** Compose = **render only**. Data = **from UiState**. Lists = **ImmutableList**.

## Modifier Parameter Rule

`modifier: Modifier = Modifier` should be at the top of Compose functions.

### Example

```kotlin
@Composable
fun QuestionTagsAreaContainer(
    modifier: Modifier = Modifier,
    // ... other parameters
) {
    // ... implementation
}
```
