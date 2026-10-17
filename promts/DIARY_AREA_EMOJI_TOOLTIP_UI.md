# Diary Alan Emoji ve Hover Tooltip UI

Mevcut Diary entry editor içindeki `Alan (isteğe bağlı)` bölümünü küçük emoji ikonları ve açıklama
tooltip'leriyle geliştir. Mevcut ekran düzenini, seçim davranışını ve `3 + 2` segmented button
yerleşimini koru. İlgisiz ekranlara, backend'e, Supabase'e veya timer davranışına dokunma.

## Hedef görünüm

Mevcut alan butonları şu içerikle gösterilsin:

```text
[ 🏗️ İnşa ] [ 💪 Beden ] [ 🧠 Zihin ]
[ 🧭 Karakter ] [ 🌍 Yaşam ]
```

Emoji küçük olmalı ve mevcut label'ın hemen solunda yer almalı. Buton yüksekliğini gereksiz
büyütme, mevcut segmented button tasarımını bozma.

## Alan içerikleri

Tam eşleme:

```text
WORK
emoji: 🏗️
label: İnşa
description: Bir şey üretmek, proje ve hedef geliştirmek

BODY
emoji: 💪
label: Beden
description: Spor, fiziksel sağlık ve bakım

MIND
emoji: 🧠
label: Zihin
description: Öğrenme, düşünme ve araştırma

CHARACTER
emoji: 🧭
label: Karakter
description: Değerler, disiplin ve kişisel yön

LIFE
emoji: 🌍
label: Yaşam
description: İnsanlar, seyahat ve deneyimler
```

## State hazırlığı

Compose içinde `when(area)` ile emoji veya açıklama üretme. Mevcut `DiaryAreaUiState` modelini
render-ready olacak şekilde genişlet:

```kotlin
data class DiaryAreaUiState(
    val area: AreaEntry,
    val label: String,
    val emoji: String,
    val description: String,
)
```

Emoji, label ve description değerlerini `DiaryOperator` içindeki mevcut `areaOptions()` hazırlığında
oluştur. Mevcut `StringKeys` yaklaşımını kullanılıyorsa label ve açıklamaları orada tut; aynı metni
farklı dosyalarda çoğaltma.

Alan sırası değişmemeli:

```text
WORK, BODY, MIND, CHARACTER, LIFE
```

Operator mevcut UI state için satırları hazırlamaya devam etmeli:

```text
ilk satır: WORK, BODY, MIND
ikinci satır: CHARACTER, LIFE
```

Chunking, sıralama veya mapping işlemini Compose'a taşıma.

## Tooltip davranışı

Her alan butonunu Compose Multiplatform/Material 3 uyumlu tooltip bileşeniyle sar.

Beklenen davranış:

- Desktop ve Web/Wasm: Mouse ilgili alan üzerinde kısa süre beklediğinde açıklama görünmeli.
- Android/dokunmatik: Long-press veya desteklenen erişilebilir tooltip davranışıyla açıklama
  açılabilmeli.
- Klavye/focus desteği platform bileşeni tarafından sunuluyorsa korunmalı.
- Tooltip yalnız açıklama metnini göstermeli; başlık veya ikinci bir ikon ekleme.
- Tooltip kısa, tek veya en fazla iki satırlık olmalı.
- Mouse alandan ayrılınca veya kullanıcı başka yere tıklayınca kapanmalı.
- Aynı anda yalnız bir tooltip görünmeli.
- Tooltip açılması alan seçimini değiştirmemeli ve `DiaryAction.AreaSelected` göndermemeli.
- Normal click/tap mevcut alan seçim Action'ını göndermeye devam etmeli.

Öncelikle projede kullanılan Compose Material 3 sürümünün ortak source set'te desteklediği
`TooltipBox`, `PlainTooltip`, `TooltipDefaults` ve `rememberTooltipState` API'lerini kullan.
Desteklenen
API imzasını mevcut dependency sürümünden doğrula; Android-only tooltip veya Swing/AWT API ekleme.

Örnek yönelim; birebir kopyalamadan mevcut sürüme uyarla:

```kotlin
TooltipBox(
    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
    tooltip = {
        PlainTooltip {
            Text(areaOption.description)
        }
    },
    state = rememberTooltipState(),
) {
    SegmentedButton(
        selected = selectedArea == areaOption.area,
        onClick = { onAreaSelected(areaOption.area) },
        // mevcut shape ve modifier değerlerini koru
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = areaOption.emoji,
                fontSize = 16.sp,
            )
            Text(
                text = areaOption.label,
                maxLines = 1,
            )
        }
    }
}
```

Tooltip API'si mevcut Compose Multiplatform sürümünde hover'ı otomatik sağlamıyorsa yalnız ortak
Compose pointer/focus API'leriyle küçük bir wrapper oluştur. Yeni dependency ekleme ve platforma
özel
duplicate ekran oluşturma. Tooltip için feature/business state'i Operator'a ekleme; tooltip'in açık
olup olmaması yalnız görsel, ephemeral UI state olabilir.

## Görsel kurallar

- Emoji font boyutu yaklaşık `16.sp`; label mevcut typography stilini kullansın.
- Emoji ile label arasında yaklaşık `4.dp` boşluk olsun.
- Emoji butonun ortalanmasını veya seçim check ikonunu bozmamalı.
- Dar genişlikte label tek satırda kalsın; butonların eşit ağırlığı korunsun.
- Tooltip mevcut açık/koyu tema renklerini Material theme üzerinden kullansın.
- Sabit beyaz/siyah tooltip rengi yazma.
- Network resmi, bitmap, drawable veya yeni icon asset'i ekleme; bu adımda Unicode emoji kullan.
- Tooltip bilgisi yardımcı olmalı; alanın anlaşılması yalnız tooltip'e bağlı olmamalı. Label her
  zaman
  görünür kalmalı.

## Compose saflığı

- Compose yalnız `DiaryAreaUiState` içindeki `emoji`, `label` ve `description` değerlerini çizsin.
- Compose enum eşlemesi, kapsam kararı veya feature state dönüşümü yapmasın.
- Tooltip açılması için Operator Action üretme.
- Tooltip için feature coroutine veya `LaunchedEffect` kullanma.
- Mevcut area selection, clear selection ve save davranışlarını değiştirme.

## Preview

Diary preview verisinde beş alan seçeneğini emoji ve description değerleriyle hazırla. Mümkünse UI
helper'ı için küçük bir preview ekle; mevcut dosyanın preview'larını dosya sonunda tut.

Preview veya doğrulamada en az şu görünümü kontrol et:

```text
🏗️ İnşa    💪 Beden    🧠 Zihin
🧭 Karakter             🌍 Yaşam
```

## Doğrulama

En az şunları kontrol et:

1. Beş alan doğru emoji ve label ile görünür.
2. Satır düzeni `3 + 2` olarak korunur.
3. Desktop/Web hover ile doğru açıklama açılır.
4. Android long-press ile açıklama erişilebilir olur.
5. Tooltip açılması alan seçimini tetiklemez.
6. Click/tap mevcut alan seçim davranışını korur.
7. Seçili alan görünümü ve check işareti bozulmaz.
8. Dar ekranda label taşması veya buton yüksekliğinde aşırı büyüme olmaz.
9. Compose içinde alan eşleme veya business logic bulunmaz.
10. Etkilenen KMP Android ve Wasm hedefleri derlenir.

Sonuçta değişen model, Operator ve UI dosyalarını; kullanılan tooltip API'sini ve Android/Wasm
doğrulama sonucunu kısa şekilde raporla.
