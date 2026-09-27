# Diary Entry Create Endpoint ve KMP Entegrasyonu

Mevcut projeyi inceleyerek yeni diary entry oluşturma akışını uçtan uca, küçük ve genişletilebilir
bir ilk sürüm olarak tamamla. İlgisiz kodu ve legacy Android tarafını refactor etme.

## Amaç

İlk sürümde yalnızca yeni diary entry oluşturmayı tamamla:

```text
Diary Action -> DiaryOperator -> POST endpoint -> Supabase -> EntryResponse -> DiaryUiState
```

Bu görevde listeleme, silme, tag sistemi ve timer özelliğini uygulama. Ancak oluşturulan tablo daha
sonra PATCH ve günlük listeleme endpointlerinin eklenmesini engellememelidir.

## Önce mevcut yapıyı doğrula

Özellikle şu dosya ve modelleri yeniden kullan:

- `EntryPostBody`
- `EntryResponse`
- `EntryEntity`
- `AreaEntry`
- `DiaryEndpointOperation`
- `DiaryOperator`
- `GenericResponse<T>` ve `bodyOrError<T>()`
- `OperationState`, `UiError` ve `executeOperation`
- `supabase/functions/kmpFunctions/index.ts`

Aynı amaçla duplicate model, repository, use case veya yeni mimari katman oluşturma.

## İlk sürüm veri sözleşmesi

Create endpoint için gerekli minimum alanlar:

```json
{
  "areaId": "MIND",
  "text": "Bugün için küçük bir başlangıç yaptım.",
  "entryDate": "2026-09-27"
}
```

Kararlar:

- `id`, `userId`, `createdAt` ve `updatedAt` backend tarafından oluşturulmalıdır.
- Ekranda seçilen gün yalnızca tarih olduğu için request alanı `entryDate` olmalıdır.
- Mevcut `occurredAt` alanına yalnız `YYYY-MM-DD` gönderme. `occurredAt` kullanılacaksa tam ISO-8601
  timestamp olmalıdır; bu ilk sürümde tarih ile timestamp kavramını karıştırma.
- `areaId`, mevcut `AreaEntry` değerlerinden biri olmalıdır.
- `text` trim edilmeli, boş olamamalı ve makul bir maksimum uzunlukla doğrulanmalıdır.
- `createdBy` client request'inden alınmamalıdır; gerekiyorsa backend kullanıcı bilgisinden üret.

Başarılı cevap mevcut `GenericResponse<EntryResponse>` sözleşmesine uymalıdır:

```json
{
  "data": {
    "id": "6b27d37a-b194-4f91-b624-657b9f4ec691",
    "areaId": "MIND",
    "text": "Bugün için küçük bir başlangıç yaptım.",
    "entryDate": "2026-09-27",
    "createdBy": null,
    "createdAt": "2026-09-27T14:18:53Z",
    "updatedAt": "2026-09-27T14:18:53Z"
  },
  "message": "Diary kaydı başarıyla oluşturuldu.",
  "status": true
}
```

`EntryPostBody`, `EntryResponse` ve `EntryEntity` içindeki tarih alanlarını bu açık sözleşmeyle
uyumlu
hale getir. Geniş kapsamlı model temizliği yapma.

## Database migration

Yeni, ileri yönlü bir Supabase migration oluştur. Mevcut migration'ı sonradan değiştirme.

Minimum tablo:

```text
diary_entries
- id uuid primary key
- user_id uuid not null
- entry_date date not null
- area_id text not null
- text text not null
- created_at timestamptz not null
- updated_at timestamptz not null
```

Kurallar:

- Gün bazlı sorgu için `(user_id, entry_date)` index'i ekle.
- `area_id` için mevcut enum değerleriyle uyumlu bir check constraint ekle veya projedeki mevcut
  Supabase yaklaşımı farklıysa aynı yaklaşımı kullan.
- Boş metni engelleyen temel constraint ekle.
- RLS'yi aktif et.
- Mevcut quote migration'ını veya tablosunu değiştirme.
- `note`, duration/timer alanları ve tag ilişki tablolarını bu ilk migration'a ekleme; gerçek
  özellik
  geldiğinde ayrı migration ile ekle.

Kimlik doğrulama henüz gerçek kullanıcıya bağlanmamışsa mevcut `fixedUserId` davranışını yalnız
geçici uyumluluk için koruyabilirsin. Bunu genişletme ve client'tan `userId` kabul etme. Kodda kısa
bir TODO ile bunun JWT `auth.uid()` tabanlı kullanıcıya geçirilmesi gerektiğini belirt.

## Supabase Edge Function route

Mevcut tek `kmpFunctions` function'ı içinde route ekle:

```text
POST /diary/entries
```

Supabase dış URL biçimi mevcut quote endpoint'iyle aynı function prefix'ini kullanmalıdır:

```text
/functions/v1/kmpFunctions/diary/entries
```

Mevcut quote GET/PUT route'larını bozma.

POST davranışı:

1. JSON body'yi güvenli parse et.
2. `areaId`, `text` ve `entryDate` alanlarını doğrula.
3. Kaydı `diary_entries` tablosuna ekle.
4. Eklenen satırı seç ve camelCase `EntryResponse` biçimine map et.
5. Başarı ve hata cevaplarını mevcut `GenericResponse<T>` helper'ıyla üret.
6. Validation için 400, bulunamayan route için 404, desteklenmeyen method için 405 ve beklenmeyen
   backend hatası için güvenli 500 cevabı kullan.
7. Ham database hata ayrıntısını son kullanıcı mesajı olarak sızdırma.
8. CORS `Access-Control-Allow-Methods` listesine `POST` ekle.

Routing yalnız methoda göre karar vermemeli; path ve method birlikte kontrol edilmelidir.

## KMP endpoint düzeltmesi

`EndpointStrings.diaryEntries` şu an function prefix'i bakımından quote endpoint'iyle tutarlı
olmayabilir. Create çağrısını gerçek route'a yönlendir:

```text
.../functions/v1/kmpFunctions/diary/entries
```

Base URL tekrarını mümkün olduğunca küçük bir düzenlemeyle azalt. Diğer endpointleri veya login
akışını bozma.

`DiaryEndpointOperation.createEntry(request): EntryResponse` başarıda doğrudan model döndürmeye
devam etsin. `Result`, `runCatching` veya endpoint içinde tekrar eden `try/catch` ekleme.

## Operator ve state entegrasyonu

Mevcut `executeOperation` yapısını kullan. Compose ekranına network veya hata mantığı ekleme.

Mevcut create akışındaki geçici id problemini düzelt:

- Yeni entry için `entries.size.toString()` gibi local sahte id üretme.
- POST başarılı olduğunda backend'in döndürdüğü gerçek `response.id` değerini kullan.
- Başarılı response'u `EntryEntity` değerine dönüştür ve state listesini bununla güncelle.
- İlk başarılı create sonrasında `editingEntryId = response.id` yap; aynı editor açıkken sonraki
  autosave işlemleri yeni POST yerine PATCH kullanabilsin.
- Request sürerken kullanıcı metni değiştirmişse yeni local metni response ile ezme.
- Request sürerken seçilen tarih veya düzenlenen entry değişmişse eski response'u yanlış ekrana
  uygulama.
- Başarısız create işleminde local input, seçili area ve dirty bilgisi korunmalı.
- Aynı create devam ederken ikinci POST başlatılmamalı.
- Manuel Save ve debounce aynı kayıt mekanizmasını kullanmalı.

Basitlik için ilk sürümde optimistic olarak listeye sahte entry ekleme. Loading state göster, POST
başarılı olduğunda gerçek server entry'sini listeye ekle. Bu, rollback ve geçici id karmaşıklığını
önler.

Network sonucu `EntryResponse -> EntryEntity` dönüşümünü küçük, platformdan bağımsız bir fonksiyonla
yap. Sırf bunun için yeni mapper katmanı veya sınıf oluşturma.

## Autosave ve duplicate kayıt güvenliği

Mevcut 700 ms debounce davranışını koru. İlk create çağrısı başarıyla id dönmeden yeni POST
başlatılmamalıdır.

Bu görevde kapsamlı offline queue oluşturma. Ancak timeout sonrası retry'nin duplicate POST üretme
riskini analiz et ve sonuç raporunda belirt. Backend/client tarafında zaten kullanılabilir bir UUID
üretim standardı varsa küçük bir `clientRequestId` idempotency anahtarı ekleyebilirsin; yoksa yalnız
bu görev için yeni UUID kütüphanesi ekleme.

## Kapsam dışı

- Günlük entry listesini getiren GET endpoint
- Entry PATCH implementasyonunu yeniden tasarlama
- Delete endpoint
- Tag tabloları ve ilişkileri
- Timer/countdown/stopwatch davranışı
- Offline-first queue ve sync engine
- Gerçek auth migrasyonu
- Legacy Android refactoru
- UI tasarım değişikliği

## Doğrulama

En az şu senaryoları doğrula:

1. Geçerli request tek bir kayıt oluşturur ve gerçek UUID döndürür.
2. Boş text, geçersiz area ve geçersiz tarih 400 döndürür.
3. Response `GenericResponse<EntryResponse>` olarak parse edilir.
4. Başarılı create sonrasında state gerçek server id'sini içerir.
5. Aynı editor üzerindeki sonraki kayıt create değil update yoluna hazırlanır.
6. Request sırasında yeni input girilirse yeni local değer korunur ve dirty kalır.
7. Hata halinde editor içeriği silinmez ve retry mümkündür.
8. Quote GET/PUT route'ları çalışmaya devam eder.
9. Etkilenen KMP Android ve Wasm hedefleri derlenir.

Sonuçta değişen dosyaları, migration kararını ve ileride GET/PATCH/auth için kalan işleri kısa
şekilde
raporla.
