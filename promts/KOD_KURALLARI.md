# Kod Kuralları (Code Rules)

## Compose Fonksiyonları: UI State üzerinden Yönetim

### YANLIŞ: Hardcoded Data in Compose

Compose fonksiyonunun içinde liste, konstant, veya konfigürasyon tanımlama:

```kotlin
@Composable
fun AdminApproveQuestionContent(
    uiState: AdminApproveQuestionUiState,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
) {
    val tabs = listOf(
        // YANLIŞ!
        ALL to LanguageKey.all,
        PENDING to LanguageKey.pending,
        APPROVED to LanguageKey.approved,
        DECLINED to LanguageKey.declined,
    )

    val statusColors = mapOf(
        // YANLIŞ!
        "PENDING" to Color.Orange,
        "APPROVED" to Color.Green,
        "DECLINED" to Color.Red,
    )

    val filterOptions = listOf("All", "Active", "Archived")  // YANLIŞ!

    // ... kullanım
}
```

### DOĞRU: All Data from UI State

Tüm data, konfigürasyon ve liste ViewModel/State üzerinden gelir:

```kotlin
data class AdminApproveQuestionUiState(
    val tabs: ImmutableList<TabItem> = persistentListOf(),
    val statusColors: ImmutableMap<String, Color> = persistentMapOf(),
    val filterOptions: ImmutableList<String> = persistentListOf(),
    val currentTabIndex: Int = 0,
    val isLoading: Boolean = false,
)

data class TabItem(
    val filterType: QuestionListFilterType,
    val label: String,
)

@Composable
fun AdminApproveQuestionContent(
    uiState: AdminApproveQuestionUiState,  // Tüm data buradan
    onEvent: (AdminApproveQuestionEvent) -> Unit,
) {
    // tabs, colors, options hepsi uiState'den
    val tabs = uiState.tabs
    val statusColors = uiState.statusColors
    val filterOptions = uiState.filterOptions

    // ... kullanım
}
```

## Neden Bu Kural?

1. **Test Edilebilirlik**: UI'ı farklı state'lerle test et
2. **Esneklik**: Dinamik data daha kolay
3. **Sorumluluk Ayrımı**: Compose = render, ViewModel = logic + data
4. **Lokalizasyon**: Language key'ler runtime'da değişebilir
5. **Tema/Renk**: Tema değişimi runtime'da reflect edilir

## Örnek: Tab Konfigürasyonu

### YANLIŞ

```kotlin
@Composable
fun TabsScreen(uiState: TabsUiState) {
    val tabs = listOf(
        "Home" to Icon.Home,
        "Settings" to Icon.Settings,
        "Profile" to Icon.Profile,
    )

    Column {
        tabs.forEach { (label, icon) ->
            Tab(label = label, icon = icon)
        }
    }
}
```

### DOĞRU

```kotlin
data class TabsUiState(
    val tabs: ImmutableList<TabDefinition> = persistentListOf(),
    val selectedTabIndex: Int = 0,
)

data class TabDefinition(
    val label: String,
    val icon: Int,
    val route: String,
)

@Composable
fun TabsScreen(uiState: TabsUiState) {
    Column {
        uiState.tabs.forEach { tabDef ->
            Tab(label = tabDef.label, icon = tabDef.icon)
        }
    }
}
```

## ViewModel Sorumluluğu

Tab listesi, filtre seçenekleri, durumlar ViewModel'de hazırlanır:

```kotlin
class AdminApproveQuestionVm(
    private val questionListVm: QuestionListVm,
    private val userRepository: UserRepository,
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(AdminApproveQuestionUiState())

    init {
        viewModelScope.launch(getDispatcherIo()) {
            val tabs = buildTabList()  // ViewModel'de hazırla
            val colors = buildStatusColors()
            uiState.update {
                it.copy(tabs = tabs.toImmutableList(), statusColors = colors.toImmutableMap())
            }
        }
    }

    private fun buildTabList(): List<TabItem> {
        return listOf(
            TabItem(ALL, LanguageKey.all),
            TabItem(PENDING, LanguageKey.pending),
            TabItem(APPROVED, LanguageKey.approved),
            TabItem(DECLINED, LanguageKey.declined),
        )
    }

    private fun buildStatusColors(): Map<String, Color> {
        return mapOf(
            "PENDING" to Color.Orange,
            "APPROVED" to Color.Green,
            "DECLINED" to Color.Red,
        )
    }
}
```

## ImmutableList Kullanımı (KRİTİK)

UiState içinde **HER ZAMAN** ImmutableList kullan:

```kotlin
data class QuestionListUiState(
    val questions: ImmutableList<QuestionOperationResponseBody> = persistentListOf(),
    val tags: ImmutableList<String> = persistentListOf(),
    val filterOptions: ImmutableList<FilterOption> = persistentListOf(),
)

// ViewModel'de update
uiState.update {
    it.copy(questions = newQuestions.toImmutableList())
}
```

## Uygulanacak Alanlar

- Tab/Navigation konfigürasyonları
- Text/Label listleri
- Dinamik buton/aksiyon listleri
- Sıralama/Gruplama seçenekleri
- Validasyon mesajları
- **Tüm liste verileri (questions, users, messages, vb.)**

## İstisna

Sabit compose-only render yardımcıları kabul edilebilir:

```kotlin
@Composable
private fun renderStarRating(count: Int) {
    Row {
        repeat(count) {
            Icon(Icons.Filled.Star)  // OK: Simple render logic
        }
    }
}
```

Ancak veri listesi, seçenekler, konfigürasyonlar HER ZAMAN UiState'ten.

## Kontrol Listesi

- [ ] Compose fonksiyonunda `val list = listOf(...)` varsa? → ViewModel'e taşı
- [ ] Compose'ta `val colors = mapOf(...)` varsa? → UiState'e taşı
- [ ] Compose'ta hardcoded text array? → Language key'leri UiState'e taşı
- [ ] Dinamik filtreler/seçenekler? → UiState'ten al
- [ ] Tab listesi tanımı? → UiState'te
- [ ] Status isimlendirmesi? → UiState'te
- [ ] Liste verisi (List<T>)? → **ImmutableList<T>** olarak UiState'te
- [ ] Map verisi (Map<K,V>)? → **ImmutableMap<K,V>** olarak UiState'te

## Özet: Compose vs ViewModel

| Element | Compose ❌ | UiState ✅ |
|---------|-----------|-----------||
| Tab listesi | `val tabs = listOf(...)` | `tabs: ImmutableList<Tab>` |
| Renkler | `val colors = mapOf(...)` | `colors: ImmutableMap<String, Color>` |
| Filtreler | `val filters = listOf(...)` | `filters: ImmutableList<Filter>` |
| Sorular | `val questions = listOf(...)` | `questions: ImmutableList<Question>` |
| Seçenekler | `val options = listOf(...)` | `options: ImmutableList<Option>` |
| Hardcoded text | `"Submit"` | `submitButtonText: String` |

**Kural:** Compose = **sadece render**. Data = **UiState'ten**. Liste = **ImmutableList**.
