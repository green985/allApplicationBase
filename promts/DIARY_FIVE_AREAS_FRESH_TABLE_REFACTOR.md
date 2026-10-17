# Diary: 5 Alan ile Temiz Tablo ve Mevcut Endpoint Refactoru

Mevcut Diary entry yapısını beş alanlı yeni ürün modeline göre temiz biçimde yeniden oluştur.

Bu proje için eski `diary_entries` tablosundaki verilere, eski tablo şemasına ve eski alan
değerlerine
ihtiyaç yoktur. Veri taşıma, backward compatibility, v2 tablo veya v2 endpoint oluşturma.

## Kesin kararlar

- Endpoint URL'leri değişmeyecek.
- `/v2`, `diary_entries_v2` veya paralel API oluşturulmayacak.
- Eski `diary_entries` tablosu yeni ileri yönlü migration içinde kaldırılıp aynı isimle yeniden
  oluşturulacak.
- Eski entry verileri taşınmayacak.
- Quote tablosu ve quote endpointleri değiştirilmeyecek.
- KMP modelleri, Operator ve UI yeni beş alanlı sözleşmeye geçirilecek.
- İlgisiz Android legacy modülleri refactor edilmeyecek.

## Beş alan

UI ve ürün anlamları:

```text
WORK      -> İnşa     -> İş, projeler, üretim, maddi hedefler
BODY      -> Beden    -> Spor, beslenme, uyku, fiziksel sağlık
MIND      -> Zihin    -> Öğrenme, düşünme, araştırma, entelektüel gelişim
CHARACTER -> Karakter -> Erdem, disiplin, meditasyon, duygusal olgunluk
LIFE      -> Yaşam    -> İnsanlar, ilişkiler, eğlence, seyahat, deneyimler
```

Kurallar:

- Database wire değeri `WORK` olarak kalmalı; UI'da `İnşa` gösterilmeli.
- `IDLE`, `HEALTH` ve `PEOPLE` tamamen kaldırılmalı.
- Alan seçilmemiş olma durumu yalnız `areaId = null` ile temsil edilmeli.
- Alan seçimi opsiyonel olmalı.
- Yalnız not, yalnız alan, yalnız timer veya bunların birleşimiyle entry oluşturulabilmeli.
- Boş ve anlamsız entry oluşturulmamalı; en az alan, non-blank text, timer süresi veya `Yapıldı`
  seçimi bulunmalı.

## Yeni migration: tabloyu temiz oluştur

Mevcut migration dosyalarını düzenleme. Yeni tarihli ileri yönlü bir migration oluştur.

Bu migration'ın eski entry verilerini sileceği bilinçli olarak kabul edilmiştir. Yalnız
`public.diary_entries` ve ona doğrudan bağlı eski trigger/RPC yapılarını hedefle. Quote veya başka
tabloları silme.

Önerilen sıra:

1. Eski timer RPC fonksiyonlarını güvenli biçimde kaldır.
2. Eski `diary_entries` tablosunu `cascade` etkisini kontrol ederek kaldır.
3. Aynı isimle yeni `public.diary_entries` tablosunu oluştur.
4. Index, constraint, RLS ve yeni timer RPC fonksiyonlarını oluştur.
5. Function yetkilerini yalnız mevcut güvenlik modeline uygun role ver.

Yeni minimum tablo:

```sql
create table public.diary_entries (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    entry_date date not null,
    area_id text null,
    text text not null default '',
    duration_seconds bigint null,
    timer_started_at timestamptz null,
    timer_ends_at timestamptz null,
    timer_cancelled_at timestamptz null,
    completed_at timestamptz null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
```

Constraint'ler:

```text
area_id null veya WORK/BODY/MIND/CHARACTER/LIFE
text uzunluğu en fazla 3000
duration_seconds null veya 1..86400
timer_ends_at >= timer_started_at
timer_started_at varsa timer_ends_at ve duration_seconds bulunmalı
timer_cancelled_at varsa timer_started_at bulunmalı
```

Süre üst sınırı mevcut ürün kararı farklı değilse 24 saat/86400 saniye olsun. Database, Edge
Function ve KMP validation aynı üst sınırı kullanmalı.

Gün sorgusu için index oluştur:

```sql
create index diary_entries_user_date_index
    on public.diary_entries (user_id, entry_date, created_at desc);
```

RLS'yi aktif et. Mevcut Edge Function service-role kullanıyorsa aynı güvenlik sınırını koru;
client'a
service-role anahtarı verme.

## Timer database sorumluluğu

Backend saniyelik sayaç çalıştırmamalı ve status kolonu saklamamalıdır. Yalnız şu gerçekleri saklar:

```text
duration_seconds
timer_started_at
timer_ends_at
timer_cancelled_at
completed_at
```

UI/domain status'u Operator tarafından türetilir:

```text
timerCancelledAt != null                       -> CANCELLED
completedAt != null                            -> FINISHED
timerEndsAt != null && timerEndsAt <= now      -> FINISHED
timerStartedAt != null && timerEndsAt != null  -> RUNNING
durationSeconds != null                        -> PENDING
aksi                                              -> NONE
```

`Yapıldı` seçeneğinde backend `completed_at` değerini kendi saatiyle üretir. Client timer
zamanlarını
doğrudan gönderemez.

Yeni tabloya bağlı RPC fonksiyonlarını yeniden oluştur:

```text
start_diary_entry_timer(entryId, userId)
cancel_diary_entry_timer(entryId, userId)
```

Start işlemi:

- Entry ve kullanıcıyı doğrulasın.
- Pozitif duration zorunlu olsun.
- Server zamanı ile `timer_started_at` üretsin.
- `timer_ends_at = timer_started_at + duration_seconds` hesaplasın.
- Aynı kullanıcı için aynı anda yalnız bir aktif timer başlatılmasına izin versin.
- Aynı entry ikinci kez başlatılamasın.

Cancel işlemi:

- Yalnız aktif timer'ı iptal etsin.
- `timer_cancelled_at` değerini server zamanı ile yazsın.
- Tekrarlanan cancel çağrısı idempotent olsun.

Timer bitiş endpoint'i veya backend timer job'u oluşturma. Bitiş `timerEndsAt <= now` üzerinden
türetilir.

## Mevcut endpointler korunacak

Aşağıdaki URL ve methodlar değişmeyecek:

```text
GET   /diary/days/{date}/entries
POST  /diary/entries
PATCH /diary/entries/{entryId}
POST  /diary/entries/{entryId}/timer/start
POST  /diary/entries/{entryId}/timer/cancel
```

Yeni path, yeni Edge Function veya `/v2` oluşturma. Mevcut `kmpFunctions` içinde aynı route
handler'larını yeni tablo sözleşmesine göre güncelle.

Edge Function değişiklikleri:

- `validAreas` yalnız `WORK`, `BODY`, `MIND`, `CHARACTER`, `LIFE` içersin.
- `IDLE`, `HEALTH` ve `PEOPLE` kabul edilmesin ve normalize edilmesin.
- Eski client uyumluluğu yazma; eski client desteği gerekmiyor.
- `areaId = null` geçerli olsun.
- Select/map işlemleri yeni tablonun bütün alanlarını kullansın.
- Client'ın `timerStartedAt`, `timerEndsAt`, `timerCancelledAt`, `completedAt` göndermesini reddet.
- `markAsCompleted = true` için `completed_at` server zamanı ile oluşturulsun.
- Validation ve database hataları güvenli `GenericResponse<T>` ile dönsün.
- Raw SQL/Supabase hata mesajını kullanıcıya sızdırma.
- Quote route ve handler'larına dokunma.
- Diary archive endpoint'i entry export ediyorsa artık yeni, boş başlayan `diary_entries` tablosunu
  kullansın; response sözleşmesini değiştirme.

## KMP modelleri

`AreaEntry` şu hale gelsin:

```kotlin
enum class AreaEntry {
    WORK,
    BODY,
    MIND,
    CHARACTER,
    LIFE,
}
```

- `IDLE`, `HEALTH` ve `PEOPLE` kaldırılmalı.
- `EntryEntity.areaId`, `EntryPostBody.areaId`, patch body ve `EntryResponse.areaId` nullable
  kalmalı.
- Duplicate v2 model oluşturma.
- Kullanılmayan eski timer alanları (`timerType`, `startedAt`, `endedAt` gibi yeni sözleşmeyle
  çakışan alanlar) gerçekten kullanılmıyorsa kaldır; önce referanslarını kontrol et.
- Tek doğruluk kaynağı şu alanlar olsun:

```text
durationSeconds
timerStartedAt
timerEndsAt
timerCancelledAt
completedAt
```

## Operator

Alan seçeneklerini `AreaEntry.values()` üzerinden rastgele sıra ile üretme. Sıralamayı açık tut:

```kotlin
private val diaryAreas = listOf(
    AreaEntry.WORK,
    AreaEntry.BODY,
    AreaEntry.MIND,
    AreaEntry.CHARACTER,
    AreaEntry.LIFE,
)
```

Label eşlemesi:

```text
WORK      -> İnşa
BODY      -> Beden
MIND      -> Zihin
CHARACTER -> Karakter
LIFE      -> Yaşam
null      -> alan etiketi gösterme
```

- `areaOptions` ve `areaOptionRows` Operator tarafından hazırlanmalı.
- UI için satırlar `3 + 2` şeklinde hazırlanmalı.
- Alan seçimini kaldırmak için `AreaCleared` action'ı ekle veya seçili alana tekrar basıldığında
  Operator state'ini `null` yap; karar Compose içinde verilmesin.
- Eski ID'yi enum'a çeviren kod `firstOrNull` ile güvenli kalmalı.
- Entry save validation en az text, area, duration veya tamamlandı seçiminden birini kabul etmeli.
- Timer status ve countdown mevcut zaman alanlarından Operator'da türetilmeli.

## Compose UI

Entry editor başlığı:

```text
Alan (isteğe bağlı)
```

Beş alanı dengeli göster:

```text
[ İnşa ] [ Beden ] [ Zihin ]
[ Karakter ] [ Yaşam ]
```

- `Alan yok` veya `IDLE` butonu ekleme; seçim yapılmamış state `null` olarak kalmalı.
- Alan temizlemek için küçük `Temizle` aksiyonu gösterilebilir.
- Uzun kapsam açıklamalarını ana dialoga doldurma.
- İstenirse bilgi alanında kısa açıklamalar kullanılabilir:

```text
İnşa: İş, proje ve üretim
Beden: Spor, uyku ve sağlık
Zihin: Öğrenme ve düşünme
Karakter: Disiplin ve içsel gelişim
Yaşam: İlişkiler ve deneyimler
```

- Compose yalnız hazır state'i çizsin ve Action göndersin.
- Validation, enum dönüşümü, filtreleme, chunking veya save kararı Compose içinde olmasın.
- Preview'ları yeni beş alanla güncelle.

## Temizlenecek referanslar

Projede şu değerleri ara ve yeni Diary akışından kaldır:

```text
AreaEntry.IDLE
AreaEntry.HEALTH
AreaEntry.PEOPLE
idleArea
healthArea
peopleArea
```

`EntryDummyData`, preview, StringKeys, `areaLabel`, test/dummy JSON ve Supabase validation
listelerini
güncelle. İlgisiz başka feature aynı StringKeys değerini kullanıyorsa körlemesine silme.

## Deploy

Kod tamamlandıktan sonra:

1. Migration'ın yalnız `diary_entries` tablosunu yeniden oluşturduğunu doğrula.
2. Kullanıcı eski entry verilerinin silineceğini açıkça kabul ettiği için veri taşıma yapma.
3. Bağlı Supabase project ref'i mevcut config üzerinden doğrula.
4. Migration'ı uygula:

```bash
supabase db push
```

5. Mevcut function'ı aynı adla deploy et:

```bash
supabase functions deploy kmpFunctions
```

6. Base URL ve endpoint URL'lerini değiştirme:

```text
https://uduwhuvgdcacvdhzheyi.supabase.co/functions/v1/kmpFunctions
```

7. `EndpointStrings` içinde `/v2` veya yeni base URL ekleme.
8. Secret/service-role key'i kaynak koda veya loglara yazma.
9. Deploy yetkisi/oturumu yoksa deploy edilmiş gibi davranma; kalan komutları raporla.

## Doğrulama

En az şunları doğrula:

1. Yeni `diary_entries` tablosu boştur ve eski veri taşınmamıştır.
2. Yalnız `WORK`, `BODY`, `MIND`, `CHARACTER`, `LIFE` veya null kabul edilir.
3. `IDLE`, `HEALTH` ve `PEOPLE` 400 döndürür.
4. Yalnız text ile entry oluşturulur.
5. Yalnız alan ile entry oluşturulur.
6. Yalnız timer süresi ile PENDING entry oluşturulur.
7. `Yapıldı` seçimi server `completedAt` değeri ile entry oluşturur.
8. Start server zamanlarını üretir ve aynı kullanıcı için ikinci aktif timerı engeller.
9. Cancel idempotent çalışır.
10. GET entries yalnız yeni tablodaki kayıtları döndürür.
11. Mevcut endpoint URL'leri değişmemiştir.
12. Quote GET/PUT davranışı bozulmamıştır.
13. UI beş alanı `3 + 2` düzeninde gösterir ve alan opsiyoneldir.
14. Android ve Wasm KMP hedefleri derlenir.

Sonuç raporunda oluşturulan migration'ı, silinen eski veri kapsamını, değişmeden kalan endpoint
URL'lerini, deploy sonucunu ve doğrulanan senaryoları kısa şekilde belirt.
