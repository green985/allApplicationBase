# Filter Operation Helper - Factory Pattern

## Genel Bakış

QueFilter operasyonlarını merkezi bir helper üzerinden yönetmek için factory pattern
implementasyonu. Her VM için ayrı instance oluşturulur.

## Mimari

### 1. QuestionFilterOperationHelper (Factory)

**Lokasyon:** `composeBase/.../questionScreens/list/QuestionFilterOperationHelper.kt`

```kotlin
class QuestionFilterOperationHelper {
    private val _queFilter = MutableStateFlow(QueFilter.DEFAULT)
    val queFilter: StateFlow<QueFilter> = _queFilter.asStateFlow()

    fun setAdminFilter(filterType: QuestionListAdminFilterType)
    fun setTagFilter(tag: QueTag?)
    fun clearAllFilters()
    fun getCurrentFilter(): QueFilter
}
```

**Özellikler:**

- Her VM için ayrı instance (factory pattern)
- Merkezi filter state yönetimi
- StateFlow ile reactive update
- Read/Write separation (private _queFilter, public queFilter)

### 2. Koin DI - Factory Registration

**Lokasyon:** `QuestionProjectModule.kt`

```kotlin
factoryOf(::QuestionFilterOperationHelper)
```

**Not:** `factoryOf` kullanıldı çünkü her inject edildiğinde YENİ instance yaratılmalı.

### 3. QuestionPagerScreenSetup (Yeni Layer)

**Amaç:** HorizontalPager + Filter yönetimi için intermediate layer

**Yapı:**

```
QuestionPagerScreenSetup
  └── QuestionPagerVm (filterHelper inject)
      └── QuestionListVm
```

**Sorumluluklar:**

1. QuestionFilterBar UI yönetimi
2. HorizontalPager state yönetimi
3. Filter event'lerini filterHelper'a yönlendirme
4. filterHelper state'ini QuestionListVm'e sync etme

**Dosyalar:**

- `QuestionPagerVm.kt`
- `QuestionPagerScreenSetup.kt`

### 4. QuestionPagerVm

```kotlin
class QuestionPagerVm(
    appDispatchers: AppDispatchers,
    val questionListVm: QuestionListVm,
    val filterHelper: QuestionFilterOperationHelper,
) : BaseViewModel(appDispatchers)
```

**init bloğu:**

```kotlin
init {
    viewModelScope.launch(getDispatcherIo()) {
        filterHelper.queFilter.collectLatest { filter ->
            _uiState.value = _uiState.value.copy(currentFilter = filter)
            syncFilterToListVm(filter)
        }
    }
}
```

**Event handling:**

```kotlin
sealed class QuestionPagerEvent {
    data class OnTagFilterChanged(val tag: QueTag?) : QuestionPagerEvent()
}
```

**Sync mantığı:**

```kotlin
private fun syncFilterToListVm(filter: QueFilter) {
    questionListVm.setAdminFilter(filter.adminFilterType)
    questionListVm.setTagFilter(filter.selectedTagFilter)
}
```

### 5. AdminApproveQuestionVM Entegrasyonu

**Constructor:**

```kotlin
class AdminApproveQuestionVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    val questionListVm: QuestionListVm,
    val filterHelper: QuestionFilterOperationHelper,
) : BaseViewModel(appDispatchers)
```

**Event eklendi:**

```kotlin
data class OnTagFilterChanged(val tag: QueTag?) : AdminApproveQuestionEvent()
```

**Event handler:**

```kotlin
is AdminApproveQuestionEvent.OnTagFilterChanged -> {
    filterHelper.setTagFilter(event.tag)
}
```

**init bloğu:**

```kotlin
init {
    viewModelScope.launch(getDispatcherIo()) {
        filterHelper.queFilter.collectLatest { filter ->
            questionListVm.setAdminFilter(filter.adminFilterType)
            questionListVm.setTagFilter(filter.selectedTagFilter)
        }
    }
}
```

### 6. AdminApproveQuestionScreenSetup Wire

```kotlin
onEvent = { ev ->
    when (ev) {
        is QuestionListEvent.OnTagFilterChanged -> {
            vm.onEvent(AdminApproveQuestionEvent.OnTagFilterChanged(ev.tag))
        }
        else -> vm.questionListVm.onEvent(ev)
    }
}
```

## Data Flow

### QuestionPagerScreenSetup Flow

```
User taps FilterChip
  ↓
QuestionListEvent.OnTagFilterChanged
  ↓
QuestionPagerEvent.OnTagFilterChanged
  ↓
filterHelper.setTagFilter(tag)
  ↓
filterHelper.queFilter emits
  ↓
QuestionPagerVm.init collectLatest
  ↓
syncFilterToListVm(filter)
  ↓
questionListVm.setAdminFilter() + setTagFilter()
  ↓
questionListVm internal combine()
  ↓
List filtered + UI updated
```

### AdminApproveQuestionScreenSetup Flow

```
User taps FilterChip
  ↓
QuestionListEvent.OnTagFilterChanged
  ↓
AdminApproveQuestionEvent.OnTagFilterChanged (wired in ScreenSetup)
  ↓
filterHelper.setTagFilter(tag)
  ↓
filterHelper.queFilter emits
  ↓
AdminApproveQuestionVM.init collectLatest
  ↓
questionListVm.setAdminFilter() + setTagFilter()
  ↓
List filtered + UI updated
```

## HorizontalPager Yapısı

**QuestionPagerScreenSetup:**

```kotlin
HorizontalPager(
    state = pagerState,
    modifier = Modifier.weight(1f)
) { page ->
    QuestionListContent(
        listViewState = listViewState,
        onQuestionEvent = onQuestionEvent
    )
}
```

**Şu anda:** Single page (pageCount = 1)
**Gelecekte:** Multiple pages eklenebilir (favorite, recommended, etc.)

## Navigation

**Yeni route:**

```kotlin
val QuestionPager = Route("question/QuestionPager")
```

**Navigation entrypoint:**

```kotlin
composable(QuestionAppProjectRoutes.QuestionPager.route) {
    QuestionPagerScreenSetup()
}
```

**Mevcut QuestionList route korundu:**

```kotlin
composable(QuestionAppProjectRoutes.QuestionList.route) {
    QuestionListScreenSetup()
}
```

## Avantajlar

1. **Separation of Concerns:** Filter logic helper'a taşındı
2. **Factory Pattern:** Her VM kendi filter instance'ına sahip
3. **Reusability:** filterHelper birden fazla VM'de kullanılabilir
4. **Testability:** Helper ayrı test edilebilir
5. **Scalability:** Yeni filter türleri kolayca eklenebilir
6. **Reactive:** StateFlow ile otomatik UI update

## Dezavantajlar & Notlar

⚠️ **Duplicate Sync:** Şu anda filterHelper state'i hem QuestionPagerVm hem AdminApproveQuestionVM
init'te sync ediliyor. İleride bu tekrarı refactor edilebilir.

⚠️ **HorizontalPager Gereksinimi:** QuestionPagerScreenSetup şu anda single page kullanıyor. Eğer
pager gerekmiyorsa bu layer optional tutulabilir.

## Dosyalar

### Yeni Oluşturulan

- `QuestionFilterOperationHelper.kt`
- `QuestionPagerVm.kt`
- `QuestionPagerScreenSetup.kt`

### Güncellenen

- `QuestionProjectModule.kt` - factory registration
- `AdminApproveQuestionVM.kt` - filterHelper inject + OnTagFilterChanged
- `AdminApproveQuestionScreenSetup.kt` - event wire
- `QuestionAppProjectRoutes.kt` - QuestionPager route
- `QuestionAppNavigation.kt` - QuestionPager composable

## Test

Build başarılı:

```bash
./gradlew :composeBase:assembleDebug
# BUILD SUCCESSFUL in 2s
```

## Gelecek Geliştirmeler

1. **Multiple pages:** HorizontalPager'a favorite/recommended page'ler ekle
2. **FilterHelper refactor:** Sync mantığını centralize et
3. **FilterChip animation:** Seçili tag'e smooth transition
4. **Persist filter state:** DataStore ile filter state'i persist et
5. **Filter presets:** "Most Popular", "Recent" gibi preset'ler
