# Diary Alan Bazlı Tag Autocomplete — Supabase ve KMP UI

Mevcut Diary entry editor'a, seçilen alana bağlı basit ve kullanıcıya özel bir tag sistemi ekle.
Google/Supabase oturumunu, mevcut entry endpointlerini, Operator düzenini ve Compose saflık
kurallarını koru. İlgisiz Android legacy kodunu refactor etme.

## İlk sürüm ürün kararı

İlk sürümde bir entry en fazla **bir opsiyonel tag** taşısın.

- Kullanıcı önce bir alan seçer.
- Alan seçilince o kullanıcı ve o alan için daha önce kullanılan son 6 tag gösterilir.
- Kullanıcı önerilerden birini hemen seçebilir.
- Kullanıcı isterse arama/yazma alanına yeni bir tag yazabilir.
- Aynı isimde tag varsa mevcut tag seçilir; yoksa kullanıcı onayıyla backend'e kaydedilir.
- Oluşturulan tag kullanıcıya ait olur ve sonraki girişlerde kaybolmaz.
- Tag seçmeden entry kaydedilebilir veya dialog kapatılabilir.
- Entry listesinde alan label'ının hemen altında seçilmiş tag küçük bir chip/text olarak görünür.
- Alan seçilmemişse tag seçimi gösterilmez ve tag oluşturulamaz.
- Alan değiştirildiğinde önceki alana ait seçili tag temizlenir ve yeni alanın önerileri yüklenir.

Çoklu tag, global/public tag kataloğu ve tag yönetim ekranı bu ilk sürümün kapsamı dışındadır.

## Google login ve kullanıcı sahipliği

Mevcut `GoogleLoginOperation`, `KMPHeaderInterceptor` ve Supabase bearer token akışını yeniden
kullan.
Yeni login sistemi oluşturma.

Edge Function mevcut davranışla kullanıcıyı request header'daki Supabase access token üzerinden
çözmelidir:

```text
Authorization: Bearer <access-token>
-> supabase.auth.getUser(token)
-> userId = data.user.id
```

Kurallar:

- Client request body/query içinde `userId` gönderemez.
- Tag sorguları yalnız çözülen `userId` için çalışır.
- Bir kullanıcı başka kullanıcının tag'ini göremez, seçemez veya entry'sine bağlayamaz.
- Mevcut admin/fixed-user geliştirme modu varsa aynı sınır içinde çalışmaya devam edebilir; Google
  kullanıcısının ID'siyle karışmamalıdır.
- Android Google login henüz bağlı değilse bu görevde geniş auth refactoru yapma; mevcut platform
  desteğini bozma ve eksik platform bağlantısını sonuçta açıkça raporla.

## Supabase şeması

Mevcut migration dosyalarını değiştirme. Yeni ileri yönlü migration oluştur.

Yeni tablo:

```sql
create table public.diary_tags (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    area_id text not null,
    name text not null,
    normalized_name text not null,
    usage_count bigint not null default 0,
    last_used_at timestamptz null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
```

`area_id` yalnız mevcut Diary alanlarını kabul etsin:

```text
WORK, BODY, MIND, CHARACTER, LIFE
```

Tag adı kuralları:

- Baş ve son boşluklar temizlenmeli.
- Birden fazla ardışık boşluk tek boşluğa normalize edilmeli.
- Görünen `name` kullanıcının okunabilir yazımını korumalı.
- `normalized_name` karşılaştırma/unique amacıyla küçük harf normalize edilmiş değer olmalı.
- Boş tag kabul edilmemeli.
- Tag adı en fazla 40 karakter olmalı.
- Aynı kullanıcı ve aynı alan içinde büyük/küçük harf farkıyla duplicate tag oluşmamalı.
- Aynı tag adı farklı alanlarda kullanılabilir.
- Aynı tag adı farklı kullanıcılarda kullanılabilir.

Unique ve sorgu indexleri:

```text
unique(user_id, area_id, normalized_name)
index(user_id, area_id, last_used_at desc)
```

`diary_entries` tablosuna nullable ilişki ekle:

```text
tag_id uuid null references public.diary_tags(id) on delete set null
```

Entry tag'siz kalabilmelidir. RLS'yi `diary_tags` için aktif et. Mevcut Edge Function service-role
sınırını koru; service-role anahtarını client'a verme.

## Tag kullanım sırası

“İlk 6 tag” son kullanılan tag'ler olmalıdır:

```text
last_used_at desc nulls last
usage_count desc
name asc
limit 6
```

Bir tag entry'ye başarıyla bağlandığında:

```text
usage_count = usage_count + 1
last_used_at = server now
updated_at = server now
```

Entry aynı tag ile autosave edildiğinde usage_count'u her PATCH çağrısında tekrar artırma. Yalnız
tag
ilişkisi gerçekten null/farklı id değerinden bu tag'e geçtiğinde kullanım kaydı güncellensin.

Alan/tag sahipliği ve usage güncellemesi yarış koşulu üretmeyecek şekilde database function veya
transactional server akışıyla doğrulanmalıdır. Gereksiz geniş repository/manager katmanı oluşturma.

## Endpointler

Mevcut `kmpFunctions` içine küçük tag handler'ları ekle. Yeni Edge Function veya `/v2` oluşturma.

Yeni route'lar:

```text
GET  /diary/areas/{areaId}/tags?limit=6
GET  /diary/areas/{areaId}/tags?query=med&limit=6
POST /diary/tags
```

Mevcut entry route'ları aynı URL ile tag desteği kazanır:

```text
POST  /diary/entries
PATCH /diary/entries/{entryId}
GET   /diary/days/{date}/entries
```

### GET tag önerileri

- `areaId` geçerli beş alandan biri olmalı.
- Query yoksa kullanıcının o alandaki son kullanılan 6 tag'i dönmeli.
- `query` varsa normalize edilmiş isim üzerinden prefix/uygun case-insensitive arama yapmalı.
- Limit server tarafından güvenli aralıkta sınırlandırılmalı; UI varsayılanı 6 olmalı.
- Yalnız oturum kullanıcısının tag'leri dönmeli.
- Sonuç boşsa başarılı boş liste dönmeli.

### POST tag oluşturma

Request:

```json
{
  "areaId": "MIND",
  "name": "Meditasyon"
}
```

- Tag adını backend normalize etmeli.
- Aynı normalized tag mevcutsa duplicate oluşturmak yerine mevcut modeli başarıyla döndürmeli.
- Yeni tag oluşturulduğunda `usage_count = 0`, `last_used_at = null` olabilir; entry'ye bağlanınca
  kullanım bilgisi güncellenir.
- Response mevcut `GenericResponse<TagResponse>` sözleşmesini kullanmalı.

### Entry create/update

`EntryPostBody` ve PATCH request'i nullable `tagId` kabul etsin:

```json
{
  "areaId": "MIND",
  "text": "10 dakika nefes çalışması",
  "tagId": "tag-uuid"
}
```

Kurallar:

- `tagId = null` tagsiz entry anlamına gelir.
- Tag mevcut kullanıcıya ait olmalıdır.
- Tag'in `area_id` değeri entry'nin `areaId` değeriyle aynı olmalıdır.
- `tagId` varsa `areaId` zorunludur.
- Alan değiştirilirken eski tag yeni alanla uyumsuzsa request reddedilmek yerine client önce tag'i
  temizlemelidir; backend yine de güvenlik için uyumsuz isteği 400 döndürmelidir.
- GET entries response'u seçili tag modelini döndürmelidir; UI ayrıca tag endpoint'i çağırmadan
  listeyi çizebilmelidir.

Entry response içinde:

```json
{
  "id": "entry-uuid",
  "areaId": "MIND",
  "text": "10 dakika nefes çalışması",
  "tag": {
    "id": "tag-uuid",
    "name": "Meditasyon",
    "areaId": "MIND"
  }
}
```

Tag yoksa:

```json
"tag": null
```

Entry liste sorgusunda N+1 query oluşturma. `diary_entries.tag_id -> diary_tags` relation select
veya
tek uygun sorgu ile tag'i beraber getir.

## KMP modelleri

Mevcut `TagEntry` modelini duplicate oluşturmadan gerçek sözleşmeye uyumlu hale getir:

```kotlin
data class TagEntry(
    val id: String,
    val name: String,
    val areaId: String,
)
```

Wire modelleri uygun paketlerde oluştur:

```kotlin
@Serializable
data class TagPostBody(
    val areaId: String,
    val name: String,
)

@Serializable
data class TagResponse(
    val id: String,
    val name: String,
    val areaId: String,
)
```

- `EntryPostBody` nullable `tagId` alsın.
- `EntryResponse` nullable `tag: TagResponse?` alsın.
- `EntryEntity` nullable `tag: TagEntry?` alsın.
- Tag UI state'i wire response modeli olarak kullanma.

## EndpointStrings ve operation

Mevcut `kmpFunctionsBaseUrl` üzerinden URL üret:

```kotlin
fun diaryAreaTags(areaId: String, query: String? = null): String
const val diaryTags = "$kmpFunctionsBaseUrl/diary/tags"
```

Query parametresini string birleştirerek güvensiz yazma; Ktor request parameter API'siyle encode et.

`DiaryEndpointOperation` içine yalnız gerekli fonksiyonları ekle:

```kotlin
suspend fun getAreaTags(
    areaId: String,
    query: String? = null,
    limit: Int = 6,
): List<TagResponse>

suspend fun createTag(request: TagPostBody): TagResponse
```

Başarıda doğrudan model döndür. `Result`, `runCatching` veya endpoint içinde tekrar eden `try/catch`
ekleme; mevcut `bodyOrError()` ve `executeOperation` akışını kullan.

## Diary UI state

Mevcut `DiaryUiState` içine render-ready tag alanları ekle. İsimleri mevcut stile göre uyarlayarak:

```kotlin
val selectedTag: TagEntry? = null
val tagQuery: String = ""
val tagSuggestions: List<DiaryTagUiState> = emptyList()
val isTagInputVisible: Boolean = false
val canCreateTag: Boolean = false
val tagOperation: OperationState = OperationState.Idle
```

```kotlin
data class DiaryTagUiState(
    val id: String,
    val name: String,
    val displayLabel: String,
    val isSelected: Boolean,
)
```

Entry list item state'i tag'i hazır taşısın:

```kotlin
val tagLabel: String?
```

Compose `#` ekleme veya tag label mapping yapmamalı; Operator örneğin `#Meditasyon` şeklinde hazır
değer üretmeli.

Gerekli action'ları mevcut `DiaryAction` içine ekle:

```kotlin
data class TagQueryChanged(val value: String) : DiaryAction
data class TagSelected(val tagId: String) : DiaryAction
data object TagCleared : DiaryAction
data object CreateTagClicked : DiaryAction
```

## Operator davranışı

### Alan seçilince

1. `selectedArea` güncellenir.
2. Önceki `selectedTag`, query ve öneriler temizlenir.
3. Seçilen alan için son kullanılan 6 tag yüklenir.
4. Tag yükleme hatası entry editor'ü kapatmamalı ve entry kaydını engellememelidir.

### Kullanıcı yazınca

- `tagQuery` state hemen güncellenir.
- İlk yüklenen 6 öneri kısa query için local filtrelenebilir.
- Backend araması gerekiyorsa mevcut Operator scope içinde yaklaşık 250–350 ms debounce kullan.
- Yeni query geldiğinde önceki arama job'unu iptal et.
- Boş query son kullanılan 6 tag'i tekrar gösterir.
- Response geldiğinde hâlâ aynı alan/query aktif değilse eski sonucu state'e yazma.

### Tag seçilince

- `selectedTag` güncellenir.
- Query temizlenebilir veya seçili tag adı olarak gösterilebilir.
- Entry dirty/revision bilgisi güncellenir.
- Mevcut autosave varsa gerçek entry id oluştuktan sonra aynı save mekanizmasını kullanır.
- Tag seçimi dialogu kapatmaz.

### Yeni tag oluşturulunca

- Trim edilmiş query geçerli değilse işlem başlatma.
- Exact normalized eşleşme varsa POST yerine mevcut tag'i seç.
- Yoksa `createTag` çağrısı yap.
- Başarıda dönen tag'i seç, önerilere ekle ve entry'yi dirty yap.
- Hata halinde kullanıcının query metnini kaybetme.
- Aynı tag için paralel create çağrısı başlatma.

### Alan değişince

- Önceki tag kesinlikle temizlenmeli.
- Yeni alan önerileri yüklenmeli.
- Backend'e uyumsuz area/tag çifti gönderilmemeli.

### Editor açılınca/kapanınca

- Mevcut entry düzenleniyorsa response/entity içindeki tag seçili gelmeli.
- Tag seçilmeden Save veya dialog dismiss mümkün olmalı.
- Tag operation loading/error durumu ana entry save işlemini gereksiz yere engellememeli.
- Yeni entry açıldığında tag state temiz başlamalı.

## Compose UI

Tag bölümü yalnız `selectedArea != null` olduğunda, alan seçeneklerinin hemen altında gösterilsin.

Önerilen sade düzen:

```text
Etiket (isteğe bağlı)

[ Meditasyon ] [ Okuma ] [ Araştırma ] [ Odak ] [ Fikir ] [ Nefes ]

[ Etiket ara veya oluştur...                         ]
```

Kurallar:

- İlk 6 öneriyi küçük `AssistChip`/`FilterChip` benzeri bileşenlerle göster.
- Seçili tag görsel olarak belirgin olsun.
- Input placeholder: `Etiket ara veya oluştur`.
- Query mevcut tag ile exact eşleşmiyorsa küçük bir aksiyon göster:

```text
“Meditasyon” etiketini oluştur
```

- Create aksiyonu yalnız Operator'ın `canCreateTag` state'ine göre görünür/enabled olsun.
- Tag seçmeden entry Save/Dismiss akışı çalışmaya devam etsin.
- Alan temizlenirse tag bölümü gizlensin ve seçili tag temizlensin.
- Compose içinde filtreleme, normalization, duplicate kontrolü, network veya save kararı yazma.
- Feature coroutine veya tag araması için `LaunchedEffect` kullanma.
- Screen yalnız state çizer ve Action gönderir.

## Entry listesi

Entry kartında mevcut alan label'ının hemen altında tag varsa göster:

```text
🧭 Karakter
#Meditasyon
10 dakika nefes çalışması
```

- Tag yoksa boş alan veya placeholder gösterme.
- Tag küçük, ikincil renkte ve tek satır olmalı.
- Tag'e listeden tıklamak bu görevde filtreleme veya navigation başlatmamalı.
- Timer ve mevcut entry action düzenini bozma.

## Hata ve operation state

- Tag önerisi yükleme, tag oluşturma ve entry save operation'larını tek global loading alanına
  bağlama.
- Tag hatası `UiError`/`OperationState` üzerinden state-driven gösterilmeli.
- Teknik backend mesajı Compose'a sızdırılmamalı.
- 401/403 mevcut Google session expiry akışına gitmeli.
- Tag önerileri yüklenemezse kullanıcı tagsiz entry kaydedebilmeli.

## Supabase deploy

Kod tamamlandıktan sonra:

1. Yeni `diary_tags` migration'ını ve `diary_entries.tag_id` ilişkisini doğrula.
2. Bağlı project ref'i mevcut config üzerinden doğrula.
3. Migration'ı uygula:

```bash
supabase db push
```

4. Mevcut function'ı aynı ad ve base URL ile deploy et:

```bash
supabase functions deploy kmpFunctions
```

5. `/v2`, yeni Edge Function veya yeni base URL oluşturma.
6. Google kullanıcı token'ı ile user isolation doğrulaması yap; token veya service-role secret'ını
   loglama/raporlama.
7. Deploy yetkisi veya oturumu yoksa deploy edilmiş gibi davranma; kalan komutları açıkça raporla.

## Kapsam dışı

- Entry başına çoklu tag
- Tag silme/yeniden adlandırma yönetim ekranı
- Global veya kullanıcılar arası ortak tag
- Tag renkleri ve özel ikonlar
- Tag'e göre liste filtreleme
- Tag istatistik ekranı
- Android Google login için geniş platform refactoru
- Quote veya timer endpointlerini yeniden tasarlama
- Legacy Android feature migrasyonu

## Doğrulama

Yeni KMP test dosyası/dependency ekleme; mevcut proje politikasına göre derleme ve davranış kontrolü
yap.

En az şu durumları doğrula:

1. Google kullanıcısı yalnız kendi tag'lerini görür.
2. Alan seçilince o alanın son kullanılan en fazla 6 tag'i gelir.
3. Alan değiştirilince eski tag seçimi ve önerileri temizlenir.
4. Existing tag seçimi yeni tag oluşturmaz.
5. Büyük/küçük harf farkıyla duplicate tag oluşmaz.
6. Elle girilen yeni tag backend'de kalıcı olur ve sonraki kullanımda önerilir.
7. Tagsiz entry create/update/dismiss çalışır.
8. Başka kullanıcıya veya başka alana ait tag entry'ye bağlanamaz.
9. Autosave aynı tag kullanım sayısını tekrar tekrar artırmaz.
10. GET entries tag bilgisini N+1 olmadan döndürür.
11. Entry listesinde tag alanın altında görünür; tagsiz entry boş placeholder göstermez.
12. 401 mevcut Google session expiry davranışını korur.
13. Mevcut quote, timer ve entry endpoint URL'leri bozulmaz.
14. Etkilenen KMP Android ve Wasm hedefleri derlenir.

Sonuç raporunda migration'ı, yeni route'ları, kullanıcı izolasyonunu, UI davranışını, deploy
sonucunu
ve Android Google login desteğinin mevcut durumunu kısa şekilde belirt.
