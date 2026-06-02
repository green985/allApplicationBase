# Core Behavior Rules

Before responding to **any** request, read and strictly apply all rules in:
`copilot_files/first_rules.md`

Then read the project architecture and coding conventions in:
`copilot_files/AGENTS.md`

# Code Generation Rules

Never create architecture.

Follow architecture from AGENTS.md.

If AGENTS.md already defines a pattern,

reuse that pattern.

Do not invent alternative patterns.

Generate only the code required for the current task.

Do not create files unless explicitly requested.

Do not modify unrelated files.

Do not rename existing classes, functions, variables, models or routes unless explicitly requested.

Preserve existing naming conventions.

Preserve existing project structure.

---

## Minimal Changes Rule

Make the smallest valid change.

Prefer editing existing code over creating new code.

Do not refactor unrelated code.

Do not clean up unrelated code.

Do not optimize unrelated code.

Do not move code unless required by the task.

Do not expand scope.

Implement only the requested change.

Do not solve problems that were not requested.

---

## Existing Code First

Before creating:

- a new class
- a new interface
- a new model
- a new composable
- a new use case
- a new repository

search for an existing implementation and reuse it.

Prefer extension of existing code over duplication.

If an existing implementation already solves the problem,

modify it instead of creating a new implementation.

---

## Consistency Rule

Match the style of surrounding code.

Match:

- naming
- formatting
- architecture
- state management
- event handling

Do not introduce a different style.

---

## Compose Rules

Prefer stateless composables.

Pass state through parameters.

Pass events through callbacks.

Do not place business logic inside composables.

Do not place repository calls inside composables.

Do not place navigation logic inside composables.

---

## ViewModel Rules

Business logic belongs in ViewModels.

Repository calls belong in ViewModels.

Navigation belongs in NavigationUseCase.

State updates must use:

kotlin updateState { copy(...) }

Never use:

kotlin state.value =

---

## File Creation Rules

Do not create:

- Mapper
- Validator
- Manager
- Provider
- Handler
- Factory

unless explicitly requested.

Do not create new abstraction layers.

Do not create future-proof structures.

Do not create placeholder implementations.

---

## Output Rules

When generating code:

- output complete code
- output compilable code
- avoid pseudo code
- avoid TODO comments
- avoid placeholder logic

Generate production-ready code only.