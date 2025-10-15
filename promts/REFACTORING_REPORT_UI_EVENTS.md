# UI Event Pattern Refactoring - Question Project

## Status

Question projesi: sealed class Event pattern, callback parametresi kaldırıldı. WARP.md "UI Event
Pattern (KRİTİK)" ile uyumlu.

## Değişiklikler

### AdminApproveQuestionScreenSetup.kt

Eski:

```kotlin
onFilterSelected: (QuestionListFilterType) -> Unit
onApproveAll: () -> Unit
onDeclineAll: () -> Unit
```

Yeni:

```kotlin
onEvent: (AdminApproveQuestionEvent) -> Unit
```

ScreenSetup içindeki when-logic kaldırıldı. İş mantığı ViewModel'e taşındı.

### AdminApproveQuestionVM.kt

Event handling:

```kotlin
sealed class AdminApproveQuestionEvent : BaseEvent() {
    data class OnFilterSelected(val filterType: QuestionListFilterType) :
        AdminApproveQuestionEvent()
    data object OnApproveAll : AdminApproveQuestionEvent()
    data object OnDeclineAll : AdminApproveQuestionEvent()
    data object OnRefreshClicked : AdminApproveQuestionEvent()
}
```

QuestionListVm inject edildi. Tüm routing ViewModel içinde.

## Doğru Kullanılan Dosyalar

1. QuestionListScreenSetup.kt - onEvent: (QuestionListEvent) -> Unit
2. QuestionCreateQuestionScreenSetup.kt - onEvent: (QuestionViewEvent) -> Unit + onCreateEvent
3. QuestionViewContainers.kt - event pattern
4. BaseQuestionView ve alt view'lar - event pattern

## Kurallar (WARP.md)

### UI Event Pattern

- sealed class Event kullan
- callback parametresi kaldır
- Preview/Test: onEvent = {}

### Screen Setup Sorumlulukları

- Sadece bağlantı: koinViewModel(), collectAsState(), onEvent = { vm.onEvent(it) }
- İş mantığı ViewModel'de
- when/if logic: ViewModel'de, ScreenSetup'ta değil
- Birden fazla ViewModel: parent ViewModel'e inject et

## Analiz

Refactor edilen: 2 dosya
Kaldırılan callback: 3
Eklenen event: 3
Kaldırılan when/if logic: 1
ViewModel injection: 1
Kod satır azalması: ~20

Tüm değişiklikler compiled. Breaking change yok.
