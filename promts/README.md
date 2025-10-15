# Prompts Index

Tüm kurallar burada. Yeni task başlarken oku.

## Files

**WARP.md** - Repository setup, build commands, architecture, core rules, import rules (PRIMARY)

**KOD_KURALLARI.md** - Compose fonksiyonları: Hardcoded data kullanılmaz, UiState'ten al,
ImmutableList

**KOD_KURALLARI2.md** - Question Project Architecture: Models, Repository, ViewModels, Screens,
Status management

**REFACTORING_REPORT_UI_EVENTS.md** - UI Event Pattern: sealed class Events, ScreenSetup wiring only

**QUESTION_CREATION_PUBLISHING_FLOW.md** - Question creation flow: Type → Style → Content →
Preview → Publish → Admin Moderation

## Rules Summary

1. Compose: No hardcoded data (KOD_KURALLARI)
2. Events: sealed class pattern (REFACTORING_REPORT)
3. ScreenSetup: Wiring only, no logic (WARP + REFACTORING_REPORT)
4. Navigation: NavigationUseCase inside ViewModel (WARP)
5. DI: Koin viewModelOf(::VM) (WARP, KOD_KURALLARI2)
6. State: ImmutableList<T> (WARP, KOD_KURALLARI, KOD_KURALLARI2)
7. Output: Blunt, no filler (sadelik_anlasilabilirlik)
8. Import: NO import lines in docs/examples (WARP)
9. Status: APPROVED cannot be deleted directly (KOD_KURALLARI2)

## When Starting Task

Read WARP.md first, then task-specific rules.
