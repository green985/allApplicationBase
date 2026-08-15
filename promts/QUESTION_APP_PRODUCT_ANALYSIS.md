# Question App — Official Product Analysis
> Last updated: 2026-07-13
> Based exclusively on source code inspection. No features invented.

---

## Project Architecture Overview

The `questionapplication` module is a **shell app** with no Kotlin source files of its own.
All business logic lives in shared modules:

| Module | Role |
|---|---|
| `composeBase` | Screens, ViewModels, UiStates, Events, Navigation |
| `domain` | Repository interfaces, UseCases |
| `data/models` | Domain + data models, enums, catalogs |
| `data/remote` | Retrofit API + DataSource (Supabase Edge Functions) |
| `data/repository` | Repository implementations |
| `data/local` | Room DB |
| `diModule` | Koin DI wiring, BaseApplication |

**Backend:** Supabase Edge Functions (`https://uduwhuvgdcacvdhzheyi.supabase.co/functions/v1/`)
**Firebase:** Available for legacy question CRUD and notifications — partially used.

---

---

# Feature Documentation

---

## 1. Authentication

**Current Status:** Functional — core flows working

### Capabilities
- Google Sign-In (via `googleLogin` module)
- Auto-login from saved session token on app start
- Auto-logout on 401 from API
- Delete account (API: `DELETE /v1/deleteUser`)
- Anonymous access (Supabase anon JWT injected by `AuthInterceptor`)

### Entry Point
`GeneralOperationVM.autoLogin()` — runs on app start via `GeneralOperationScreenSetup`

### Navigation Flow
```
App Start
  → GeneralOperationScreenSetup
      → autoLogin() syncs saved Google session
      → if saved session valid → navigates to home
      → if 401 → logout → stays at login state
```

### Screens
- `LoginOperationScreenSetup` (shared composable wrapping all screens)
- `LoginOperationSmallButtonSetup` (inline login button on Home screen)

### UiStates
- `AuthOperationVM` (shared auth state VM, injected everywhere)

### Events
- Login → triggered from `LoginOperationSmallButtonSetup` on home screen
- Logout → triggered from settings
- Delete Account → `QuestionSupabaseRepository.deleteAccount()`

### UseCases
- `AuthOperationRepository.syncUserFromSavedSession()`
- `AuthOperationRepository.logout()`

### Repository Methods
- `QuestionSupabaseRepository.registerGoogleUser()`
- `QuestionSupabaseRepository.getUserWithToken()`
- `QuestionSupabaseRepository.updateUser()`

### Missing Pieces
- No dedicated Login screen (login button embedded on Home)
- No registration form (Google only)
- No email/password auth
- No email verification
- No password reset

### Possible UX Problems
- Login entry point is buried on the Home screen toolbar — not a dedicated onboarding flow
- No explicit "you are logged out" screen

---

## 2. Question Feed (Browse Questions)

**Current Status:** Functional — paged list with filtering working

### Capabilities
- Browse questions paginated by `createdAt`
- Filter by `QueTag` (KNOWLEDGE, OPINION, EMOTION, BEHAVIOR, CREATIVITY, SOCIETY, FUTURE)
- Filter by `ModerationStatus` (APPROVED, PENDING, DECLINED, ALL)
- HorizontalPager with one tab per tag category
- Answer overlay — answered questions show user's selection
- Infinite scroll pagination

### Entry Point
Bottom navigation tab: **QuestionList** → `AppRoute.QuestionPager`

### Navigation Flow
```
Bottom Nav (QuestionList tab)
  → QuestionPagerScreenSetup
      → HorizontalPager (one page per QueTag)
          → QuestionListWithParamsScreenSetup(tag)
              → QuestionListWithParamsContent (LazyColumn)
                  → BaseQuestionView per item
```

### Screens
- `QuestionPagerScreenSetup`
- `QuestionListWithParamsScreenSetup`
- `QuestionListWithParamsContent`

### UiStates
- `QuestionPagerUiState` (currentFilter, currentPage, currentFilterType, tabs)
- `QuestionListUiState` (toolbarTitleText, currentFilter)
- `GenericListState<QuestionViewUiState>`
- `QuestionViewUiState` (per question item)

### Events
- `QuestionPagerEvent.OnTagFilterChanged`
- `QuestionListEvent.OnRefreshClicked`
- `QuestionListEvent.OnItemClicked`
- `QuestionListEvent.OnTagFilterChanged`
- `QuestionViewEvent.OnOptionSelected`
- `QuestionViewEvent.OnDeleteAnswerClicked`
- `QuestionViewEvent.OnTagSelected`

### ViewModels
- `QuestionPagerVm`
- `QuestionListVm`

### UseCases
- `GetQuestionsPagedByCreatedAtUseCase`
- `AnswerUseCase` (answer overlay)
- `QuestionEventHandlerUseCase`

### Repository Methods
- `QuestionSupabaseRepository.getQuestionListWithFilterParam(QueFilter)`
- `QuestionSupabaseRepository.addAnswer()`
- `QuestionSupabaseRepository.deleteAnswer()`

### Missing Pieces
- No question detail screen (no tap-to-expand action)
- `OnItemClicked` event exists in `QuestionListEvent` but is never handled with navigation
- No search within feed
- No sort options (only `createdAt` descending)

### Possible UX Problems
- `OnItemClicked` is defined but does nothing — tapping a question has no navigation
- No empty state message per tag category
- No pull-to-refresh gesture (only programmatic refresh)

---

## 3. Answer a Question

**Current Status:** Functional — yes/no and two-choice answers work

### Capabilities
- Select an option on a question (YES/NO, UP/DOWN, GOOD/BAD)
- Answer persisted to Supabase
- In-memory answer cache via `AnswerUseCase.answersState`
- Delete own answer
- Visual feedback: answered questions show selected option highlighted

### Entry Point
Any screen showing `BaseQuestionView` → `TwoChoicesSelectorView`

### Screens
- `TwoChoicesSelectorView` (inline in question card)

### UiStates
- `QuestionViewUiState.selectedAnswer`
- `QuestionViewUiState.isAnsweredByUser`

### Events
- `QuestionViewEvent.OnOptionSelected(questionId, optionId)`
- `QuestionViewEvent.OnDeleteAnswerClicked(questionId)`

### ViewModels
- `QuestionListVm.onQuestionEvent()` → `QuestionEventHandlerUseCase`
- `QuestionFormViewModel.onQuestionEvent()` → `QuestionEventHandlerUseCase`

### UseCases
- `AnswerUseCase.submitAnswer()`
- `AnswerUseCase.deleteAnswer()`
- `QuestionUseCase.submitAnswer()`

### Repository Methods
- `QuestionSupabaseRepository.addAnswer()`
- `QuestionSupabaseRepository.updateAnswer()`
- `QuestionSupabaseRepository.deleteAnswer()`
- `QuestionSupabaseRepository.getAnswersByUser()`

### Missing Pieces
- Multi-choice, scale, and open-ended question types exist in models/enums but only `SINGLE_CHOICE` is active
- `MULTI_CHOICE`, `SCALE`, `OPEN_ENDED` in `QuestionType` are commented out
- Three-choice selector view is not implemented (only `TwoChoicesSelectorView` exists; three-choice is a TODO in `QuestionAnswerAreaContainer`)

### Possible UX Problems
- No undo after answering
- No answer history screen for user
- Three-choice questions cannot be answered (placeholder only)

---

## 4. Create Question

**Current Status:** Functional — two-choice questions creatable

### Capabilities
- Create a question with title text
- Select category: TWO_CHOICE or THREE_CHOICE
- Select sub-category (YES_NO, UP_DOWN, GOOD_BAD; LOW_MED_HIGH, AGREE_NEUTRAL_DISAGREE)
- Tag selection (from meaning + style tag lists)
- Auto-approve toggle (admin users only)
- Submit → Supabase → snackbar confirmation → navigate back

### Entry Point
Home screen: **"Create Question"** button → `AppRoute.QuestionCreateQuestionPage`

### Navigation Flow
```
Home Screen
  → QuestionCreateScreenSetup(questionId = "")
      → submit → goBack() + snackbar
```

### Screens
- `QuestionCreateScreenSetup`
- `CreateQuestionYesNoView` (edit mode of `BaseQuestionView`)

### UiStates
- `QuestionCreateQuestionScreenUiState` (isLoading, titleText, taxonomy, isAutoApprove, etc.)
- `QuestionViewUiState` (for the question preview card)

### Events
- `QuestionCreateQuestionEvent.OnTitleChange`
- `QuestionCreateQuestionEvent.OnCategorySelected`
- `QuestionCreateQuestionEvent.OnTwoChoiceSubSelected`
- `QuestionCreateQuestionEvent.OnThreeChoiceSubSelected`
- `QuestionCreateQuestionEvent.OnAutoApproveChanged`
- `QuestionCreateQuestionEvent.OnSubmit`
- `QuestionCreateQuestionEvent.OnScreenOut`
- `QuestionViewEvent.OnTagSelectedForCreateQuestion`
- `QuestionViewEvent.OnTagRemoved`

### ViewModels
- `QuestionCreateQuestionVm`

### UseCases
- `QuestionTaxonomyFactory.buildQuestion(...)` (builds `QuestionOperationResponseBody`)
- `RandomHelper.generateGuid()`

### Repository Methods
- `QuestionSupabaseRepository.createQuestion()`

### Missing Pieces
- Edit existing question: `questionId` parameter accepted but editing logic not fully implemented (VM initializes with empty state regardless of `questionId`)
- `OnEditClicked` in `QuestionEventHandlerUseCase` navigates to `QuestionCreateQuestionPage(questionId)` but the VM doesn't load existing question data
- MULTI_CHOICE, SCALE, OPEN_ENDED categories exist in enum but create screen UI only shows TWO_CHOICE and THREE_CHOICE
- No image/media attachment

### Possible UX Problems
- Editing a question navigates to create screen but pre-populates nothing — user sees a blank form
- No character limit feedback on title field
- `OnScreenOut` event defined but implementation not visible in the screen

---

## 5. Admin / Moderation

**Current Status:** Partially functional — UI complete, core actions work

### Capabilities
- View all questions by status: ALL, APPROVED, PENDING, DECLINED (tabbed HorizontalPager)
- Approve a pending question
- Decline a pending question
- Send approved/declined question back to PENDING
- Edit a pending question (navigates to create screen)

### Entry Point
`AppRoute.AdminApproveQuestion` — reachable via navigation (no bottom tab; likely from Settings)

### Navigation Flow
```
Settings / Debug
  → AdminApproveQuestionScreenSetup
      → HorizontalPager (ALL | APPROVED | DECLINED | PENDING tabs)
          → QuestionListWithParamsScreenSetup(adminFilterType, isAdminMode=true)
```

### Screens
- `AdminApproveQuestionScreenSetup`

### UiStates
- `AdminApproveQuestionUiState` (tabs, currentFilterType, pendingCountText)

### Events
- `AdminApproveQuestionEvent.OnFilterSelected`
- `AdminApproveQuestionEvent.OnTagFilterChanged`
- `AdminApproveQuestionEvent.OnRefreshClicked`
- `QuestionViewEvent.OnAcceptClicked`
- `QuestionViewEvent.OnDeclineClicked`
- `QuestionViewEvent.OnPendingClicked`
- `QuestionViewEvent.OnEditClicked`

### ViewModels
- `AdminApproveQuestionVm`
- `QuestionListVm` (with admin mode enabled)

### UseCases
- `QuestionEventHandlerUseCase.handleAdminStatusUpdate(ModerationStatus)`

### Repository Methods
- `QuestionSupabaseRepository.updateQuestionStatus(QuestionStatusUpdateRequest)`

### Missing Pieces
- No admin role check in UI — `AdminApproveQuestion` screen is accessible to anyone who can navigate there
- `pendingCountText` field in `AdminApproveQuestionUiState` is never populated (no pending count fetch)
- No bulk approve/decline action

### Possible UX Problems
- Admin screen reachable without role gate — any user navigating to the route sees admin controls
- Pending count badge never shows a real number

---

## 6. Question Forms (Catalog / AI Assessment)

**Current Status:** Core flow functional; AI result delivery working

### Capabilities
- Browse a list of forms (catalogs)
- Open a form and answer all questions sequentially
- Track answered/total count with progress indicator
- Submit form answers to Supabase
- Trigger AI (ChatGPT) result generation after submit
- Receive AI result via push notification (`firebaseNotificationTokenStateFlow`)
- View AI result text in form screen ("AI Değerlendirme" card)
- Edit submitted form (unlock → re-answer → re-submit)

### Entry Point
Home screen: **"Question Form"** button → `AppRoute.QuestionFormScreen`

Also: push notification → `GeneralOperationVM.observeFormNotifications()` → `navigateTo(QuestionFormScreen(formId))`

### Navigation Flow
```
Home Screen
  → QuestionFormScreenSetup(formId)
      → OnFormLoad → getFormDetail()
      → Answer questions
      → OnSubmitForm → submitCatalog() → generateFormResult() [GlobalScope]
          → Push Notification → GeneralOperationVM → navigateTo QuestionFormScreen
              → AI result rendered in screen

Push Notification
  → GeneralOperationVM.observeFormNotifications()
      → snackbar with action → navigateTo(QuestionFormScreen(formId))
```

### Screens
- `QuestionFormScreenSetup`
- `QuestionFormListViewModel` / screen (list of forms — ViewModel exists but no corresponding screen setup file found)

### UiStates
- `QuestionFormScreenUiState` (isLoading, isLocked, isSubmitted, isGeneratingResult, generatedResultText, canSubmit, validationErrors, questions, progress)
- `QuestionFormListUiState` (catalogList, isLoading)
- `CatalogItemUiState` (formId, title, totalQuestions, answeredCount, isCompleted)

### Events
- `QuestionFormEvent.OnFormLoad`
- `QuestionFormEvent.OnSubmitForm`
- `QuestionFormEvent.OnEditForm`
- `QuestionFormEvent.OnCancelEdit`
- `QuestionFormEvent.OnBackPressed`
- `QuestionFormEvent.OnClearAnswer`
- `QuestionFormEvent.OnErrorDismiss`
- `QuestionFormListEvent.OnCatalogItemClick`
- `QuestionFormListEvent.OnRefresh`

### ViewModels
- `QuestionFormViewModel`
- `QuestionFormListViewModel`

### UseCases
- `AnswerUseCase.answersState` (overlay answers on questions)
- `QuestionEventHandlerUseCase` (per-question answer events)

### Repository Methods
- `QuestionSupabaseRepository.getCatalogList()`
- `QuestionSupabaseRepository.getCatalogDetail(formId, userId)`
- `QuestionSupabaseRepository.submitCatalog(formId, userId, questions)`
- `QuestionSupabaseRepository.generateFormResult(formId, userId, prompt, notificationToken, token)`

### Firebase Interactions
- `FirebaseNotificationTokenOperationRepository.firebaseNotificationTokenStateFlow` — token sent with `generateFormResult` request so backend can push result notification
- `NotificationHandlerRepository.formNotificationFlow` — observed in `GeneralOperationVM` to show snackbar + navigate

### Missing Pieces
- **Critical bug:** `QuestionFormScreenSetup` calls `vm.onEvent(OnFormLoad("form1"))` — hardcoded `"form1"` instead of the `formId` parameter passed to the composable
- `QuestionFormListViewModel` exists and is fully implemented but no `QuestionFormListScreenSetup` composable is registered in navigation
- The form catalog list is not reachable from the current navigation graph
- `generateFormResult` runs on `GlobalScope` — can leak if app is killed

### Possible UX Problems
- Hardcoded `formId = "form1"` means all form screens load the same form regardless of navigation parameter
- No way to browse available forms (list screen not wired to navigation)
- AI result generation has no timeout/error handling visible in UI state
- `isGeneratingResult` state exists but no loading indicator shown during AI generation

---

## 7. User Profile

**Current Status:** Partially functional — view/edit exists, "my questions" / "my answers" not fully wired

### Capabilities
- View own profile (`UserProfile` with empty `receiverUserId`)
- View another user's profile (`UserProfile(receiverUserId)`)
- Edit profile (`AppRoute.EditProfile`)
- Complete profile (`AppRoute.CompleteProfileScreen`)
- User list screen (`AppRoute.UserList`)

### Entry Point
Bottom navigation tab: **UserProfile** → `AppRoute.UserProfile()`

### Navigation Flow
```
Bottom Nav (UserProfile tab)
  → User2ProfileScreenSetup(receiverUserId = "")
      → own profile

QuestionList
  → OnTagSelected on user card → UserProfile(receiverUserId)
```

### Screens
- `User2ProfileScreenSetup` (via `UserProfileVm2`)
- `EditUserProfileScreenSetup`
- `CompleteProfileScreenSetup`
- `UserListScreenSetup`

### ViewModels
- `UserProfileVm2`

### Missing Pieces
- No "My Questions" list on profile (only `GetUserQuestionsPagedByCreatedAtUseCase` exists; not wired to a profile screen)
- No "My Answers" list on profile (`GetUserAnsweredQuestionsPagedUseCase` returns `emptyList()` — stubbed)
- `QuestionListType.USERS_QUESTIONS` and `USERS_ANSWERS` exist and are handled in `QuestionListVm.setUserFilter()` but not surfaced in the profile UI

### Possible UX Problems
- No visible path for user to see their own question history
- "My Answers" use case is explicitly stubbed with TODO — users cannot view their answer history

---

## 8. Settings

**Current Status:** Screen exists — specific settings unknown without reading screen content

### Capabilities
- Settings screen accessible from bottom navigation tab

### Entry Point
Bottom navigation tab: **Settings** → `AppRoute.QuestionAppSettings`

### Screens
- `FacSettingsScreenSetup` (ViewModel: `FacSettingsVm`)

### ViewModels
- `FacSettingsVm`

### Missing Pieces
- Admin panel access route not visible from bottom navigation (only reachable via direct route `AppRoute.AdminApproveQuestion`)
- Language settings (a `languageModule` exists in the project but connection to Settings screen is unclear)

---

## 9. Stopwatch / Focus Timer

**Current Status:** Duration selection functional; countdown functional; session tagging functional; `StopwatchScreen` route is empty

### Capabilities
- Select focus duration from preset values
- Start a countdown timer
- View remaining time in `MM:SS` format
- Tag a session (DENEME, SIGARA, SU_IC, NEFES_EGZERSIZI, POSTUR, KAHVE, etc.) — tag suggestions change based on elapsed duration
- Toggle tag selection (tap again to deselect)
- Cancel countdown → go back
- Finish session → record session to `StopwatchRecordRepository` → go back

### Entry Point
Home screen: **"Stop duration Screen"** button → `AppRoute.StopwatchDurationScreen`

### Navigation Flow
```
Home Screen
  → StopwatchDurationScreenSetup
      → user picks duration
      → navigateTo(AppRoute.StopwatchScreen(minutes))   ← route has empty composable body
```

### Screens
- `StopwatchDurationScreenSetup` (ViewModel: `StopwatchDurationVm`)
- `StopwatchScreen` — **route registered in navigation but composable body is empty** (not implemented)

### UiStates
- `StopwatchUiState` (formattedTime, suggestedTags, selectedTag, isFinished)
- `StopwatchDurationVm` (unknown — not read; assumed duration selection state)

### Events
- `StopwatchEvent.OnCancelClicked`
- `StopwatchEvent.OnFinishClicked`
- `StopwatchEvent.OnTagSelected(tag)`

### ViewModels
- `StopwatchVm`
- `StopwatchDurationVm`

### UseCases
- `StopwatchOperationUseCase.startCountdown()`
- `StopwatchOperationUseCase.cancelCountdown()`
- `StopwatchOperationUseCase.finishSession()`
- `StopwatchOperationUseCase.currentSuggestedTags()`
- `StopwatchOperationUseCase.updateSessionTag()`
- `StopwatchOperationUseCase.clearFinishedPendingDisplay()`

### Repository Methods
- `StopwatchRecordRepository.insertRecord(startedAt, endedAt, durationSeconds, status, tag)`

### Missing Pieces
- `StopwatchScreen` composable body is empty — the actual countdown screen is not rendered
- No session history screen
- No statistics/analytics on focus sessions
- `FloatingAskQuestionBarVm` (injected in pager screen) — unclear relation to stopwatch

### Possible UX Problems
- Navigation to `StopwatchScreen` leads to a blank screen

---

## 10. Notifications

**Current Status:** Form result notification delivery working; general notification infrastructure present

### Capabilities
- Receive push notification when AI form result is ready
- Snackbar shown with action to navigate to the form screen

### Entry Point
`GeneralOperationVM.observeFormNotifications()` — always active

### Navigation Flow
```
Push Notification received
  → NotificationHandlerRepository.formNotificationFlow emits FormNotificationData
      → GeneralOperationVM shows Snackbar
          → user taps action → navigateTo(QuestionFormScreen(formId))
```

### Repository Methods / UseCases
- `NotificationHandlerRepository.formNotificationFlow`
- `FirebaseNotificationTokenOperationRepository.firebaseNotificationTokenStateFlow`

### Missing Pieces
- No notification settings (enable/disable per type)
- No notification history/inbox screen
- No badge count management

---

## 11. Floating Ask Question Bar

**Current Status:** VM registered, bar rendered in `QuestionPagerScreenSetup` — exact behavior not fully traced

### Capabilities
- Floating action bar shown on the question pager screen
- `FloatingAskQuestionBarVm` handles its state

### Entry Point
`QuestionPagerScreenSetup` — always visible on the questions tab

---

---

# MVP Progress Report

```
Authentication
████████░░ 80%
  ✓ Google Sign-In, auto-login, logout, delete account
  ✗ No dedicated onboarding/login screen
  ✗ No email auth

Question Feed (Browse)
███████░░░ 70%
  ✓ Paged list, tag filter, HorizontalPager, answer overlay
  ✗ No question detail screen
  ✗ OnItemClicked does nothing

Answer Question
████████░░ 80%
  ✓ Two-choice (YES/NO, UP/DOWN, GOOD/BAD), delete answer, cache
  ✗ Three-choice selector not implemented
  ✗ No answer history

Create Question
███████░░░ 70%
  ✓ Two-choice + three-choice category, tags, submit
  ✗ Edit existing question pre-population broken
  ✗ No MULTI_CHOICE/SCALE/OPEN_ENDED UI

Admin / Moderation
███████░░░ 70%
  ✓ Approve / Decline / Pending actions, tabbed view
  ✗ No role gate
  ✗ Pending count never populated

Question Forms (Catalog)
██████░░░░ 60%
  ✓ Form detail, answer, submit, AI result, notification delivery
  ✗ Hardcoded formId "form1" bug
  ✗ Form list screen not wired to navigation

User Profile
████░░░░░░ 40%
  ✓ View profile, edit, complete profile, user list
  ✗ My Questions not surfaced on profile
  ✗ My Answers stubbed (emptyList TODO)

Settings
███░░░░░░░ 30%
  ✓ Screen exists
  ✗ Content/capabilities not fully traced

Stopwatch / Focus Timer
█████░░░░░ 50%
  ✓ Duration selection, countdown logic, session tagging, record save
  ✗ StopwatchScreen composable body is empty → blank screen

Notifications
██████░░░░ 60%
  ✓ Form result push notification → snackbar → navigate
  ✗ No notification inbox
  ✗ No notification preferences
```

---

---

# Full Audit

---

## 1. Existing Features (confirmed in code)

- Google authentication (register, auto-login, logout, delete account)
- Anonymous Supabase access (anon JWT in `AuthInterceptor`)
- Question feed with tag-based HorizontalPager
- Paged question list (cursor-based by `createdAt`)
- Answer questions (two-choice)
- Delete own answer
- In-memory answer cache (`AnswerUseCase.answersState`)
- Create question (two-choice, three-choice taxonomy, tag selection)
- Admin question moderation (approve/decline/pending)
- Question form browse + answer + submit
- AI-generated form result via Supabase Edge Function
- Push notification delivery of AI result
- Focus timer (duration selection + countdown + session tagging + record)
- User profile view/edit/complete
- User list screen
- Floating ask-question bar on pager
- Snackbar feedback system (`SnackbarDelegate`)
- Bottom navigation (Home, Questions, Settings, Profile)
- App review prompt (`AppReviewOperationUseCase`)
- App open count tracking

---

## 2. Missing Features (not found anywhere in code)

- Question detail / expand screen
- My Questions list on profile
- My Answers list (stubbed with `emptyList()`)
- Search (no search query, no search screen, no search API endpoint)
- Bookmarks / Favorites
- Comments (interface `FirebaseCommentOperationRepository` exists but no screen/VM)
- Social sharing
- Leaderboard / points / gamification
- Question reactions beyond answering
- Notification inbox / notification history
- Email/password authentication
- Dark mode toggle
- Language switcher UI (module exists, no UI connection found)
- Onboarding flow
- Form catalog browse screen (VM exists, no screen in navigation)

---

## 3. Duplicate Logic

- `QuestionUseCase.submitAnswer()` and `AnswerUseCase.submitAnswer()` both call `QuestionSupabaseRepository.addAnswer()` — two paths to submit an answer
- `QuestionEventHandlerUseCase` is instantiated inline inside `QuestionListVm` and `QuestionFormViewModel` rather than being a proper Koin-injected singleton — leads to separate instances with separate state

---

## 4. Dead Code

- `GetUserAnsweredQuestionsPagedUseCase` — fully built use case class that always returns `emptyList()` (explicit TODO)
- `FirebaseQuestionOperationRepository` and its `Impl` — the application uses Supabase as the active backend; the Firebase question repository is registered in DI but not used by any active ViewModel
- `QuestionApprovedListType` enum (`ALL`, `RECENTLY_ADDED`, `RECENTLY_ANSWERED`) — defined in models but never referenced in any filter or UI
- `GeneralPlaygroundScreen` / `GeneralPlaygroundVm` — debug playground, not reachable from production navigation
- `QuestionAppDebugRoot` composable — debug root, not wired to production start
- `QuestionAppNestedRoot` composable — alternative root, not the active entry point

---

## 5. Unused Screens

- `GeneralPlaygroundScreen` — playground screen, no production navigation entry
- Form list screen — `QuestionFormListViewModel` is fully implemented but no composable screen setup file exists and it is not registered in `questionAppNavigation()`

---

## 6. Unreachable Navigation

- `AppRoute.StopwatchScreen(minutes)` — registered in `questionAppNavigation()` but the composable body is **empty** (`{}`) → navigating to it produces a blank screen
- `AppRoute.AdminApproveQuestion` — no bottom tab or visible menu item points to it; only reachable via programmatic `navigateTo`
- `AppRoute.QuestionFormScreen` — accessible from Home "Question Form" button, but always loads `formId = "form1"` due to hardcoded value in `QuestionFormScreenSetup`
- Form list via catalog → no navigation entry point to `QuestionFormListViewModel`'s feature

---

## 7. Unused Events

- `QuestionListEvent.OnItemClicked(item)` — defined and emitted in UI, but no handler in `QuestionListVm.onEvent()` and no navigation triggered
- `QuestionViewEvent.OnTagRemoved` — handled in `QuestionCreateQuestionVm` but logged as ignored in `QuestionEventHandlerUseCase` (list context)
- `QuestionViewEvent.OnTagSelectedForCreateQuestion` — logged as ignored in list context
- `AdminApproveQuestionUiEvent.OnIdle` — sealed class with only `OnIdle`; never emitted
- `QuestionCreateQuestionUiEvent.OnSubmitSuccess` — defined but `QuestionCreateQuestionVm.submit()` navigates back without emitting this event

---

## 8. Unused UiStates

- `AdminApproveQuestionUiState.pendingCountText` — field defined, never populated with real data
- `QuestionFormScreenUiState.isGeneratingResult` — set to `true`/`false` during AI generation (on `GlobalScope`) but no spinner or visual indicator shown in `QuestionFormScreenSetup`
- `QuestionFormScreenUiState.validationErrors` — populated in `canSubmit` logic but not displayed in the UI
- `QuestionViewUiState.bodyText` — stored in model and mapped from `questionTitle` but not rendered in any composable (only `titleText` is displayed)

---

## 9. Unused UseCases

- `GetUserAnsweredQuestionsPagedUseCase` — registered in `QuestionProjectModule` but always returns `emptyList()`; never drives any visible screen
- `GetUserQuestionsPagedByCreatedAtUseCase` — registered in DI (`singleOf`), referenced in `QuestionListVm` branch for `USERS_QUESTIONS` but there is no UI entry point that calls `setUserFilter()` with a real userId from the profile screen
- `QuestionUseCase.updateQuestionStatus()` — defined and implemented but `QuestionEventHandlerUseCase` calls `questionSupabaseRepository.updateQuestionStatus()` directly, bypassing this use case

---

## 10. Recommended Next Features (priority order)

See Roadmap below.

---

---

# Product Roadmap

> Ordered by impact and implementation readiness.
> Based entirely on what already exists and what logically extends it.

---

## Phase 1 — Critical Bug Fixes (Immediate)

### 1.1 Fix `StopwatchScreen` composable body
**Why:** Users are navigated to a blank screen after selecting a duration.
**What:** Implement the `StopwatchScreen` composable body in `questionAppNavigation()` using `StopwatchVm` (fully built) and `StopwatchUiState`.

### 1.2 Fix `QuestionFormScreen` hardcoded `formId`
**Why:** All form screens load `"form1"` regardless of which form was tapped.
**What:** Replace `"form1"` with the `formId` parameter in `QuestionFormScreenSetup`.

### 1.3 Handle `QuestionListEvent.OnItemClicked`
**Why:** Tapping a question does nothing — dead interaction.
**What:** Wire `OnItemClicked` to navigate to a question detail screen (or expand inline).

---

## Phase 2 — Complete Existing Flows (High Value)

### 2.1 Wire Form List to Navigation
**Why:** `QuestionFormListViewModel` is complete but unreachable.
**What:** Create `QuestionFormListScreenSetup`, register `AppRoute.QuestionFormList` in `questionAppNavigation()`, and add an entry point (e.g., from Home or Settings).

### 2.2 Complete "My Questions" on Profile
**Why:** `GetUserQuestionsPagedByCreatedAtUseCase` is complete; `QuestionListType.USERS_QUESTIONS` is handled in `QuestionListVm`.
**What:** Add a "My Questions" section to `User2ProfileScreenSetup` that calls `vm.setUserFilter(USERS_QUESTIONS, userId)`.

### 2.3 Fix Edit Question pre-population
**Why:** Editing a question shows a blank form.
**What:** In `QuestionCreateQuestionVm`, if `questionId` is non-empty, fetch and populate existing question data before rendering.

### 2.4 Implement Three-Choice Selector View
**Why:** Three-choice questions can be created and stored, but cannot be answered.
**What:** Build `ThreeChoicesSelectorView` and wire it in `QuestionAnswerAreaContainer`.

---

## Phase 3 — New Screens for Existing Logic

### 3.1 Dedicated Login / Onboarding Screen
**Why:** Login is currently a small inline button on the Home screen — poor discoverability.
**What:** Create a `LoginScreen` with Google Sign-In button as the app entry point for unauthenticated users.

### 3.2 Question Detail Screen
**Why:** `OnItemClicked` exists but has no destination.
**What:** Create a detail screen showing full question, all answers/stats, and related tags.

### 3.3 My Answers Screen
**Why:** `AnswerUseCase.answersState` holds all user answers in memory; `getAnswersByUser()` is functional.
**What:** Implement `GetUserAnsweredQuestionsPagedUseCase.fetchDataAfter()` (remove TODO), add "My Answers" tab to profile.

---

## Phase 4 — Search & Discovery

### 4.1 Search Questions
**Why:** No search exists; the question list API accepts a `QueFilter` that could include text.
**What:** Add a `searchQuery` field to `QueFilter`, add a search bar to `QuestionPagerScreenSetup`, extend API if needed.

### 4.2 Question Tags Navigation
**Why:** `OnTagSelected` event navigates to `QuestionListWithParams(tag)` — this already works.
**What:** Surface tag chips more prominently on question cards to encourage tag-based browsing.

---

## Phase 5 — Admin & Quality

### 5.1 Admin Role Gate
**Why:** `AdminApproveQuestion` screen has no role check — any user can reach it.
**What:** Check user role (admin flag) before rendering admin UI; hide admin navigation entries for non-admins.

### 5.2 Pending Count Badge
**Why:** `AdminApproveQuestionUiState.pendingCountText` exists but is never populated.
**What:** Fetch and display pending question count on the admin tab badge.

---

## Phase 6 — Engagement Features

### 6.1 Focus Session History
**Why:** Sessions are recorded to `StopwatchRecordRepository` (Room DB) but never displayed.
**What:** Add a session history screen accessible from Settings or Profile.

### 6.2 Notification Inbox
**Why:** Push notifications for AI results arrive but disappear — no history.
**What:** Create a simple notification list screen reading from local DB or Firestore.

### 6.3 Comments
**Why:** `FirebaseCommentOperationRepository` interface already exists.
**What:** Implement comment list + add comment on question detail screen.

---

## Phase 7 — Polish

### 7.1 Validation Error Display on Forms
**Why:** `QuestionFormScreenUiState.validationErrors` is populated but never shown in UI.
**What:** Display inline validation messages below unanswered questions.

### 7.2 AI Result Loading State
**Why:** `isGeneratingResult` state exists but no spinner shown during generation.
**What:** Show a loading card / shimmer while `isGeneratingResult = true`.

### 7.3 Language Switcher UI
**Why:** `languageModule` and `LanguageImplModule` are integrated in DI but no UI settings exist.
**What:** Add language selection to Settings screen using existing module.

---

*End of Document — update this file as features are added or changed.*

