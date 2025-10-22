# Cursor Prompts Documentation System

## Usage Rules

### When to Load Which Documentation

| Task Type                 | Load Files                                     | Reason                                     |
|---------------------------|------------------------------------------------|--------------------------------------------|
| **UI/Compose Changes**    | `compose_rules.md` + `project_context.md`      | Focus on UI patterns and project structure |
| **Architecture Changes**  | `project_context.md` + `architecture_rules.md` | Need full project understanding            |
| **New Features**          | `project_context.md` + `code_standards.md`     | Understand project + coding patterns       |
| **Bug Fixes**             | `code_standards.md` + `compose_rules.md`       | Focus on existing patterns                 |
| **Database Changes**      | `project_context.md` + `architecture_rules.md` | Need data layer understanding              |
| **Navigation Changes**    | `compose_rules.md` + `project_context.md`      | UI + routing patterns                      |
| **State Race Conditions** | `08_stateflow_mapping_pattern.md`              | StateFlow mapping patterns                 |

### File Structure

- `00_README.md` - This file (usage rules)
- `01_project_context.md` - Project overview, architecture, modules
- `02_compose_rules.md` - Compose-specific coding standards
- `03_architecture_rules.md` - Clean architecture, MVVM patterns
- `04_code_standards.md` - General coding standards and patterns
- `05_self_enforcement.md` - Rules for AI to follow documentation
- `06_floating_action_button_pattern.md` - FAB implementation pattern
- `07_import_corrections_index.md` - Correct import paths reference
- `08_stateflow_mapping_pattern.md` - StateFlow mapping to avoid race conditions

### Loading Strategy

1. **Always load** `00_README.md` first
2. **Always load** `07_import_corrections_index.md` for correct imports
3. **Select relevant files** based on task type
4. **Never skip** documentation loading
5. **Reference specific rules** when making changes

### Import Reference

**ALWAYS** consult `07_import_corrections_index.md` when:

- Creating new files
- Adding imports
- Getting import errors
- Using asResult, BaseEvent, koinViewModel, or navigation
