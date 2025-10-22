# Self-Enforcement Rules for AI Assistant

## MANDATORY: Always Load Documentation

**RULE 1**: Before making ANY code changes, I MUST load the appropriate documentation files from
`cursor_prompts/` folder.

**RULE 2**: I MUST reference specific rules when making changes.

**RULE 3**: I MUST NOT skip documentation loading for any reason.

## Documentation Loading Protocol

### Step 1: Determine Task Type

- UI/Compose changes → Load `02_compose_rules.md` + `01_project_context.md`
- Architecture changes → Load `01_project_context.md` + `03_architecture_rules.md`
- New features → Load `01_project_context.md` + `04_code_standards.md`
- Bug fixes → Load `04_code_standards.md` + `02_compose_rules.md`
- Database changes → Load `01_project_context.md` + `03_architecture_rules.md`
- Navigation changes → Load `02_compose_rules.md` + `01_project_context.md`

### Step 2: Load Documentation

```bash
# ALWAYS load README first
read_file cursor_prompts/00_README.md

# Then load relevant files based on task type
read_file cursor_prompts/01_project_context.md
read_file cursor_prompts/02_compose_rules.md
# ... etc
```

### Step 3: Reference Rules

When making changes, explicitly reference which rules I'm following:

- "Following Compose Rules: Moving hardcoded data to UiState"
- "Following Architecture Rules: Using ImmutableList for collections"
- "Following Code Standards: Using proper naming conventions"

## Compose Rules Enforcement

### MANDATORY Compose Checks

Before writing ANY Compose function, I MUST verify:

1. **No hardcoded data**: All lists, colors, text must come from UiState
2. **ImmutableList usage**: All collections must be ImmutableList<T>
3. **Modifier parameter**: `modifier: Modifier = Modifier` at top
4. **Separation of concerns**: Compose = render only, ViewModel = logic

### Example Enforcement

```kotlin
// WRONG - I MUST NOT write this
@Composable
fun QuestionList() {
    val questions = listOf(question1, question2) // WRONG!
    val colors = mapOf("PENDING" to Color.Orange) // WRONG!
}

// CORRECT - I MUST write this
@Composable
fun QuestionList(
    modifier: Modifier = Modifier,
    uiState: QuestionListUiState,
    onEvent: (QuestionListEvent) -> Unit,
) {
    val questions = uiState.questions // CORRECT!
    val colors = uiState.statusColors // CORRECT!
}
```

## Architecture Rules Enforcement

### MANDATORY Architecture Checks

Before making architectural changes, I MUST verify:

1. **Layer separation**: UI, Domain, Data layers properly separated
2. **MVVM pattern**: ViewModels handle state, Compose handles UI
3. **Repository pattern**: Use interfaces, implement in data layer
4. **State management**: UiState pattern with sealed events

### Example Enforcement

```kotlin
// WRONG - I MUST NOT write this
class QuestionScreen {
    fun loadQuestions() {
        // Business logic in UI layer - WRONG!
    }
}

// CORRECT - I MUST write this
class QuestionListVm {
    fun onEvent(event: QuestionListEvent) {
        when (event) {
            is LoadQuestions -> loadQuestions()
        }
    }
    
    private fun loadQuestions() {
        // Business logic in ViewModel - CORRECT!
    }
}
```

## Code Standards Enforcement

### MANDATORY Code Quality Checks

Before writing ANY code, I MUST verify:

1. **Naming conventions**: Proper class, function, variable names
2. **Import rules**: Short imports, no wildcards
3. **File organization**: Proper package structure
4. **Error handling**: Proper exception handling patterns

## Self-Monitoring Protocol

### Before Every Code Change

1. Load appropriate documentation
2. Identify which rules apply
3. Reference specific rules in my response
4. Verify compliance before proceeding

### During Code Changes

1. Check each line against relevant rules
2. Ensure ImmutableList usage for collections
3. Verify no hardcoded data in Compose
4. Confirm proper architecture patterns

### After Code Changes

1. Review changes against documentation
2. Confirm all rules were followed
3. Reference specific rules in explanation

## Violation Consequences

If I violate these rules:

1. I MUST immediately correct the violation
2. I MUST reload the documentation
3. I MUST redo the changes following proper rules
4. I MUST acknowledge the mistake and explain the correction

## Documentation References

### Compose Rules References

- "Following Compose Rules: No hardcoded data in Compose functions"
- "Following Compose Rules: Using ImmutableList for all collections"
- "Following Compose Rules: Modifier parameter at top of function"

### Architecture Rules References

- "Following Architecture Rules: Proper MVVM separation"
- "Following Architecture Rules: Repository pattern implementation"
- "Following Architecture Rules: UiState pattern for state management"

### Code Standards References

- "Following Code Standards: Proper naming conventions"
- "Following Code Standards: Short imports, no wildcards"
- "Following Code Standards: Proper error handling patterns"

## MANDATORY Acknowledgment

Every response MUST include:

1. Which documentation files I loaded
2. Which specific rules I'm following
3. How I'm applying those rules to the current task

## Example Response Format

```
I've loaded the following documentation:
- cursor_prompts/00_README.md
- cursor_prompts/02_compose_rules.md
- cursor_prompts/01_project_context.md

Following Compose Rules: Moving hardcoded data to UiState
Following Architecture Rules: Using ImmutableList for collections
Following Code Standards: Proper naming conventions

[Then proceed with the actual code changes]
```

## NO EXCEPTIONS

These rules apply to:

- Small changes (10 lines)
- Large changes (multiple files)
- Bug fixes
- New features
- Refactoring
- Any code modification

**I MUST NEVER skip documentation loading or rule enforcement.**
