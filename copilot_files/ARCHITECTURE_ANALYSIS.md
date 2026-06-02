# Architecture Analysis — QuestionCreate Feature (Reference Implementation)

Bu dosya, projedeki genel kodlama yapısını, mimariyi ve model pattern'lerini belgeler.
`QuestionCreateScreenSetup` referans alınarak yazılmıştır.

---

## 1. Katman Yapısı (Clean Architecture)

```
[UI Layer]          QuestionCreateScreenSetup.kt      ← Composable, sadece render
                    QuestionCreateScreen()             ← Stateless private composable
                         ↓ state / events
[ViewModel Layer]   QuestionCreateQuestionVm           ← BaseViewModel, iş mantığı
                         ↓ inject
[Domain Layer]      QuestionSupabaseRepository         ← interface (impl yok burada)
                    NavigationUseCase                  ← navigation köprüsü
                         ↓
[Data Layer]        repository impl (data:repository)  ← Flow döner
[Models]            QuestionOperationResponseBody       ← data:models, pure @Keep data class
```

Modül bağımlılıkları:
- Feature modüller sadece `:domain` ve `:data:models`'e bağımlıdır.
- Sadece `:diModule` tüm modüllere bağımlıdır.
- `:composeBase` UI, ViewModel ve navigation içerir.

---

## 2. State Yönetimi

Bu ekranda **2 ayrı MutableStateFlow** kullanılır:

| Flow | Tip | İçerik |
|---|---|---|
| `uiState` | `QuestionCreateQuestionScreenUiState` | Ekrana özel: loading, error, taxonomy, isAutoApprove |
| `questionUiState` | `QuestionViewUiState` | Paylaşılan soru görünümü: title, options, tags |

### Kural
- Mutasyon sadece `updateState { copy(...) }` extension'ı ile yapılır (`UIState.kt`).
- Asla direkt `.value = ...` atanmaz.

```kotlin
// ✅ Doğru
uiState.updateState { copy(isLoading = true, errorText = "") }

// ❌ Yanlış
uiState.value = uiState.value.copy(isLoading = true)
```

### UiState Zorunlu Alanlar

Her UiState şu alanları taşımalıdır:

```kotlin
data class XUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",  // LanguageKey.* sabitleri kullanılır
    // ... domain alanlar
)
```

---

## 3. Event Sistemi

**3 ayrı event tipi** vardır:

| Tip | Base Sınıf | Kullanım |
|---|---|---|
| `QuestionViewEvent` | `BaseEvent` | Paylaşılan/global, `onEvent(Any)` ile handle edilir |
| `QuestionCreateQuestionEvent` | `BaseEvent` | Ekrana özel, `onCreateEvent()` ile handle edilir |
| `QuestionCreateQuestionUiEvent` | `BaseUIEvent` | One-shot UI event, `MutableSharedFlow` ile emit edilir |

### Global Event Bus
`BaseViewModel.init` → `testEventNavigator.eventFlow.collectLatest { onEvent(it) }` ile tüm VM'ler
global event bus'a **otomatik** abone olur. Yeni VM yazarken ek subscription gerekmez.

### ErrorDismiss Zorunluluğu
Her event sealed class'ında `ErrorDismiss` olmalıdır:

```kotlin
// ✅ Doğru
is XEvent.ErrorDismiss -> myState.updateState { copy(isError = false, errorMessage = "") }
```

---

## 4. ViewModel Pattern

```kotlin
class MyVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val repository: SomeRepository,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(MyUiState())
    val uiEvent = MutableSharedFlow<MyUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onEvent(event: Any) {
        // global + shared event handling
    }
}
```

### Async İşlem Pattern (Flow tabanlı repository)
Repository `Flow<T>` döndürüyorsa `.asResult()` ile sarılır:

```kotlin
viewModelScope.launch(getDispatcherIo()) {
    uiState.updateState { copy(isLoading = true, isError = false, errorMessage = "") }
    repository.doSomething(body)
        .asResult()
        .collectLatest { result ->
            result.fold(
                onSuccess = {
                    uiState.updateState { copy(isLoading = false) }
                    navigationUseCase.goBack()
                },
                onFailure = { error ->
                    uiState.updateState {
                        copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = ErrorMessage.fetchErrorMessage(error.message)
                        )
                    }
                }
            )
        }
}
```

### Async İşlem Pattern (suspend repository)
Repository `suspend fun` ve `Result<T>` döndürüyorsa:

```kotlin
viewModelScope.launch(getDispatcherIo()) {
    uiState.updateState { copy(isLoading = true, isError = false, errorMessage = "") }
    repository.doSomething().fold(
        onSuccess = { data ->
            uiState.updateState { copy(isLoading = false) }
        },
        onFailure = { error ->
            uiState.updateState {
                copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = ErrorMessage.fetchErrorMessage(error.message)
                )
            }
        }
    )
}
```

---

## 5. Model Yapısı (`data:models`)

```
QuestionOperationResponseBody       ← API request/response body
    ├── questionId: String
    ├── formId: String?
    ├── questionTitle: String
    ├── questionType: QuestionType  ← enum
    ├── moderationStatus: ModerationStatus ← enum
    ├── taxonomy: QuestionTaxonomyRef
    │       ├── categoryKey: String   ← enum değil, String key!
    │       └── subCategoryKey: String?
    ├── options: List<QueOption>
    ├── tags: List<QueTag>
    └── createdBy: String

QuestionCategories (enum)           ← TWO_CHOICE, THREE_CHOICE, ...
QuestionCategoryKeys (object)       ← "2choice", "3choice", ... (backend'e giden String)
```

### Enum ↔ String Key Dönüşüm Kuralı
Backend'e String gider, UI'da enum kullanılır. Dönüşüm extension ile yapılır:

```kotlin
// ✅ Enum → String (backend'e göndermek için)
QuestionCategories.TWO_CHOICE.asKey()  // → "2choice"

// ✅ String → Enum (backend'den gelen datayı parse etmek için)
categoryFromKey("2choice")             // → QuestionCategories.TWO_CHOICE
```

### Model Annotation Kuralı
Tüm data/request/response modelleri `@Keep` ile annotate edilir (ProGuard koruması):

```kotlin
@Keep
data class MyModel(val field: String = "")
```

---

## 6. Navigation

```kotlin
// ✅ VM içinde — sadece UseCase üzerinden
navigationUseCase.navigateTo(AppRoute.SomeScreen)
navigationUseCase.goBack()

// ✅ Root composable'da bir kez set edilir
navigationUseCase.setNavigator(
    navigateTo = { route -> scope.launch { backStack.add(route as NavKey) } },
    goBack = { scope.launch { if (backStack.size > 1) backStack.removeLastOrNull() } }
)
```

VM asla `NavBackStack`'e dokunmaz.

---

## 7. UI Katmanı Kuralları

### Setup Composable Yapısı
```kotlin
@Composable
fun MyScreenSetup(param: String = "") {
    val vm = koinViewModel<MyVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    MyScreen(
        uiState = uiState,
        onEvent = { vm.onEvent(it) }
    )

    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { event ->
            when (event) { ... }
        }
    }

    DisposableEffect(Unit) {
        onDispose { vm.onEvent(MyEvent.OnScreenOut) }
    }
}
```

### Stateless Screen Composable
```kotlin
@Composable
private fun MyScreen(
    uiState: MyUiState,
    onEvent: (MyEvent) -> Unit = {},
) {
    BaseScaffold(...) { ... }
}
```

### Renk / Tipografi / Spacing
```kotlin
// ✅ Zorunlu
color = AppColors.textPrimary
style = AppTextStyles.body
Modifier.padding(AppSpacing.lg)
Card(shape = AppShapes.roundedLarge)

// ❌ Yasak
color = Color(0xFF...)
TextStyle(fontSize = 16.sp)
Modifier.padding(16.dp)
RoundedCornerShape(16.dp)
```

---

## 8. DI (Koin) Kayıt Kuralları

```kotlin
// Paylaşılan state taşıyan VM veya servis → single
singleOf(::AuthOperationVM)
singleOf(::SnackbarDelegate)

// Lifecycle-scoped VM → viewModel
viewModelOf(::QuestionCreateQuestionVm)

// Her injection'da yeni instance → factory
factoryOf(::GetQuestionsPagedByCreatedAtUseCase)
```

---

## 9. Snackbar

```kotlin
// ✅ Doğru — VM içinden delegate üzerinden
snackbarDelegate.triggerSnackbarState(message = LanguageKey.someMessage)

// ❌ Yanlış — UI katmanından direkt emit
```

---

## 10. Repository Katmanı Kuralları

```kotlin
// ✅ Domain interface — Flow döner
interface MyRepository {
    fun doSomething(body: RequestBody): Flow<ResponseBody>
}

// ✅ Impl — try/catch, raw exception korunur
@Suppress("TooGenericExceptionCaught")
override fun doSomething(body: RequestBody): Flow<ResponseBody> = flow {
    try {
        val result = remoteSource.call(body)
        emit(result)
    } catch (e: Exception) {
        throw e  // raw message korunur, VM normalize eder
    }
}
```

`ErrorMessage.fetchErrorMessage()` **asla** repository'de çağrılmaz. Sadece ViewModel'de çağrılır.

---

## 11. Bilinen Sapma Noktaları (Dikkat)

| Sorun | Olması gereken |
|---|---|
| `errorText` field adı bazı UiState'lerde kullanılıyor | `errorMessage` olmalı |
| Bazı UiState'lerde `isError` flag yok | Zorunlu alan |
| `onFailure`'da `error.message` direkt snackbar'a gidebiliyor | `ErrorMessage.fetchErrorMessage()` geçmeli |
| Hardcoded string (`"Create Question"`) | `LanguageKey.*` kullanılmalı |

