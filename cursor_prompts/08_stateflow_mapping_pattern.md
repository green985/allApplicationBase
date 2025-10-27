# StateFlow Mapping Pattern

## Problem: Race Condition with State and Data Initialization

### Issue Description

When setting state (like admin view mode) before data initializes, the state is lost because it only
updates existing items in the list. If the list loads after the state change, the state won't be
applied.

**Example Problem:**

```kotlin
// WRONG: Updates existing items only
is QuestionViewEvent.SetAdminMode -> {
    viewModelScope.launch(getDispatcherIo()) {
        val currentList = listViewState.value.items.map { item ->
            item.copy(isAdminView = event.isAdminView)
        }
        listViewState.value = listViewState.value.copy(items = currentList.toImmutableList())
    }
}
```

**Race Condition:**

1. User sets admin mode → Updates empty list
2. Data loads from network → Admin mode flag is lost

## Solution: StateFlow + Flow Mapping

Use a separate StateFlow to persist the state and map it to every item in the data flow.

### Implementation Pattern

#### Step 1: Add StateFlow for Persistent State

```kotlin
class QuestionListVm(
    appDispatchers: AppDispatchers,
    // ... other dependencies
) : BaseListViewModel<QuestionViewUiState>(appDispatchers) {

    val uiState = MutableStateFlow(QuestionListUiState())
    private val queFilter = MutableStateFlow<QueFilter?>(null)

    // Add StateFlow to persist admin view state
    private val adminViewState = MutableStateFlow(false)

    // ... rest of the code
}
```

#### Step 2: Map StateFlow to Data Flow

```kotlin
fun getQuestionDataFlow(): Flow<List<QuestionViewUiState>> {
    return queFilter.filterNotNull().flatMapLatest {
        questionUseCase.getQuestionListWithUpdates(
            moderationStatus = it.adminFilterType.toModerationStatusOrNull(),
            tag = it.selectedTagFilter
        )
    }.combine(answerRepository.answersState) { questions, answers ->
        overlayAnswers(questions, answers)
    }.combine(adminViewState) { questions, isAdminView ->
        // Map admin view state to every question
        questions.map { question ->
            question.copy(isAdminView = isAdminView)
        }
    }
}
```

#### Step 3: Update StateFlow Instead of List Items

```kotlin
is QuestionViewEvent.SetAdminMode -> {
    // Simply update the StateFlow
    adminViewState.value = event.isAdminView
    Timber.d("Admin view mode set to: ${event.isAdminView}")
}
```

## Benefits

### 1. No Race Conditions

State is always applied regardless of when data loads:

- Set admin mode → Load data → Admin mode is applied ✅
- Load data → Set admin mode → Admin mode is applied ✅

### 2. Reactive Updates

When admin state changes, all questions automatically update through the flow.

### 3. Single Source of Truth

Admin state lives in one place (`adminViewState`), not scattered across list items.

### 4. Cleaner Code

No need to manually update every item in the list.

### 5. Performance

Flow operators handle updates efficiently.

## Common Use Cases

### Use Case 1: Admin/View Mode Flags

```kotlin
private val isAdminMode = MutableStateFlow(false)

fun getDataFlow() = dataSource.getItems()
    .combine(isAdminMode) { items, isAdmin ->
        items.map { it.copy(isAdminView = isAdmin) }
    }
```

### Use Case 2: Selection State

```kotlin
private val selectedItemIds = MutableStateFlow<Set<String>>(emptySet())

fun getDataFlow() = dataSource.getItems()
    .combine(selectedItemIds) { items, selectedIds ->
        items.map { it.copy(isSelected = selectedIds.contains(it.id)) }
    }
```

### Use Case 3: Filter/Sort State

```kotlin
private val sortOrder = MutableStateFlow(SortOrder.DATE_DESC)

fun getDataFlow() = dataSource.getItems()
    .combine(sortOrder) { items, order ->
        when (order) {
            SortOrder.DATE_ASC -> items.sortedBy { it.date }
            SortOrder.DATE_DESC -> items.sortedByDescending { it.date }
            SortOrder.TITLE -> items.sortedBy { it.title }
        }
    }
```

### Use Case 4: User Preferences

```kotlin
private val showArchived = MutableStateFlow(false)

fun getDataFlow() = dataSource.getItems()
    .combine(showArchived) { items, includeArchived ->
        if (includeArchived) items
        else items.filter { !it.isArchived }
    }
```

## Pattern Template

```kotlin
class MyListVm(
    appDispatchers: AppDispatchers,
    private val repository: MyRepository,
) : BaseListViewModel<MyItemUiState>(appDispatchers) {

    // Step 1: Create StateFlow for persistent state
    private val myPersistentState = MutableStateFlow(initialValue)

    // Step 2: Map state to data flow
    override val listViewState: MutableStateFlow<GenericListState<MyItemUiState>> =
        MutableStateFlow(
            GenericListState(
                dataFlow = getDataFlow()
            )
        )

    private fun getDataFlow(): Flow<List<MyItemUiState>> {
        return repository.getItems()
            .combine(myPersistentState) { items, state ->
                items.map { item ->
                    item.copy(someProperty = state)
                }
            }
    }

    // Step 3: Update StateFlow in events
    override fun onEvent(event: Any) {
        if (event is MyEvent) {
            when (event) {
                is MyEvent.SetState -> {
                    myPersistentState.value = event.value
                }
            }
        }
    }
}
```

## Multiple StateFlows

You can combine multiple StateFlows:

```kotlin
private val adminViewState = MutableStateFlow(false)
private val selectedIds = MutableStateFlow<Set<String>>(emptySet())
private val sortOrder = MutableStateFlow(SortOrder.DATE_DESC)

fun getDataFlow(): Flow<List<QuestionViewUiState>> {
    return repository.getQuestions()
        .combine(adminViewState) { items, isAdmin ->
            items.map { it.copy(isAdminView = isAdmin) }
        }
        .combine(selectedIds) { items, ids ->
            items.map { it.copy(isSelected = ids.contains(it.id)) }
        }
        .combine(sortOrder) { items, order ->
            items.sortedWith(order.comparator)
        }
}
```

## Best Practices

### DO ✅

1. Use StateFlow for UI-related state that affects data presentation
2. Keep StateFlow private
3. Update StateFlow directly in event handlers
4. Use `combine` operator for mapping
5. Log state changes for debugging

```kotlin
is MyEvent.SetState -> {
    myState.value = event.value
    Timber.d("State updated to: ${event.value}")
}
```

### DON'T ❌

1. Don't manually update list items
2. Don't use mutable collections for state
3. Don't create new flows on every event
4. Don't forget to map state to new data

```kotlin
// WRONG
is MyEvent.SetState -> {
    listViewState.value.items.forEach { item ->
        item.someProperty = event.value  // Mutating items
    }
}
```

## When to Use This Pattern

✅ **Use when:**

- State affects how data is displayed
- State can change before or after data loads
- State should persist across data refreshes
- Multiple items share the same state

❌ **Don't use when:**

- State is specific to one item
- State doesn't need to persist
- Simple one-time transformations

## Debugging

### Check StateFlow Value

```kotlin
init {
    viewModelScope.launch(getDispatcherIo()) {
        adminViewState.collect { isAdmin ->
            Timber.d("Admin view state changed: $isAdmin")
        }
    }
}
```

### Log Flow Transformations

```kotlin
fun getDataFlow(): Flow<List<QuestionViewUiState>> {
    return repository.getQuestions()
        .onEach { Timber.d("Raw questions: ${it.size}") }
        .combine(adminViewState) { items, isAdmin ->
            Timber.d("Applying admin mode: $isAdmin to ${items.size} items")
            items.map { it.copy(isAdminView = isAdmin) }
        }
        .onEach { Timber.d("Final questions: ${it.size}") }
}
```

## Summary

**Problem:** Race condition between state updates and data loading

**Solution:** Use StateFlow + Flow.combine() to map state to every item

**Benefits:**

- No race conditions
- Reactive updates
- Single source of truth
- Cleaner code
- Better performance

**Key Pattern:**

1. Create StateFlow for persistent state
2. Combine with data flow using `.combine()`
3. Map state to each item in the flow
4. Update StateFlow (not list items) in events
