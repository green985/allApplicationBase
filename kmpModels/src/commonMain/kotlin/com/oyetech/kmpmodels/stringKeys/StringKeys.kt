package com.oyetech.kmpmodels.stringKeys

object StringKeys {
    const val diaryReminders = "Yapılacaklar"
    const val diaryAddReminder = "Yapılacaklara ekle"
    const val diaryCopyReminderToToday = "Bugün için yeni kayıt aç"
    const val diaryReminderCopied = "Bugün için yeni kayıt açıldı."
    const val diaryRemindersEmpty = "Yapılacak kayıt yok."
    const val bulkEntryTitle = "Toplu Entry Ekle"
    const val bulkEntryJsonLabel = "JSON kayıtları"
    const val bulkEntryPreview = "Önizle"
    const val bulkEntryPreviewTitle = "Kaydedilecek kayıtlar"
    const val bulkEntryPreviewHint =
        "Henüz kayıt yapılmadı. Kayıt bilgilerini kontrol edip Kaydet’e basın."
    const val bulkEntrySubmit = "Kaydet"
    const val bulkEntrySuccess = "Kaydedilen kayıtlar"
    const val bulkEntryTagsTitle = "Alanlar ve etiketler"
    const val bulkEntryAreaHasNoTags = "Bu alanda etiket yok."
    const val bulkEntryInvalidJson =
        "Geçerli JSON gerekli; yalnız requestId ve entries kabul edilir."
    const val bulkEntryInvalidRequestId = "requestId geçerli bir UUID olmalıdır."
    const val bulkEntryEmpty = "En az bir kayıt gerekli."
    const val bulkEntryInvalidFields = "Desteklenmeyen kayıt alanı."
    const val bulkEntryInvalidDate = "Tarih geçersiz."
    const val bulkEntryInvalidArea = "Alan geçersiz."
    fun bulkEntryInvalidText(maxLength: Int): String =
        "text, en fazla $maxLength karakterlik bir metin olmalıdır."

    const val bulkEntryInvalidTag = "Etiket bu alana ve hesabınıza ait olmalıdır."
    const val bulkEntryTagsNotReady = "Etiketleri yükleyip yeniden önizleyin."
    const val bulkEntryInvalidDuration = "durationSeconds, 1–86400 arasında bir tam sayı olmalıdır."
    const val bulkEntryInvalidCompletion = "markAsCompleted, true veya false olmalıdır."
    const val bulkEntryMissingContent = "Alan, not, süre veya Yapıldı seçimi gerekli."
    const val bulkEntryNoArea = "Alan yok"
    const val bulkEntryNoTag = "Etiket yok"
    const val bulkEntryNotCompleted = "Yapılmadı"
    const val bulkEntryExampleTitle = "Örnek JSON"
    const val bulkEntryExampleHint =
        "Yeni bir toplu kayıt için yeni requestId kullanın; aynı isteği tekrar denerken değiştirmeyin. " +
                "Etiket için yukarıdaki tagId değerlerinden aynı alana ait olanı kullanın veya tagId alanını kaldırın. " +
                "Süre saniye cinsindendir; süre eklemek zamanlayıcıyı başlatmaz. markAsCompleted: true, kaydı Yapıldı olarak işaretler."
    const val bulkEntryGoogleRequired = "Toplu kayıt için web üzerinde Google ile giriş gerekli."
    const val bulkEntryDiaryNotReady =
        "Önce açık kaydı kapatın ve kaydetmenin tamamlanmasını bekleyin."

    fun bulkEntryError(index: Int, message: String): String = "$index. kayıt — $message"
    fun bulkEntryResult(date: String, area: String, time: String, text: String): String =
        "$date · $area · $time’te eklendi · $text"

    const val kmpFeatures = "KMP Features"
    const val kmpFeaturesReady = "KMP Features is ready"
    const val adminLogin = "Admin giriş"
    const val openOperatorExample = "Operator örneğini aç"

    const val googleLogin = "Google ile giriş"
    const val continueWithGoogle = "Google ile devam et"
    const val back = "Geri"
    const val unconfiguredGoogleClientId = "Google Web Client ID yapılandırılmamış"
    const val googleLoginFailed = "Google login failed"
    const val sessionExpired = "Oturumunuz sona erdi. Google ile tekrar giriş yapın."
    const val sessionUnavailable =
        "Oturum kontrol edilemedi. Bağlantınızı kontrol edip tekrar deneyin."
    const val googleLoginCancelled = "Google girişi iptal edildi."
    const val unknownUser = "daha belli degil !"
    const val adminUsername = "Admin"
    fun username(username: String): String = "Kullanıcı: $username"

    const val dailyContent = "Günlük içerik"
    const val yesterday = "Dün"
    const val tomorrow = "Yarın"
    const val areas = "Arealar"
    const val returnToToday = "Bugüne dön"
    const val noAreaEntries = "Bu alana ait kayıt yok."
    const val allTags = "Tümü"
    const val noAreaTagEntries = "Bu alan ve etikete ait kayıt yok."
    const val allDates = "Tüm tarihler"
    const val lastSevenDays = "Son 7 gün"
    const val lastThirtyDays = "Son 30 gün"
    const val customDateRange = "Özel aralık"
    const val rangeStartDate = "Başlangıç tarihi"
    const val rangeEndDate = "Bitiş tarihi"
    const val dateInputFormat = "YYYY-AA-GG"
    const val applyDateRange = "Uygula"
    const val cancelDateRange = "Vazgeç"
    const val invalidDateRange = "Geçerli tarihleri YYYY-AA-GG biçiminde girin."
    const val reversedDateRange = "Bitiş tarihi başlangıç tarihinden önce olamaz."
    const val noFilteredAreaEntries = "Seçilen filtrelere uygun kayıt yok."
    fun areaEntriesTitle(area: String, dates: String = allDates): String = "$area · $dates"

    const val diary = "Günlük"
    const val downloadDiaryHtml = "Günlüğü HTML indir"
    const val downloadDiaryArchive = "Tüm günleri tek HTML indir"
    const val downloadDiaryWeekly = "Haftalık indir"
    const val diaryWeeklyReport = "Haftalık günlük raporu"
    const val diaryWeeklyDownloadConfirm = "HTML indir"
    const val diaryWeeklySelectDate = "Tarihi seç"
    const val diaryWeeklyEmpty = "Bu tarih aralığında günlük kaydı veya söz bulunamadı."
    const val diaryArchive = "Günlük arşivi"
    const val diaryArchiveNotReady =
        "Arşiv indirmeden önce açık kaydı kapatın ve kaydın tamamlanmasını bekleyin."
    const val diaryArchiveEmpty = "Arşivlenecek günlük kaydı veya söz bulunamadı."
    const val diaryExportNotReady = "Günlük verileri yüklenirken veya kaydedilirken tekrar deneyin."
    const val entryCreatedAt = "Oluşturulma zamanı"
    const val timerStartedAt = "Timer başlangıcı"
    const val noDiaryEntries = "Bu güne ait kayıt yok."
    fun diaryExportDuration(seconds: Long): String = when {
        seconds < 60 -> "$seconds sn"
        seconds % 60 == 0L -> "${seconds / 60} dk"
        else -> "${seconds / 60} dk ${seconds % 60} sn"
    }

    const val addEntry = "+"
    const val today = "Bugün"
    const val selectedDay = "Seçili gün"
    const val todaysQuote = "Bugünün sözü"
    const val writeTodaysQuote = "Bugün için bir söz yaz"
    const val dailyThoughts = "Günün düşünceleri"
    const val showDailyThoughts = "Düşünceleri aç"
    const val hideDailyThoughts = "Düşünceleri kapat"
    const val dailyThoughtsPlaceholder = "Aklına gelenleri sabah raporu için not et"
    const val dailyThoughtsNotSaved = "Günün düşünceleri henüz kaydedilmedi."
    const val newEntry = "Yeni kayıt"
    const val cancelEntry = "Kaydı iptal et"
    const val area = "Alan"
    const val optionalArea = "Alan (isteğe bağlı)"
    const val tag = "Etiket"
    const val optionalTag = "Etiket (isteğe bağlı)"
    const val tagSearchPlaceholder = "Etiket ara veya oluştur"
    const val clearTag = "Etiketi kaldır"
    const val tagNameTooLong = "Etiket adı en fazla 40 karakter olabilir."
    fun createTag(name: String): String = "\"$name\" etiketini oluştur"
    const val duration = "Süre"
    const val selectAreaRequired = "Alan seçmelisiniz."
    const val note = "Not"
    const val save = "Save"
    const val delete = "Sil"
    const val retry = "Tekrar dene"
    const val alreadyDone = "Yapıldı"
    const val fiveMinutes = "5 dk"
    const val tenMinutes = "10 dk"
    const val fifteenMinutes = "15 dk"
    const val twentyMinutes = "20 dk"
    const val custom = "Özel"
    const val start = "Başlat"
    const val pending = "Bekliyor"
    const val finished = "Tamamlandı"
    const val cancelled = "İptal edildi"
    const val timerFinished = "Timer tamamlandı."
    const val cancelTimer = "İptal et"
    const val singleTimerOnly = "Aynı anda yalnız bir timer çalışabilir."
    const val entryInputRequired = "Alan, not, süre veya Yapıldı seçimi gerekli."
    fun entryTextTooLong(maxLength: Int): String = "Not en fazla $maxLength karakter olabilir."
    fun customDurationInvalid(maxMinutes: Long): String =
        "Süre 1 ile $maxMinutes arasında tam sayı olmalıdır."

    const val workArea = "İnşa"
    const val bodyArea = "Beden"
    const val mindArea = "Zihin"
    const val characterArea = "Karakter"
    const val lifeArea = "Yaşam"
    const val workAreaDescription = "Bir şey üretmek, proje ve hedef geliştirmek"
    const val bodyAreaDescription = "Spor, fiziksel sağlık ve bakım"
    const val mindAreaDescription = "Öğrenme, düşünme ve araştırma"
    const val characterAreaDescription = "Değerler, disiplin ve kişisel yön"
    const val lifeAreaDescription = "İnsanlar, seyahat ve deneyimler"

    const val operatorExample = "Operator example"
    fun completedOperations(count: Int): String = "Completed operations: $count"
    const val runOperation = "Run operation"
    const val reset = "Reset"
    const val operationCompleted = "Operation completed"

    val turkishMonths = listOf(
        "Ocak",
        "Şubat",
        "Mart",
        "Nisan",
        "Mayıs",
        "Haziran",
        "Temmuz",
        "Ağustos",
        "Eylül",
        "Ekim",
        "Kasım",
        "Aralık",
    )
}
