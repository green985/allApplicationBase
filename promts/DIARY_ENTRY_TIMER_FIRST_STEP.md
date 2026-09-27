# Diary Entry Timer — İlk Küçük KMP Adımı

Mevcut Diary entry oluşturma akışına, alan seçiminin hemen altında basit bir zamanlama seçimi ekle.
Bu görev küçük bir ilk sürüm olmalı; mevcut Android/Wear stopwatch sistemini KMP'ye taşımaya
çalışma.

## Ürün davranışı

Entry editor içinde kullanıcı sırasıyla şunları görsün:

1. Alan seçimi
2. Zamanlama seçimi
3. İsteğe bağlı not
4. Kaydet veya Başlat butonu

İlk sürümde yalnız iki seçenek olsun:

```text
Yapıldı
5 sn
```

Davranış:

- `Yapıldı`: Kullanıcı işi daha önce tamamlamıştır. Aktif sayaç çalıştırmadan entry kaydedilir.
- `5 sn`: Kullanıcı şimdi çalışacaktır. Buton metni `Başlat` olur, 5 saniyelik countdown başlar.
- Countdown sırasında kalan süre `00:05` ... `00:00` şeklinde gösterilir.
- Sayaç tamamlandığında entry otomatik kaydedilir.
- Sayaç tamamlanmadan entry tamamlanmış sayılmaz.
- Sayaç sırasında `İptal` aksiyonu bulunur. İptal edilen sayaç entry oluşturmaz ve editor içeriğini
  silmez.
- Aynı anda yalnız bir Diary entry sayacı çalışabilir.
- Bu ilk sürümde pause/resume, background service, notification, alarm, stopwatch-up counter ve özel
  süre
  girişi ekleme.

`5 sn` yalnız ilk entegrasyonu doğrulamak için demo süresidir. Süreyi dağınık magic number olarak
yazma; tek bir sabit/preset modelinden gelsin ki sonra 5/10/25 dakika seçenekleri eklenebilsin.

## State-driven yapı

Bütün timer davranışı `DiaryOperator` içinde olmalıdır. Compose yalnız hazır state'i çizsin ve
Action
göndersin.

Platformdan bağımsız modelleri mevcut sahiplik kurallarına göre `kmpModels` içinde oluştur. Önerilen
minimum modeller:

```kotlin
enum class EntryTimingMode {
    ALREADY_DONE,
    COUNTDOWN,
}

enum class EntryTimerStatus {
    IDLE,
    RUNNING,
    FINISHED,
}
```

Mevcut `TimerTypeEntry` aynı amacı temiz biçimde karşılıyorsa duplicate enum üretme; ancak
`ALREADY_DONE` durumunu `COUNTDOWN` veya `STOPWATCH` gibi göstermeye çalışma. Tamamlanma biçimi ile
timer motoru farklı kavramlarsa ayrı tut.

`DiaryUiState` render-ready alanlar taşısın. Mevcut isimlere göre uyarlayarak minimum olarak:

```kotlin
val selectedTimingMode: EntryTimingMode?
val timerStatus: EntryTimerStatus
val selectedDurationSeconds: Long?
val remainingSeconds: Long
val timerLabel: String
val primaryButtonLabel: String
val canSubmitEntry: Boolean
```

Gerekli `DiaryAction` değerleri:

```kotlin
data class TimingModeSelected(val mode: EntryTimingMode) : DiaryAction
data object StartEntryTimerClicked : DiaryAction
data object CancelEntryTimerClicked : DiaryAction
```

Mevcut `SaveEntryClicked` aksiyonunu `Yapıldı` akışında kullanabilir veya tek bir primary action'a
dönüştürebilirsin. Gereksiz duplicate aksiyon oluşturma.

Kurallar:

- `AddEntryClicked` yeni editor state'ini timer bakımından da sıfırlamalıdır.
- `EntryDialogDismissed`, çalışan timer job'unu iptal etmelidir.
- Operator yaşam döngüsü sona erdiğinde timer coroutine scope ile birlikte iptal olmalıdır.
- Formatlama, buton label'ı, enabled kararı ve timer koşulları Compose içinde hesaplanmamalıdır.
- Compose `delay`, coroutine timer veya feature `LaunchedEffect` kullanmamalıdır.

## Timer uygulaması

KMP `commonMain` uyumlu coroutine ve `kotlin.time.Clock` kullan. Android `System.currentTimeMillis`,
`CountDownTimer`, Service veya AlarmManager kullanma.

Countdown yalnız `delay(1000)` ile değeri eksiltmeye dayanmamalıdır. Başlangıç/bitiş anını sakla ve
her tick'te kalan süreyi duvar saatinden hesapla. Böylece geciken coroutine tick'leri süreyi
kaydırmaz.

Önerilen akış:

```text
Start action
-> startedAt snapshot
-> targetEnd snapshot
-> timerStatus = RUNNING
-> wall clock üzerinden remainingSeconds güncelle
-> remainingSeconds = 0
-> timerStatus = FINISHED
-> createEntry()
```

Timer job'u `DiaryOperator` içinde tutulmalı ve yeni timer başlamadan önce önceki job güvenli
biçimde
iptal edilmelidir. Bu küçük özellik için yeni timer framework, manager, middleware veya platform
servisi oluşturma.

## Entry kaydetme koşulları

Kullanıcının yalnız alan ve zamanlama seçerek entry tamamlayabilmesi isteniyor. Bu nedenle:

- `selectedArea` zorunludur.
- `selectedTimingMode` zorunludur.
- Not opsiyoneldir.
- Mevcut `text` alanı UI'da notu temsil ediyorsa boş olmasına izin ver.
- Mevcut database `text_not_blank` constraint'i ve backend validation'ı bu davranışla çelişiyorsa
  yeni ileri yönlü migration ile düzelt; eski migration dosyasını değiştirme.
- Kolay uyumluluk için `text` kolonunu non-null ve default boş string bırakabilirsin; yalnız blank
  constraint'i kaldır. Yeni `note`/`text` isim göçünü bu küçük görevde genişletme.
- Save butonu için Compose içinde `selectedArea != null || text.isNotBlank()` benzeri karar yazma.
  `canSubmitEntry` Operator tarafından hazırlanmalıdır.

## Backend ve migration

Mevcut `diary_entries` tablosunu yeni ileri yönlü migration ile genişlet. Eski migration'ı
değiştirme.

Minimum yeni alanlar:

```text
timing_mode text not null
duration_seconds bigint null
started_at timestamptz null
ended_at timestamptz null
```

Sözleşme:

- `ALREADY_DONE`: `durationSeconds`, `startedAt`, `endedAt` null olabilir.
- `COUNTDOWN`: `durationSeconds > 0`, `startedAt` ve `endedAt` bulunmalıdır.
- `endedAt >= startedAt` olmalıdır.
- İlk sürümde countdown süresi 5 saniyedir.
- Client'tan gelen enum/string değerleri backend tarafından doğrulanmalıdır.
- `createdAt` ve `updatedAt` backend üretmeye devam etmelidir.

Uygun check constraint'leri ekle; fakat gelecekte başka countdown presetleri eklenmesini engelleyen
`duration_seconds = 5` constraint'i yazma.

Mevcut `EntryPostBody`, `EntryResponse`, `EntryEntity` ve gerekliyse `EntryPatchBody` alanlarını
gerçek
sözleşmeyle uyumlu hale getir. Wire modelleri `@Serializable` kalmalı. Timestamp değerleri ISO-8601
UTC string, entity değerleri mümkünse `Instant` olmalıdır.

Örnek `ALREADY_DONE` request:

```json
{
  "areaId": "WORK",
  "text": "",
  "entryDate": "2026-09-27",
  "timingMode": "ALREADY_DONE"
}
```

Örnek countdown tamamlandıktan sonraki request:

```json
{
  "areaId": "MIND",
  "text": "Kısa odak çalışması",
  "entryDate": "2026-09-27",
  "timingMode": "COUNTDOWN",
  "durationSeconds": 5,
  "startedAt": "2026-09-27T13:43:22Z",
  "endedAt": "2026-09-27T13:43:27Z"
}
```

Create endpoint'in `GenericResponse<EntryResponse>` sözleşmesini koru. Ayrı bir timer endpoint'i
oluşturma; timer sonucu entry oluşturma request'inin parçasıdır.

## Create operasyonu ile entegrasyon

Mevcut `executeOperation` ve `entryOperation` yapısını kullan:

- `ALREADY_DONE` seçildiyse primary action doğrudan mevcut create operasyonunu çağırır.
- `COUNTDOWN` seçildiyse primary action önce timer'ı başlatır; create yalnız timer başarıyla
  tamamlandığında çağrılır.
- Network çağrısı timer job'unun içinde dağınık şekilde yazılmamalı; timer tamamlandığında mevcut
  ortak `saveEntry/createEntry` fonksiyonunu çağır.
- Create devam ederken ikinci create başlatma.
- Network hatasında alan, timing seçimi ve not korunmalı; retry mümkün olmalı.
- Başarıda backend'in gerçek entry id'sini kullanmaya devam et.
- Autosave, kullanıcı daha timing seçmeden veya countdown tamamlanmadan entry oluşturmamalıdır.
- Countdown input değişikliklerinden bağımsızdır; her text değişiminde yeniden başlamamalıdır.

## UI

Alan seçeneklerinin hemen altına tek seçimli iki küçük seçenek ekle:

```text
[ Yapıldı ] [ 5 sn ]
```

- Mevcut tasarım dili ve segmented button yaklaşımını kullan.
- Running sırasında seçimleri ve not alanını değiştirmeyi engelle veya state tarafından belirlenen
  açık bir davranış uygula.
- Running durumda kalan süre ve `İptal` göster.
- Compose içinde timer hesaplama, validation veya save kararı bulunmasın.
- Preview'ları yeni state alanlarıyla güncelle.

## Mevcut stopwatch kodundan yararlanma sınırı

Projede bulunan Android-first `StopwatchOperationUseCase`, `StopwatchVm`, Room stopwatch repository,
Wear foreground service ve Tile/Alarm kodlarını KMP modüllerine bağlama. Bunlar Android, Timber,
Room, Service veya legacy modül bağımlılıkları taşır.

Yalnız şu fikirleri referans alabilirsin:

- wall-clock tabanlı remaining time hesabı,
- `StopwatchTickResult` benzeri sade tick state'i,
- tek aktif job,
- cancel/finish ayrımı,
- `MM:SS` formatı.

Kod taşımak KMP modüllerinin Android legacy modüllerine bağımlı olmasını gerektiriyorsa kodu taşıma;
küçük platformdan bağımsız karşılığını mevcut Operator içinde yaz.

## Kapsam dışı

- Android/Wear timer servislerini refactor etmek
- Background timer garantisi
- Notification ve alarm
- Pause/resume
- Stopwatch ileri sayım modu
- Serbest süre girişi
- Timer geçmiş ekranı
- Offline sync
- Ayrı timer endpoint'i
- Legacy Android migration

## Doğrulama

En az şu durumları doğrula:

1. Alan + `Yapıldı`, boş notla entry oluşturabilir.
2. `5 sn` seçimi sayaç başlatır ve 0'da yalnız bir entry oluşturur.
3. Countdown iptali entry oluşturmaz ve editor verisini korur.
4. Aynı anda ikinci timer veya ikinci create başlamaz.
5. Timer süresi geciken tick'lerden etkilenmez; wall-clock üzerinden hesaplanır.
6. Countdown request'i duration/start/end alanlarını doğru gönderir.
7. Network hatasında kullanıcı seçimi ve not kaybolmaz.
8. Compose yalnız state çizer ve Action gönderir.
9. Quote ve mevcut entry endpointleri bozulmaz.
10. Etkilenen KMP Android ve Wasm hedefleri derlenir.

Sonuçta değişen dosyaları, migration'ı ve background timer'ın neden sonraki aşamaya bırakıldığını
kısa
şekilde raporla.
