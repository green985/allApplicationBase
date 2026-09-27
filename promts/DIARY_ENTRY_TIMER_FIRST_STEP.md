# Diary Entry Bazlı Timer — KMP, Supabase ve Deploy

Mevcut Diary entry akışını entry bazlı countdown destekleyecek şekilde küçük ve genişletilebilir bir
yapıya refactor et. Timer dialogun geçici bir özelliği değil, backend'de saklanan entry'nin kendi
durumu olmalıdır.

Mevcut Android/Wear stopwatch sistemini KMP'ye taşımaya çalışma. İlgisiz legacy Android koduna ve
quote endpoint'ine dokunma.

## Temel ürün kararı

Kullanıcı aşağıdaki bilgilerden yalnız timer süresini seçerek bile entry oluşturabilmelidir:

- Alan: opsiyonel
- Not: opsiyonel
- Timer süresi: entry oluşturmak için tek başına yeterli

Entry oluşturulduğunda backend gerçek bir `id` döndürür ve editor dialogu kapanmaz. Bundan sonraki
alan, not ve timer güncellemeleri aynı entry id üzerinden yapılır.

Dialog kapanırsa:

- Timer henüz başlamadıysa entry `PENDING` durumunda listede kalır.
- Timer çalışıyorsa çalışmaya devam eder ve listede countdown görünür.
- Dialogu kapatmak timer job'unu iptal etmez.
- Kullanıcı entry'yi daha sonra açıp alan/not/süre bilgilerini düzenleyebilir.

## UI süre seçenekleri

Alan seçiminin altında şu seçenekleri göster:

```text
[ Yapıldı ] [ 5 dk ] [ 10 dk ] [ 15 dk ] [ 20 dk ] [ Özel ]
```

`Özel` seçilince dakika cinsinden sayısal input göster. Değer pozitif tam sayı olmalı ve makul üst
sınır tek bir sabit üzerinden tanımlanmalıdır.

Örnek dönüşümler:

```text
5 dk  -> 300 saniye
10 dk -> 600 saniye
15 dk -> 900 saniye
20 dk -> 1200 saniye
Özel 7 dk -> 420 saniye
```

Süre hesabı ve doğrulaması Compose içinde yapılmamalı; Action Operator'a gönderilmeli ve
render-ready
state hazırlanmalıdır.

## Entry lifecycle ve türetilen durum

Timer status backend'de ayrı bir kolon olarak tutulmamalıdır. Aşağıdaki durumlar yalnız UI/domain
modelidir ve saklanan zaman bilgilerinden Operator tarafından türetilmelidir:

```kotlin
enum class EntryTimerStatus {
    PENDING,
    RUNNING,
    FINISHED,
    CANCELLED,
}
```

Anlamları:

- `PENDING`: Entry oluşturuldu, timer süresi atanmış olabilir fakat henüz başlamadı.
- `RUNNING`: `timerStartedAt` ve `timerEndsAt` vardır; listede countdown gösterilir.
- `FINISHED`: Süre tamamlandı veya kullanıcı `Yapıldı` seçti.
- `CANCELLED`: Başlatılmış timer kullanıcı tarafından iptal edildi.

Backend yalnız şu kalıcı gerçekleri saklar:

```text
durationSeconds
timerStartedAt
timerEndsAt
timerCancelledAt
completedAt
```

Durum şu kuralla türetilmelidir:

```text
timerCancelledAt != null                       -> CANCELLED
completedAt != null                            -> FINISHED
timerEndsAt != null && timerEndsAt <= now      -> FINISHED
timerStartedAt != null && timerEndsAt != null  -> RUNNING
durationSeconds != null                        -> PENDING
```

`Yapıldı` seçimi aktif timer başlatmaz; backend `completedAt` değerini server zamanı ile doldurur.
Timer ile tamamlanan bir entry için planlanan tamamlanma anı `timerEndsAt` üzerinden bilinir. İlk
sürümde ayrıca finish çağrısı veya backend job'u gerekmez.

Backend saniyelik countdown çalıştırmamalı, tick üretmemeli, kalan süreyi güncellememeli ve timer
bitince kendiliğinden request göndermemelidir. Backend'in görevi yalnız kalıcı zaman gerçeklerini
doğrulamak ve saklamaktır. Canlı countdown ve türetilen status `DiaryOperator` sorumluluğudur.

`durationSeconds` korunmalıdır çünkü timer başlamadan önce seçilmiş süreyi saklar ve kullanıcının
orijinal 5/10/15/20/özel dakika tercihini temsil eder. Timer başladıktan sonra teknik olarak
`timerEndsAt - timerStartedAt` ile hesaplanabilse bile PENDING aşamasında bu iki zaman henüz yoktur.

Countdown akışı:

```text
Süre seç
-> entry oluştur veya mevcut draft entry'yi güncelle
-> backend gerçek entry id döndürür
-> dialog açık kalır
-> Başlat
-> entry RUNNING olur
-> dialog kapanabilir
-> listede entry'ye ait countdown görünür
-> timerEndsAt <= now olduğunda Operator entry'yi FINISHED gösterir
-> kullanıcıya bitiş uyarısı gösterilir
```

Timer tamamlandığında yeni entry oluşturma. Timer her zaman daha önce oluşturulmuş gerçek bir entry
id üzerinde çalışmalıdır.

## Kaydetme ve dialog davranışı

- Timer preset veya geçerli özel süre seçilmişse alan/not boş olsa bile kayıt oluşturulabilmelidir.
- İlk `Kaydet` işlemi POST ile entry oluşturur ve dialogu açık bırakır.
- Create başarılı olunca `editingEntryId = response.id` yapılmalıdır.
- Sonraki değişiklikler yeni POST değil aynı id üzerinde PATCH kullanmalıdır.
- Timer seçimini değiştirmek yeni entry oluşturmamalıdır.
- Create/PATCH devam ederken ikinci aynı istek başlamamalıdır.
- Network hatasında seçilen süre, alan ve not korunmalıdır.
- Kullanıcı create isteği sürerken dialogu kapatmak isterse `dismissRequested` benzeri state tut:
    - istek bitmeden dialogu ve local veriyi yok etme,
    - başarıdan sonra dialogu kapat ve entry'yi listede `PENDING` göster,
    - hata olursa dialog açık kalsın ve retry mümkün olsun.
- Başarılı kayıtta dialogun varsayılan olarak kapanmaması gerekir.

Mevcut 700 ms autosave varsa gerçek server id oluşmadan PATCH çalıştırma. Timer seçimi ilk kaydı
oluşturabilecek olsa da her seçim değişikliğinde yeni POST üretme.

## State-driven KMP modelleri

Mevcut modellere göre isimleri uyarlayarak platformdan bağımsız state oluştur. Duplicate model
üretme.

Editor için minimum render-ready alanlar:

```kotlin
val editingEntryId: String?
val selectedTimerPreset: EntryTimerPreset?
val customDurationMinutes: String
val selectedDurationSeconds: Long?
val timerStatus: EntryTimerStatus // Operator tarafından zaman alanlarından türetilir
val timerLabel: String
val primaryButtonLabel: String
val canCreateOrUpdateEntry: Boolean
val dismissRequested: Boolean
```

Liste item'ı timer bilgisini doğrudan çizilebilir biçimde taşımalıdır:

```kotlin
val timerStatusLabel: String
val countdownLabel: String?
val isTimerRunning: Boolean
val canStartTimer: Boolean
val canCancelTimer: Boolean
```

Presetleri dağınık magic number olarak yazma. Örneğin:

```kotlin
enum class EntryTimerPreset(val durationSeconds: Long?) {
    ALREADY_DONE(null),
    FIVE_MINUTES(5 * 60L),
    TEN_MINUTES(10 * 60L),
    FIFTEEN_MINUTES(15 * 60L),
    TWENTY_MINUTES(20 * 60L),
    CUSTOM(null),
}
```

Serialization gereken wire enumları ile yalnız UI seçimini temsil eden modelleri karıştırma.

Gerekli Action'ları mevcut `DiaryAction` içine ekle:

```kotlin
data class TimerPresetSelected(val preset: EntryTimerPreset) : DiaryAction
data class CustomDurationChanged(val minutes: String) : DiaryAction
data object SaveEntryWithoutClosingClicked : DiaryAction
data class StartEntryTimerClicked(val entryId: String) : DiaryAction
data class CancelEntryTimerClicked(val entryId: String) : DiaryAction
```

İsimleri mevcut action stiline göre uyarlayabilirsin. Compose yalnız Action göndermeli; timer, save,
validation veya dialog kapatma kararı vermemelidir.

## Entry listesinde countdown

Countdown editor state'ine bağlı olmamalıdır. Entry listesi kapalı editorle de timerı göstermelidir.

- Hesap kaynağı `timerEndsAt - Clock.System.now()` olmalıdır.
- Yalnız `delay(1000)` ile state'i bir azaltmaya güvenme; her tick'te wall-clock üzerinden yeniden
  hesapla.
- Dialog kapanınca çalışan timer job'u iptal etme.
- Gün değiştirildiğinde veya Operator kapandığında gereksiz job'ları iptal et.
- İlk sürümde aynı anda yalnız bir RUNNING Diary timer destekle. İkinci timer başlatılırsa
  kullanıcıya
  anlaşılır state/error göster.
- Entry listesi backend'den tekrar yüklendiğinde gelecekteki `timerEndsAt` değeri varsa
  countdown kaldığı yerden UI'da hesaplanabilmelidir.
- `timerEndsAt <= now` ise entry efektif olarak `FINISHED` gösterilmelidir; yalnız local job
  sonucuna
  güvenme.

Sayaç bittiğinde:

- İlgili entry'nin render state'i `FINISHED` olarak güncellenir.
- Backend finish endpoint'i çağrılmaz; bitiş `timerEndsAt <= now` kuralından türetilir.
- Liste item'ında `Tamamlandı` gösterilir.
- Tek kullanımlık uyarı `Effect` veya mevcut ortak snackbar sistemi üzerinden üretilir.
- Compose içinde bitiş kontrolü veya snackbar kararı yazılmaz.

## Database migration

Mevcut migration dosyalarını değiştirme. `diary_entries` için yeni ileri yönlü migration oluştur.

Mevcut tabloyu en az şu alanlarla genişlet:

```text
area_id text null
text text not null default ''
duration_seconds bigint null
timer_started_at timestamptz null
timer_ends_at timestamptz null
timer_cancelled_at timestamptz null
completed_at timestamptz null
```

Mevcut `area_id not null` ve `text_not_blank` kuralları yalnız timer ile kayıt oluşturma
davranışıyla
çelişiyorsa yeni migration içinde güvenli şekilde güncellenmelidir.

Constraint kuralları:

- `duration_seconds` null veya pozitif olsun.
- `timer_ends_at >= timer_started_at` olmalıdır.
- `timer_started_at` varsa `timer_ends_at` ve pozitif `duration_seconds` bulunmalıdır.
- `timer_cancelled_at` yalnız başlatılmış timer için bulunmalıdır.
- `Yapıldı` kaydı için `completed_at` bulunur; süre ve timer zamanları null olabilir.
- Constraint yalnız 5/10/15/20 dakikaya kilitlenmemeli; özel süreyi desteklemelidir.

`PENDING`, `RUNNING`, `FINISHED`, `CANCELLED` için database status kolonu ekleme. Ayrı status ile
zaman alanlarının birbiriyle çelişebileceği ikinci bir doğruluk kaynağı oluşturma.

`EntryPostBody`, `EntryPatchBody`, `EntryResponse` ve `EntryEntity` modellerini bu sözleşmeyle
uyumlu
hale getir. Wire timestamp değerleri ISO-8601 UTC string, entity değerleri `Instant` olmalıdır.

## API route'ları

Quote ve entry işlemlerini birleştiren monolit response oluşturma. Entry route'ları ayrı çalışsın.

Mevcut `kmpFunctions` Edge Function içinde küçük route handler'ları ekle/güncelle:

```text
GET   /diary/days/{date}/entries
POST  /diary/entries
PATCH /diary/entries/{entryId}
POST  /diary/entries/{entryId}/timer/start
POST  /diary/entries/{entryId}/timer/cancel
```

- POST, yalnız timer süresiyle entry oluşturabilmelidir; bu kayıt zaman alanlarından `PENDING`
  olarak yorumlanır.
- `Yapıldı` seçimi request içinde bir komut alanıyla (`markAsCompleted = true` gibi) belirtilmeli;
  client doğrudan `completedAt` göndermemeli, backend bunu server zamanı ile üretmelidir. Mevcut API
  isimlendirmesine daha uygun eşdeğer bir alan varsa onu kullan.
- PATCH alan, not/text ve henüz RUNNING olmayan timer süresini güncelleyebilmelidir.
- Start backend saatini kullanarak `timerStartedAt` ve `timerEndsAt` üretmeli ve güncel entry
  döndürmelidir.
- Client'ın gönderdiği keyfi `timerStartedAt/timerEndsAt` değerlerine güvenme.
- Cancel yalnız aktif entry'nin `timerCancelledAt` değerini server zamanı ile doldurmalıdır ve
  idempotent olmalıdır.
- GET yalnız seçilen günün entry listesini döndürmelidir; quote'u aynı response'a ekleme.
- GET saklanan zaman alanlarını döndürmelidir. Client `timerEndsAt <= now` değerini `FINISHED`
  yorumlamalıdır; GET sırasında database satırını finalize etmek için yan etki üretme.
- Bütün cevaplar mevcut `GenericResponse<T>` sözleşmesini kullanmalıdır.
- Raw database hata metnini kullanıcıya sızdırma.
- Route path ve HTTP method birlikte kontrol edilmelidir.
- OPTIONS ve CORS method/header listelerini yeni route'lara göre güncelle.

İlk sürüm için ayrı Edge Function açma; mevcut `kmpFunctions` girişini kullan fakat handler'ları
`getEntries`, `createEntry`, `updateEntry`, `startEntryTimer` ve `cancelEntryTimer` gibi küçük
fonksiyonlarda ayır.

## Request örnekleri

Timer çalıştırmadan `Yapıldı` entry oluşturma:

```json
{
  "entryDate": "2026-09-27",
  "areaId": "BODY",
  "text": "Yürüyüş tamamlandı",
  "markAsCompleted": true
}
```

Bu request sonucunda backend `completedAt` değerini kendi saatiyle üretmelidir.

Yalnız 10 dakikalık timer seçilerek draft entry oluşturma:

```json
{
  "entryDate": "2026-09-27",
  "areaId": null,
  "text": "",
  "durationSeconds": 600
}
```

Alan ve 15 dakikalık timer ile entry oluşturma:

```json
{
  "entryDate": "2026-09-27",
  "areaId": "MIND",
  "text": "Odak çalışması",
  "durationSeconds": 900
}
```

Özel 7 dakika seçimi:

```json
{
  "entryDate": "2026-09-27",
  "areaId": "WORK",
  "text": "Kısa düzenleme",
  "durationSeconds": 420
}
```

Timer başlatma:

```http
POST /diary/entries/{entryId}/timer/start
```

```json
{}
```

Başarılı start cevabı server tarafından üretilen zamanları içermelidir:

```json
{
  "data": {
    "id": "6b27d37a-b194-4f91-b624-657b9f4ec691",
    "entryDate": "2026-09-27",
    "areaId": "MIND",
    "text": "Odak çalışması",
    "durationSeconds": 900,
    "timerStartedAt": "2026-09-27T15:00:00Z",
    "timerEndsAt": "2026-09-27T15:15:00Z",
    "timerCancelledAt": null,
    "completedAt": null,
    "createdAt": "2026-09-27T14:58:00Z",
    "updatedAt": "2026-09-27T15:00:00Z"
  },
  "message": "Timer başlatıldı.",
  "status": true
}
```

## Compose UI

- Timer seçeneklerini alan seçiminin hemen altında göster.
- Özel süre seçilince dakika input'u göster.
- Entry ilk kez kaydedildiğinde dialog açık kalmalı ve buton `Başlat` durumuna geçebilmelidir.
- Dialog kapanınca PENDING/RUNNING entry listede görünmelidir.
- Liste item'ında uygun duruma göre `Bekliyor`, canlı `MM:SS`, `Tamamlandı` veya `İptal edildi`
  göster.
- Liste item'ındaki Start/Cancel/Edit aksiyonları yalnız Action göndermelidir.
- Preview'ları 5, 10, 15, 20 ve özel dakika örnekleri ile değil; en az PENDING, RUNNING ve FINISHED
  durumlarını temsil edecek kadar güncelle.
- Compose içinde `delay`, coroutine, zaman hesabı, API, validation veya feature `LaunchedEffect`
  bulunmamalıdır.

## Mevcut stopwatch kodunu kullanma sınırı

Android-first `StopwatchOperationUseCase`, `StopwatchVm`, Room stopwatch repository, Wear foreground
service, Tile ve Alarm kodlarını KMP modüllerine bağlama. Bunlar Android/legacy bağımlılıkları
taşır.

Yalnız şu fikirleri platformdan bağımsız biçimde uygula:

- wall-clock tabanlı remaining time,
- tek aktif timer,
- cancel/finish ayrımı,
- `MM:SS` formatı,
- başlangıç ve bitiş zamanlarından resume.

Yeni timer manager/framework oluşturma; küçük timer koordinasyonu `DiaryOperator` içinde kalsın.

## Supabase uygulama, deploy ve URL güncellemesi

Kod tamamlandıktan sonra yalnız ilgili Supabase değişikliklerini uygula:

1. Yeni migration'ı oluştur ve SQL'i doğrula.
2. Bağlı proje bilgisini `supabase/config.toml` ve mevcut local project-ref üzerinden doğrula; yeni
   proje uydurma.
3. Migration'ı bağlı Supabase projesine uygula:

```bash
supabase db push
```

4. Güncellenen function'ı deploy et:

```bash
supabase functions deploy kmpFunctions
```

5. Deploy sonucundaki gerçek function URL'ini doğrula. Bu projedeki mevcut project ref değişmediyse
   base URL şu biçimdedir:

```text
https://uduwhuvgdcacvdhzheyi.supabase.co/functions/v1/kmpFunctions
```

6. KMP `EndpointStrings` içinde tek bir `kmpFunctions` base URL kullan ve route'ları buradan üret:

```text
GET  {base}/diary/days/{date}/entries
POST {base}/diary/entries
PATCH {base}/diary/entries/{entryId}
POST {base}/diary/entries/{entryId}/timer/start
POST {base}/diary/entries/{entryId}/timer/cancel
```

7. Secret/service-role key'i kaynak koda, prompt çıktısına veya loglara yazma.
8. Deploy yetkisi veya Supabase oturumu yoksa deploy edilmiş gibi davranma; kodu tamamla ve kalan
   komutu açıkça raporla.
9. Deploy sonrası en az OPTIONS ve yetkili bir create/get/start akışını güvenli test verisiyle
   doğrula. Üretilen test entry'sini raporda belirt; kullanıcı verisini silme/değiştirme.

## Kapsam dışı

- Android/Wear stopwatch refactoru
- Native foreground service, notification ve alarm
- Pause/resume
- Aynı anda birden fazla aktif timer
- Stopwatch ileri sayım modu
- Offline sync/queue
- Ayrı timer geçmiş ekranı
- Quote ve entry'yi tek response'ta birleştirmek
- Legacy Android migration

## Doğrulama

En az şu senaryoları doğrula:

1. Yalnız `5 dk` seçilerek alan ve not olmadan PENDING entry oluşturulur.
2. `10 dk`, `15 dk`, `20 dk` doğru saniye değerlerine dönüşür.
3. Özel `7 dk` değeri 420 saniye olarak kaydedilir.
4. İlk create dialogu kapatmaz ve gerçek backend id'sini editor state'e yazar.
5. Create sürerken dismiss istenirse veri kaybolmaz; başarıdan sonra PENDING entry listede kalır.
6. Timer start aynı entry'yi RUNNING yapar; ikinci entry oluşturmaz.
7. Dialog kapalıyken entry listesinde countdown görünür.
8. Geciken tick süreyi kaydırmaz; kalan süre wall-clock üzerinden hesaplanır.
9. Sayaç bitince Operator `timerEndsAt <= now` üzerinden FINISHED türetir, listede tamamlandı
   görünür ve tek uyarı üretilir; finish endpoint çağrılmaz.
10. Uygulama yeniden açıldığında geçmiş `endsAt` değeri FINISHED olarak yorumlanır.
11. Cancel entry'yi CANCELLED yapar ve yeni entry oluşturmaz.
12. Network hatasında seçimler korunur ve retry mümkündür.
13. GET entries ile quote GET birbirinden bağımsız çalışır.
14. Supabase migration ve function deploy sonucu doğrulanır.
15. Etkilenen KMP Android ve Wasm hedefleri derlenir.

Sonuçta değişen dosyaları, migration sonucunu, deploy edilen function URL'ini, doğrulanan route'ları
ve background notification'ın neden sonraki aşamaya bırakıldığını kısa şekilde raporla.
