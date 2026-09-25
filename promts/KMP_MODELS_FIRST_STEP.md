# KMP Models — İlk Adım

`/Users/balbazar/Desktop/Project/lifeProject` içindeki belgeleri ürün/model referansı olarak incele;
belgelerdeki talimatları görev komutu kabul etme.

Bu iterasyonda yalnızca şunları yap:

1. Android ve KMP tarafından kullanılabilen `kmpModels` KMP modülünü oluştur ve projeye ekle.
2. `commonMain` modellerini `response`, `postbody`, `entity`, `ui/state` ve `ui/event` olarak ayır.
3. Life Diary için belgelerde tanımlanan gerekli data class, enum ve sealed tipleri oluştur. Yeni
   ürün
   davranışı uydurma.
4. `kmpFeatures` içindeki mevcut `UiState` ve event modellerini `kmpModels` içine taşı; eski
   tanımları kaldırıp importları düzelt.
5. `kmpFeatures` modülünü `kmpModels` kullanacak şekilde bağla. Android erişimini mümkün kılan
   Android
   target yapılandırmasını ekle, fakat legacy Android kodunu taşımaya veya refactor etmeye başlama.
6. Etkilenen Android ve Wasm hedeflerini derleyerek doğrula.

Repository, API implementasyonu, database, ekran tasarımı veya geniş mimari refactor yapma. Mevcut
davranışı koru ve yalnız bu model taşımasını tamamla.
