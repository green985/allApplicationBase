# Prompts Index

Tüm kurallar burada. Yeni task başlarken oku.

## Files

**WARP.md** - Repository setup, build commands, architecture, core rules (PRIMARY)

**KOD_KURALLARI.md** - Compose fonksiyonları: Hardcoded data kullanılmaz, UiState'ten al

**REFACTORING_REPORT_UI_EVENTS.md** - UI Event Pattern: sealed class Events, ScreenSetup wiring only

**QUESTION_CREATION_PUBLISHING_FLOW.md** - Question creation flow: Type → Style → Content →
Preview → Publish → Admin Moderation

## Rules Summary

1. Compose: No hardcoded data (KOD_KURALLARI)
2. Events: sealed class pattern (REFACTORING_REPORT)
3. ScreenSetup: Wiring only, no logic (WARP + REFACTORING_REPORT)
4. Navigation: NavigationUseCase inside ViewModel (WARP)
5. DI: Koin viewModelOf(::VM) (WARP)
6. State: ImmutableList<T> (WARP, KOD_KURALLARI)
7. Output: Blunt, no filler (sadelik_anlasilabilirlik)

## When Starting Task

Read WARP.md first, then task-specific rules.
