# Diary Debounced Autosave

Diary input alanları için yeniden kullanılabilir bir autosave yapısı oluştur. Compose ekranına logic
ekleme; ekran yalnızca güncel değeri typed Action olarak Operator’a göndersin.

Beklenen akış:

```text
Input değişir
→ Action Operator’a gelir
→ UiState hemen güncellenir
→ isDirty = true
→ önceki debounce job iptal edilir
→ 700 ms beklenir
→ gerekli alanlar ve mevcut state kontrol edilir
→ uygunsa internal Save action tetiklenir
→ save başlarken isSaving = true
→ başarılıysa isDirty = false, isSaving = false
→ hata olursa local değer korunur, isDirty = true kalır
→ SnackbarDelegate ile hata gösterilir
→ sonraki değişiklikte veya açık retry action’ında tekrar denenebilir
```

## Kurallar

1. `@Composable`, `Modifier` veya Screen extension içinde debounce, validation, coroutine, endpoint
   ya
   da save logic yazma.
2. Yeniden kullanılabilir debounce davranışını Operator tarafında küçük bir Kotlin extension/helper
   olarak oluştur. Yeni mimari katman veya framework kurma.
3. Helper, Operator’ın mevcut `CoroutineScope` değerini kullansın ve önceki autosave `Job` değerini
   iptal edebilsin.
4. Varsayılan debounce süresi 700 ms olsun, gerektiğinde parametreyle değiştirilebilsin.
5. Input action geldiğinde state senkron olarak hemen güncellensin; kullanıcı yazdığı değeri
   beklemeden
   görsün.
6. Save tetiklenmeden önce en az şu kontroller yapılsın:
    - state dirty mi,
    - halihazırda save çalışıyor mu,
    - ilgili alan gerçekten değişmiş mi,
    - gerekli alanlar geçerli mi,
    - edit işlemi için gerekli kayıt kimliği mevcut mu.
7. Debounce tamamlandığında endpoint’i doğrudan helper içinde çağırma. Operator’a ait typed internal
   save Action/callback tetikle; gerçek endpoint bağlantısı daha sonra bu noktaya eklenecek.
8. Yeni input geldiğinde bekleyen debounce iptal edilip yeniden başlatılsın.
9. Save devam ederken yeni input gelirse yeni değer kaybolmasın. Başarılı response yalnız kaydedilen
   snapshot’a aitse dirty state temizlensin; işlem sırasında state tekrar değiştiyse dirty kalmaya
   devam
   etsin ve yeni autosave planlansın.
10. Hata halinde input değerini eski haline döndürme. Local state korunmalı ve retry mümkün olmalı.
11. Aynı anda duplicate save isteği gönderme.
12. Ekran kapanırken zorunlu flush davranışı ekleme; bunun ürün kararı olduğunu kısa bir notla
    belirt.

## Diary entegrasyonu

- `DiaryUiState` içine yalnız ihtiyaç duyulan alanları ekle: örneğin `isDirty`, `isSaving` ve
  gerekiyorsa
  save hata bilgisi veya revision değeri.
- `QuoteChanged`, `TextChanged` ve diğer input Action’ları önce state’i güncellesin, ardından
  autosave’i
  planlasın.
- Yeni diary kaydı için gerekli zorunlu alanlar tamamlanmadan save action tetikleme.
- Mevcut diary güncellemesinde yalnız değişen alanları ileride PATCH body’ye aktarabilecek bir
  snapshot
  hazırla; endpoint veya repository implementasyonu bu görevde yazılmasın.
- Günün sözü için ayrı save action kullanılabilir; diary create ve diary update işlemlerini tek
  belirsiz
  action altında birleştirme.
- Snackbar gösterme kararı ve retry işlemi Operator’da kalsın.

## Beklenen örnek API

İsimleri mevcut projeye uyarlayarak aşağıdakine benzer küçük bir yapı üret:

```kotlin
fun scheduleDebouncedSave(
    delayMillis: Long = 700L,
    canSave: () -> Boolean,
    onSave: () -> Unit,
)

fun cancelPendingSave()
```

Bu imza yalnız yön göstericidir. Mevcut `BaseFeatureOperator` ve feature yapısına en küçük
değişiklikle
uygun çözümü seç. Global autosave manager, middleware, reducer veya Compose state oluşturma.

Endpoint, repository, database ve gerçek network çağrısı ekleme. Bu görev yalnız autosave tetikleme
mekanizmasını ve Diary Operator entegrasyon noktasını hazırlasın. Android ve Wasm hedeflerini
derleyerek
doğrula.
