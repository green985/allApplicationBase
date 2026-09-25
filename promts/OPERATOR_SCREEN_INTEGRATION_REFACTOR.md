# Operator ve Screen Entegrasyonu Refactor

Mevcut Operator örneğini aşağıdaki kurallara göre küçük ve davranış koruyan bir refactor ile
düzenle:

1. Feature `ScreenSetup` içinde yalnız feature Operator’ını inject et. Android setup’ta
   `koinViewModel` ile alınan Operator wrapper ViewModel bu sınırın parçasıdır.
2. `NavigationUseCase`, `SnackbarDelegate`, repository ve diğer bağımlılıkları ScreenSetup veya
   ScreenConnection içinde inject etme. Bunları Koin üzerinden Operator constructor’ına ver.
   Operator
   içinde `koinInject` veya service-locator `get()` çağrısı kullanma.
3. Geri aksiyonunu Operator içinde `NavigationUseCase.goBack()` ile, operasyon başarı mesajını
   `SnackbarDelegate` ile yönet. Artık kullanılmayan navigation/snackbar effect tiplerini ve
   collector
   dallarını kaldır.
4. Effect kullanmayan Operator için generic effect tipi olarak `Nothing` kullan. Nullable effect
   teknik olarak desteklenmeye devam etsin; “effect yok” anlamında `null` emit etme.
5. Ekran Android ve KMP/Wasm arasında farklı lifecycle setup’larıyla gerçekten paylaşılıyorsa ortak
   `OperatorExampleScreenConnection` korunabilir. Bu Connection yalnız state collection,
   `operator::dispatch` ve stateless Screen bağlantısını içersin; dependency injection yapmasın.
6. KMP-only başka ekranlarda `ScreenConnection` oluşturma. Operator injection, state collection ve
   Screen bağlantısını doğrudan `ScreenSetup` içinde yap.
7. Stateless Screen yalnız `UiState` ve action callback alsın; Operator, Koin, navigation, snackbar
   ve
   coroutine scope bilmesin.
8. Koin Operator factory kaydını constructor bağımlılıklarını çözecek şekilde güncelle. Operator
   `factory`, paylaşılan use case/delegate/repository bağımlılıkları uygun olduğunda `single`
   kalsın.
9. NavGraph yalnız route ile ScreenSetup’ı açsın. Operator navigation yönettiğinde kullanılmayan
   `onBackClick` callback zincirini kaldır.
10. Application root içindeki `NavigationUseCase`–`NavController` ve
    `SnackbarDelegate`–`SnackbarHost` bağlantılarını koru; global UI altyapısı feature Operator
    içine
    taşınmamalı.
11. İlgisiz feature’ları migrate etme ve yeni abstraction/layer oluşturma. Android ve Wasm
    hedeflerini
    derleyerek mevcut davranışı doğrula.

Beklenen sade akış:

```text
NavGraph -> ScreenSetup -> Operator/ViewModel -> State -> Stateless Screen
                                      Action <- Stateless Screen

Operator -> NavigationUseCase
Operator -> SnackbarDelegate
Operator -> Repository/UseCase
```
