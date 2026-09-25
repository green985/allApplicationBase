# KMP Domain — Navigation ve Snackbar Taşıması

Mevcut Android davranışını koruyarak küçük ve kontrollü bir migration yap.

1. Android ve Wasm target’ları bulunan `kmpDomain` KMP modülünü oluştur, `settings.gradle` içine
   ekle.
   `commonMain`, `kmpModels` ve `kotlinx-coroutines-core` bağımlılıklarını kullansın. Compose veya
   Android
   bağımlılığı ekleme.
2. `NavigationUseCase` sınıfını Android `domain` modülünden
   `kmpDomain/src/commonMain/.../usecase/navigation` altına taşı. `@MainThread` gibi Android
   bağımlılıklarını kaldır; `setNavigator`, bekleyen route, `navigateTo` ve `goBack` davranışlarını
   koru. Ekran yaşam döngüsü sonunda eski navigator referansını temizlemek için gerekirse küçük bir
   `clearNavigator()` API’si ekle.
3. `SnackbarDelegate` sınıfını `composeBase` modülünden
   `kmpDomain/src/commonMain/.../delegate/snackbar` altına taşı. Mutable flow’u private tut ve
   dışarıya
   read-only `StateFlow` olarak aç. Mevcut snackbar mesajı, action label ve action callback
   davranışını
   koru.
4. `SnackbarUiState` bir modeldir; onu `kmpModels/src/commonMain/.../ui/state` altına taşı. Common
   kodda Android/JVM API kullanma. Benzersiz event kimliği gerekiyorsa KMP uyumlu zaman API’si
   kullan.
5. Eski sınıf kopyalarını bırakma. Android’deki tüm importları yeni paketlere geçir. `composeBase`,
   `diModule`, `wearModule` ve gerçek tüketici olan diğer Android modüllerine doğrudan `kmpDomain`
   bağımlılığı ekle. Mevcut Koin kayıtlarını yeni sınıfları üretecek şekilde güncelle ve duplicate
   kayıt
   oluşturma.
6. `kmpFeatures` commonMain’e `kmpDomain` bağımlılığı ekle. `NavigationUseCase` ve
   `SnackbarDelegate` için ortak Koin singleton kayıtları oluştur.
7. KMP root/navigation setup içinde `NavigationUseCase` ile `NavController` bağlantısını kur; route
   navigation ve back işlemlerini burada adapte et. Lifecycle bittiğinde navigator bağlantısını
   temizle.
8. KMP root Scaffold içinde tek bir snackbar host oluştur. `SnackbarDelegate` state’ini collect
   ederek
   mesajı göster ve varsa action callback’ini çalıştır. Delegate hiçbir Compose tipi bilmesin.
9. Yalnız mevcut Operator örneğini bu iki ortak yapının kullanım örneğine dönüştür:
    - geri aksiyonunda `NavigationUseCase.goBack()` kullan,
    - operasyon tamamlandığında `SnackbarDelegate` üzerinden mesaj göster.
      Diğer feature’ları bu iterasyonda topluca migrate etme.
10. Eski Android ekranlarının navigation ve snackbar davranışını değiştirme. Package/import ve
    module
    dependency migration’ı dışında unrelated refactor yapma.
11. `kmpDomain` common testlerinde en az navigation pending-route davranışını ve snackbar state
    güncellemesini doğrula. Ardından `kmpDomain`, `kmpFeatures` Android/Wasm ve etkilenen Android
    modüllerini derle/test et.

Sonuçta `kmpDomain` platformdan bağımsız kalmalı; Android ve KMP aynı `NavigationUseCase` ile
`SnackbarDelegate` implementasyonlarını kullanmalı. UI, NavController, SnackbarHost ve lifecycle
bağlantıları kendi presentation/setup katmanlarında kalmalı.
