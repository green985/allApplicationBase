# QuestionListWithParams - Parametrik Navigasyon Sistemi

## Genel Bakış

QuestionListVm'i önceden belirlenmiş filtrelerle (tag, adminFilterType) açabilmek için parametrik
navigasyon sistemi. MessageDetailScreenSetup pattern'i baz alınarak oluşturuldu.

## Amaç

- Tag bazlı question listeleme (örn: "KNOWLEDGE" tag'li sorular)
- Admin filter bazlı listeleme (örn: "PENDING_ADMIN" sorular)
- İkisinin kombinasyonu
- Deep linking desteği

## Mimari

### 1. ScreenKey Parametreleri

**Lokasyon:** `sharedScreens/navigation/ScreenKey.kt`

```kotlin
object ScreenKey {
    const val questionTag = "questionTag"
    const val adminFilterType = "adminFilterType"
}
```

### 2. Route Tanımı

**Lokasyon:** `QuestionAppProjectRoutes.kt`

```kotlin
val QuestionListWithParams = Route("question/QuestionListWithParams")
```

### 3. QuestionListWithParamsScreenSetup

**Lokasyon:** `questionScreens/list/QuestionListWithParamsScreenSetup.kt`

**Parametreler:**

```kotlin
@Composable
fun QuestionListWithParamsScreenSetup(
    modifier: Modifier = Modifier,
    questionTagId: String? = null,
    adminFilterTypeStr: String? = null,
)
```

**LaunchedEffect ile Filter Uygulama:**

```kotlin
LaunchedEffect(questionTagId, adminFilterTypeStr) {
    if (questionTagId != null) {
        val tag = QuestionTagCatalog
            .createQuestionTagList.find { it.id == questionTagId }
        vm.setTagFilter(tag)
    }

    if (adminFilterTypeStr != null) {
        val filterType = try {
            QuestionListAdminFilterType.valueOf(adminFilterTypeStr)
        } catch (e: Exception) {
            QuestionListAdminFilterType.ALL
        }
        vm.setAdminFilter(filterType)
    }
}
```

**Özellikler:**

- `koinViewModel<QuestionListVm>()` kullanır (yeni instance)
- LaunchedEffect parametrelere göre filtreyi otomatik uygular
- Tag id'den QueTag object'e resolve eder
- String'den QuestionListAdminFilterType enum'a parse eder
- Hatalı parse durumunda ALL default'a düşer

### 4. Navigation Route Definition

**Lokasyon:** `QuestionAppNavigation.kt`

```kotlin
composable(
    route = "${QuestionAppProjectRoutes.QuestionListWithParams.route}?" +
            "${ScreenKey.questionTag}={questionTag}" +
            "&${ScreenKey.adminFilterType}={adminFilterType}",
    arguments = listOf(
        navArgument(ScreenKey.questionTag) {
            defaultValue = ""
            nullable = true
        },
        navArgument(ScreenKey.adminFilterType) {
            defaultValue = ""
            nullable = true
        }
    )
) { entry ->
    val questionTag = entry.arguments?.getString(ScreenKey.questionTag)
    val adminFilterType = entry.arguments?.getString(ScreenKey.adminFilterType)
    QuestionListWithParamsScreenSetup(
        questionTagId = questionTag,
        adminFilterTypeStr = adminFilterType
    )
}
```

### 5. QuestionListNavigationHelper

**Lokasyon:** `questionScreens/list/QuestionListNavigationHelper.kt`

**Helper fonksiyonları:**

```kotlin
object QuestionListNavigationHelper {
    // Generic route builder
    fun buildQuestionListWithParamsRoute(
        tagId: String? = null,
        adminFilterType: QuestionListAdminFilterType? = null,
    ): String

    // Tag only
    fun buildQuestionListWithTagRoute(tag: QueTag): String

    // Admin filter only
    fun buildQuestionListWithAdminFilterRoute(
        adminFilterType: QuestionListAdminFilterType
    ): String

    // Both filters
    fun buildQuestionListWithFiltersRoute(
        tag: QueTag,
        adminFilterType: QuestionListAdminFilterType,
    ): String
}
```

## Kullanım Örnekleri

### 1. Tag Bazlı Navigasyon

```kotlin
// ViewModel içinde
val tag = QuestionTagCatalog.KNOWLEDGE
val route = QuestionListNavigationHelper.buildQuestionListWithTagRoute(tag)
navigationUseCase.navigate(route)
// Result: "question/QuestionListWithParams?questionTag=knowledge"
```

### 2. Admin Filter Bazlı Navigasyon

```kotlin
val route = QuestionListNavigationHelper.buildQuestionListWithAdminFilterRoute(
    QuestionListAdminFilterType.PENDING_ADMIN
)
navigationUseCase.navigate(route)
// Result: "question/QuestionListWithParams?adminFilterType=PENDING_ADMIN"
```

### 3. Her İki Filter

```kotlin
val tag = QuestionTagCatalog.OPINION
val route = QuestionListNavigationHelper.buildQuestionListWithFiltersRoute(
    tag = tag,
    adminFilterType = QuestionListAdminFilterType.APPROVED_ADMIN
)
navigationUseCase.navigate(route)
// Result: "question/QuestionListWithParams?questionTag=opinion&adminFilterType=APPROVED_ADMIN"
```

### 4. Manuel Route Builder

```kotlin
val route = QuestionListNavigationHelper.buildQuestionListWithParamsRoute(
    tagId = "creativity",
    adminFilterType = QuestionListAdminFilterType.DECLINED_ADMIN
)
navigationUseCase.navigate(route)
```

### 5. Parametre Yoksa

```kotlin
val route = QuestionListNavigationHelper.buildQuestionListWithParamsRoute()
navigationUseCase.navigate(route)
// Result: "question/QuestionListWithParams" (no filters)
```

## Data Flow

```
User clicks navigation
  ↓
navigationUseCase.navigate(buildQuestionListWithTagRoute(tag))
  ↓
Navigation component resolves route
  ↓
QuestionAppNavigation composable matched
  ↓
Arguments extracted from route
  ↓
QuestionListWithParamsScreenSetup called
  ↓
LaunchedEffect triggered
  ↓
Tag resolved from QuestionTagCatalog
  ↓
vm.setTagFilter(tag) + vm.setAdminFilter(filterType)
  ↓
QuestionListVm internal combine() triggers
  ↓
List filtered by parameters
  ↓
UI updated with filtered list
```

## QuestionListScreenSetup vs QuestionListWithParams

| Feature            | QuestionListScreenSetup | QuestionListWithParams                |
|--------------------|-------------------------|---------------------------------------|
| Filter başlangıç   | DEFAULT (tüm sorular)   | Parametrelerle önceden set            |
| Use case           | Genel liste ekranı      | Deep linking, spesifik filtre         |
| Filter değiştirme  | UI'dan manuel           | LaunchedEffect otomatik               |
| Navigation route   | `question/QuestionList` | `question/QuestionListWithParams?...` |
| ViewModel instance | Shared (var ise)        | Her açılışta yeni                     |

## Tag ID Mapping

QuestionTagCatalog'dan tag ID'leri:

```kotlin
"yesNo" → QuestionTagCatalog.YES_NO
"goodBad" → QuestionTagCatalog.GOOD_BAD
"knowledge" → QuestionTagCatalog.KNOWLEDGE
"opinion" → QuestionTagCatalog.OPINION
"emotion" → QuestionTagCatalog.EMOTION
"behavior" → QuestionTagCatalog.BEHAVIOR
"creativity" → QuestionTagCatalog.CREATIVITY
"society" → QuestionTagCatalog.SOCIETY
"future" → QuestionTagCatalog.FUTURE
```

## AdminFilterType Enum Values

```kotlin
QuestionListAdminFilterType.ALL
QuestionListAdminFilterType.PENDING_ADMIN
QuestionListAdminFilterType.APPROVED_ADMIN
QuestionListAdminFilterType.DECLINED_ADMIN
```

**Not:** `NONE` enum değeri yok, `ALL` kullanılıyor.

## Error Handling

### Invalid Tag ID

```kotlin
LaunchedEffect(questionTagId, adminFilterTypeStr) {
    if (questionTagId != null) {
        val tag = QuestionTagCatalog.createQuestionTagList.find { it.id == questionTagId }
        vm.setTagFilter(tag)  // tag null ise filter uygulanmaz
    }
}
```

### Invalid AdminFilterType String

```kotlin
val filterType = try {
    QuestionListAdminFilterType.valueOf(adminFilterTypeStr)
} catch (e: Exception) {
    QuestionListAdminFilterType.ALL  // Default fallback
}
```

## Deep Linking

URL scheme ile doğrudan açılabilir:

```
myapp://question/QuestionListWithParams?questionTag=knowledge
myapp://question/QuestionListWithParams?adminFilterType=PENDING_ADMIN
myapp://question/QuestionListWithParams?questionTag=opinion&adminFilterType=APPROVED_ADMIN
```

## Dosyalar

### Yeni Oluşturulan

- `QuestionListWithParamsScreenSetup.kt`
- `QuestionListNavigationHelper.kt`

### Güncellenen

- `ScreenKey.kt` - questionTag + adminFilterType parametreleri
- `QuestionAppProjectRoutes.kt` - QuestionListWithParams route
- `QuestionAppNavigation.kt` - composable + navArguments

## Test

Build başarılı:

```bash
./gradlew :composeBase:assembleDebug
# BUILD SUCCESSFUL in 1s
```

## Gelecek Geliştirmeler

1. **Multiple tags support:** questionTags=tag1,tag2,tag3
2. **Date range filter:** startDate + endDate parametreleri
3. **Sorting:** sortBy + sortOrder parametreleri
4. **Pagination:** page + pageSize parametreleri
5. **Search query:** searchQuery parametresi
6. **ViewModel state persist:** Process death durumunda SavedStateHandle
7. **Analytics tracking:** Hangi filtrelerle açıldığı track edilebilir
