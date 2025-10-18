# QueFilter System - İkili Filtreleme Sistemi

## Genel Bakış

Question listeleme ekranı için AdminFilterType + QuestionTag kombinasyonunda iki katmanlı,
genişletilebilir filtreleme sistemi.

## Mimari

### 1. QueFilter Data Class

**Lokasyon:** `composeBase/.../questionScreens/list/QueFilter.kt`

```kotlin
data class QueFilter(
    val adminFilterType: QuestionListAdminFilterType = NONE,
    val selectedTagFilter: QueTag? = null,
) {
    fun hasActiveFilter(): Boolean

    companion object {
        val DEFAULT = QueFilter()
    }
}
```

**Amaç:**

- Admin ve tag filtrelerini tek obje içinde birleştirir
- Genişletilebilir yapı: yeni filter türleri eklenebilir
- İki katmanlı filtreleme: önce admin, sonra tag

### 2. QuestionListVm Değişiklikleri

**Eski Yapı:**

```kotlin
private val adminFilterType = MutableStateFlow(QuestionListAdminFilterType.NONE)
```

**Yeni Yapı:**

```kotlin
private val queFilter = MutableStateFlow(QueFilter.DEFAULT)

override val listViewState: MutableStateFlow<GenericListState<QuestionViewUiState>> =
    MutableStateFlow(
        GenericListState(
            dataFlow = combine(
                questionUseCase.getQuestionListWithUpdates(),
                answerRepository.answersState,
                queFilter,
            ) { questions, answers, filter ->
                val adminFiltered = filterQuestionsByStatus(questions, filter.adminFilterType)
                val tagFiltered = filterQuestionsByTag(adminFiltered, filter.selectedTagFilter)
                overlayAnswers(tagFiltered, answers, filter.adminFilterType)
            }
        )
    )
```

**Filtreleme Sırası:**

1. Admin filter uygulanır (ALL/PENDING/APPROVED/DECLINED)
2. Tag filter uygulanır (QueTag bazlı)
3. User answer overlay uygulanır

### 3. Event Sistemi

**QuestionListEvent Eklenen:**

```kotlin
data class OnAdminFilterChanged(val filterType: QuestionListAdminFilterType) : QuestionListEvent()
data class OnTagFilterChanged(val tag: QueTag?) : QuestionListEvent()
```

**ViewModel metodları:**

```kotlin
fun setAdminFilter(filterType: QuestionListAdminFilterType)
fun setTagFilter(tag: QueTag?)
fun clearAllFilters()
```

### 4. UI Katmanı - QuestionFilterBar

**Lokasyon:** `composeBase/.../questionScreens/list/QuestionFilterBar.kt`

**İki bölüm:**

1. **AdminFilterRow:** Admin panelde görünür (isAdminMode = true)
    - NONE, PENDING_ADMIN, APPROVED_ADMIN, DECLINED_ADMIN
2. **TagFilterRow:** Her yerde görünür
    - "All Tags" + QuestionTagCatalog.createQuestionTagList

**Kullanım:**

```kotlin
QuestionFilterBar(
    currentFilter = uiState.currentFilter,
    onEvent = { vm.onEvent(it) },
    isAdminMode = true / false
)
```

### 5. QuestionListScreenSetup Entegrasyonu

**Değişiklikler:**

- `QuestionListScreen` parametreleri: `currentFilter`, `isAdminMode` eklendi
- `QuestionFilterBar` ekranın üstüne eklendi
- Admin panelde `isAdminMode = true`, normal ekranda `false`

### 6. Admin Panel Entegrasyonu

**AdminApproveQuestionScreenSetup:**

- `listUiState` collect ediyor
- `currentFilter` ve `isAdminMode = true` ile QuestionListScreen'e aktarıyor
- Tab sistemi ile admin filter hala çalışıyor (mevcut yapı korundu)

**AdminApproveQuestionVM:**

- `setFilter()` → `setAdminFilter()` güncellendi

## Filtreleme Mantığı

### filterQuestionsByStatus

```kotlin
private fun filterQuestionsByStatus(
    questions: List<QuestionOperationResponseBody>,
    filter: QuestionListAdminFilterType,
): List<QuestionOperationResponseBody>
```

Admin filtreleme: moderationStatus bazlı

### filterQuestionsByTag

```kotlin
private fun filterQuestionsByTag(
    questions: List<QuestionOperationResponseBody>,
    tagFilter: QueTag?,
): List<QuestionOperationResponseBody>
```

Tag filtreleme: question.tags içinde tag.id eşleşmesi

**Not:** `QuestionOperationResponseBody` zaten `tags: List<QueTag>` içeriyor

## Genişletilebilirlik

QueFilter yeni filter türleri için hazır:

```kotlin
data class QueFilter(
    val adminFilterType: QuestionListAdminFilterType = NONE,
    val selectedTagFilter: QueTag? = null,
    val dateRangeFilter: DateRange? = null,  // YENİ
    val searchQuery: String? = null,          // YENİ
)
```

Yeni filter eklemek için:

1. QueFilter'a field ekle
2. VM'de `filterQuestionsBy...()` metodu ekle
3. combine bloğunda çağır
4. Event ekle
5. UI'a FilterChip/Input ekle

## Kullanım Örnekleri

### Normal User - Sadece Tag Filter

```kotlin
QuestionFilterBar(
    currentFilter = QueFilter(selectedTagFilter = QuestionTagCatalog.KNOWLEDGE),
    onEvent = { vm.onEvent(it) },
    isAdminMode = false
)
```

### Admin - Her İki Filter

```kotlin
QuestionFilterBar(
    currentFilter = QueFilter(
        adminFilterType = PENDING_ADMIN,
        selectedTagFilter = QuestionTagCatalog.OPINION
    ),
    onEvent = { vm.onEvent(it) },
    isAdminMode = true
)
```

## Dosyalar

### Yeni Oluşturulan

- `QueFilter.kt`
- `QuestionFilterBar.kt`

### Güncellenen

- `QuestionListVm.kt`
- `QuestionListUiState.kt` (QuestionListEvent + currentFilter field)
- `QuestionListScreenSetup.kt`
- `AdminApproveQuestionScreenSetup.kt`
- `AdminApproveQuestionVM.kt`

## Test

Build başarılı:

```bash
./gradlew :composeBase:assembleDebug
# BUILD SUCCESSFUL in 2s
```

## Sonuç

✅ İki katmanlı filtreleme: Admin önce, Tag sonra
✅ Genişletilebilir QueFilter objesi
✅ Event-driven yapı: sealed class pattern
✅ Admin ve normal kullanıcı modları destekleniyor
✅ Mevcut admin panel yapısı korundu (tab sistemi)
