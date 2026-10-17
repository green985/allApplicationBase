package com.oyetech.kmpmodels.stringKeys

object StringKeys {
    const val kmpFeatures = "KMP Features"
    const val kmpFeaturesReady = "KMP Features is ready"
    const val adminLogin = "Admin giriş"
    const val openOperatorExample = "Operator örneğini aç"

    const val googleLogin = "Google ile giriş"
    const val continueWithGoogle = "Google ile devam et"
    const val back = "Geri"
    const val unconfiguredGoogleClientId = "Google Web Client ID yapılandırılmamış"
    const val googleLoginFailed = "Google login failed"
    const val unknownUser = "daha belli degil !"
    const val adminUsername = "Admin"
    fun username(username: String): String = "Kullanıcı: $username"

    const val dailyContent = "Günlük içerik"
    const val yesterday = "Dün"
    const val tomorrow = "Yarın"

    const val diary = "Günlük"
    const val downloadDiaryHtml = "Günlüğü HTML indir"
    const val downloadDiaryArchive = "Tüm günleri tek HTML indir"
    const val diaryArchive = "Günlük arşivi"
    const val diaryArchiveNotReady =
        "Arşiv indirmeden önce açık kaydı kapatın ve kaydın tamamlanmasını bekleyin."
    const val diaryArchiveEmpty = "Arşivlenecek günlük kaydı veya söz bulunamadı."
    const val diaryExportNotReady = "Günlük verileri yüklenirken veya kaydedilirken tekrar deneyin."
    const val entryCreatedAt = "Oluşturulma zamanı"
    const val timerStartedAt = "Timer başlangıcı"
    const val noDiaryEntries = "Bu güne ait kayıt yok."
    fun diaryExportDuration(seconds: Long): String = "${seconds / 60} dk ${seconds % 60} sn"
    const val addEntry = "+"
    const val today = "Bugün"
    const val selectedDay = "Seçili gün"
    const val todaysQuote = "Bugünün sözü"
    const val writeTodaysQuote = "Bugün için bir söz yaz"
    const val newEntry = "Yeni kayıt"
    const val area = "Alan"
    const val optionalArea = "Alan (isteğe bağlı)"
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
