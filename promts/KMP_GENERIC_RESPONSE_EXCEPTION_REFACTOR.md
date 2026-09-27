# KMP State-Driven Network Operation Refactor

Mevcut KMP network ve Operator yapısını küçük, merkezi ve state-driven bir yapıya refactor et.
İlgisiz Android/KMP koduna dokunma ve yeni mimari katmanlar ekleme.

## Hedef akış

```text
Screen -> Action -> Operator -> Operation -> UiState -> Screen
```

- Compose yalnızca hazır `UiState` değerlerini çizsin ve `Action` göndersin.
- Operator içinde tekrar eden `try/catch` blokları bulunmasın.
- Operator yalnızca operasyonu ve operasyonun state üzerindeki etkisini tanımlasın.
- Exception ayrıştırma ortak altyapıda yapılsın.
- Özel hatalar gerektiğinde Operator'a tip güvenli biçimde iletilebilsin.

## Response ve endpoint sözleşmesi

Backend `GenericResponse<T>` döndürüyor:

```json
{
  "data": {
    "date": "2026-09-27",
    "quote": "Küçük adımlar zamanla büyük değişimler oluşturur.",
    "createdAt": "2026-09-27T08:42:16Z",
    "updatedAt": "2026-09-27T14:18:53Z"
  },
  "message": "Günün sözü başarıyla getirildi.",
  "status": true
}
```

- `status == true` ise `data` non-null olmalıdır.
- `status == true && data == null` response contract hatasıdır.
- `status == false` ise backend `message` değeri hata mesajıdır.
- Boş veya teknik mesaj için güvenli fallback kullanılmalıdır.
- HTTP status ile body içindeki `status` ayrı değerlendirilmelidir.
- `HttpResponse.bodyOrError<T>()` ortak response doğrulama noktası olmalıdır.
- Endpointler `Result<T>` döndürmemeli ve `runCatching` kullanmamalıdır.
- Başarılı endpoint doğrudan modeli döndürmelidir:

```kotlin
suspend fun updateQuote(...): DiaryQuoteResponse
```

`BaseException` platformdan bağımsız KMP domain katmanında kalmalıdır. Yalnız gerçek ihtiyaç varsa
API, HTTP, parse veya contract alt tipi ekle. Geniş exception hiyerarşisi oluşturma.
`CancellationException` hiçbir zaman sarılmamalı, tekrar fırlatılmalıdır.

## UI hata modeli

UI state içine `Throwable` veya `BaseException` koyma. Ortak, platformdan bağımsız modeller kullan:

```kotlin
data class UiError(
    val message: String,
    val code: ErrorCode = ErrorCode.Unknown,
)

enum class ErrorCode {
    Unauthorized,
    Validation,
    NotFound,
    Network,
    Unknown,
}
```

Backend hata sözleşmesinde bulunmayan kodları varsayarak ekleme. Mevcut gerçek kodlara göre modeli
daralt veya genişlet.

Ortak bir `ErrorMapper`, yakalanan hatayı `UiError` değerine dönüştürmelidir:

- Bilinen `BaseException` bilgisini koru.
- Network/timeout hatalarını uygun kullanıcı mesajına dönüştür.
- Teknik exception mesajını kullanıcıya doğrudan gösterme.
- Bilinmeyen hata için güvenli genel mesaj kullan.

## Ortak operation state

Tekrarlanan `isLoading`, `isError` ve `errorMessage` alanları yerine ortak model kullan:

```kotlin
sealed interface OperationState {
    data object Idle : OperationState
    data object Loading : OperationState

    data class Error(
        val error: UiError,
    ) : OperationState
}
```

Modeli mevcut model sahipliği kurallarına uygun olarak `kmpModels` içinde tut. Aynı modeli farklı
modüllerde çoğaltma.

Her bağımsız işlem kendi operation alanına sahip olabilir:

```kotlin
data class DiaryUiState(
    val dayQuote: String = "",
    val isDirty: Boolean = false,
    val quoteOperation: OperationState = OperationState.Idle,
)
```

Birbirinden bağımsız işlemleri tek global loading/error alanına bağlama.

## Tek Operator helper'ı

Tekrarlanan exception yönetimini `BaseFeatureOperator` içindeki tek bir fonksiyona taşı:

```kotlin
protected fun <T> executeOperation(
    onStart: State.() -> State,
    operation: suspend () -> T,
    onSuccess: State.(T) -> State,
    onError: State.(UiError) -> State,
) {
    updateState(onStart)

    launch {
        try {
            val result = operation()
            updateState { onSuccess(result) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            val uiError = errorMapper.map(error)
            updateState { onError(uiError) }
        }
    }
}
```

Örneği mevcut generic ve coroutine yapısına uyarlayabilirsin; davranışı koru:

- `onStart` çağrıdan önce state'i günceller.
- Başarı sonucu `onSuccess` ile ekrana özel state'e yazılır.
- Hata ortak mapper üzerinden `UiError` olur ve `onError` ile state'e yazılır.
- Cancellation tekrar fırlatılır.
- Helper snackbar, navigation veya feature'a özel karar vermemelidir.
- Helper iş mantığını ve ekrana özel state değişimini gizlememelidir.
- Reducer, middleware, store veya yeni bir framework oluşturma.

## Diary updateQuote refactor

Mevcut `updateQuote()` fonksiyonunda doğrudan `try/catch` bırakma. Şu kullanım biçimine geçir:

```kotlin
private fun updateQuote() {
    val currentState = state.value
    val quoteSnapshot = currentState.dayQuote
    val dateSnapshot = currentState.selectedDate

    if (currentState.quoteOperation is OperationState.Loading) return
    if (!currentState.isDirty) return
    if (quoteSnapshot.isBlank()) return

    executeOperation(
        onStart = {
            copy(quoteOperation = OperationState.Loading)
        },
        operation = {
            diaryEndpointOperation.updateQuote(
                date = dateSnapshot.toString(),
                quote = quoteSnapshot,
            )
        },
        onSuccess = { response ->
            if (selectedDate != dateSnapshot) {
                copy(quoteOperation = OperationState.Idle)
            } else {
                copy(
                    dayQuote = if (dayQuote == quoteSnapshot) response.quote else dayQuote,
                    isDirty = dayQuote != quoteSnapshot,
                    quoteOperation = OperationState.Idle,
                )
            }
        },
        onError = { error ->
            copy(
                isDirty = true,
                quoteOperation = OperationState.Error(error),
            )
        },
    )
}
```

Örneği mevcut gerçek state alanlarına göre uyarla; olmayan alanları körlemesine ekleme.

Korunması gereken davranışlar:

- Request başlamadan quote ve date snapshot alınmalı.
- Kullanıcı request sırasında yazmaya devam ederse yeni local değer response ile ezilmemeli.
- Kullanıcı request sırasında gün değiştirirse eski response yeni güne yazılmamalı.
- Hata durumunda local input ve dirty bilgisi korunmalı.
- Aynı işlem devam ederken ikinci kayıt çağrısı başlatılmamalı.

## Özel hata ve Effect

- Normal hata gösterimi `OperationState.Error(UiError)` üzerinden state-driven olmalıdır.
- Input altı hata, tam ekran hata veya retry görünümü yalnızca state'e bakılarak çizilmelidir.
- Snackbar, navigation ve dış uygulama açma gibi tek seferlik olaylar `Effect` olmalıdır.
- `executeOperation` her hatada otomatik snackbar göstermemelidir.
- Feature snackbar istiyorsa `onError` içinde açıkça kendi effect'ini üretebilir.
- Unauthorized gibi özel davranışlar `UiError.code` üzerinden Operator içinde ele alınabilir.
- Compose exception türü veya backend hata kodu yorumlamamalıdır.

## Modül sınırları

- `GenericResponse` ve response DTO'ları `kmpModels` içinde kalmalı.
- Ortak UI state/error modelleri `kmpModels` içinde kalmalı.
- Exception ve error mapping platformdan bağımsız uygun KMP domain katmanında kalmalı.
- Ktor/`HttpResponse` kodu network/data sahibi modülde kalmalı.
- Compose veya platform API'lerini domain/model katmanlarına ekleme.
- Legacy Android hata yapısını bu görevde topluca refactor etme.

## Doğrulama

En az şu durumları kontrol et:

1. Başarılı response modeli döner ve operation state `Idle` olur.
2. `status=false` güvenli bir `UiError` olarak state'e ulaşır.
3. `status=true/data=null` contract hatası olarak map edilir.
4. Network ve parse hataları teknik mesaj sızdırmadan state'e ulaşır.
5. Cancellation sarılmadan tekrar fırlatılır.
6. Hata olduğunda local input ve dirty state korunur.
7. Request sırasında yeni input girilirse yeni değer response ile ezilmez.
8. Request sırasında tarih değişirse eski response yeni tarihe yazılmaz.
9. Compose yalnızca state render eder ve action gönderir.

Etkilenen KMP Android ve Wasm hedeflerini derleyerek doğrula. İlgisiz dosyaları değiştirme.
