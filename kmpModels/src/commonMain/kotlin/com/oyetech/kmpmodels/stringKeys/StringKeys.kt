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
    const val addEntry = "+"
    const val today = "Bugün"
    const val selectedDay = "Seçili gün"
    const val todaysQuote = "Bugünün sözü"
    const val writeTodaysQuote = "Bugün için bir söz yaz"
    const val newEntry = "Yeni kayıt"
    const val area = "Alan"
    const val idleArea = "Beklemede"
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
    const val workArea = "İş / İnşa"
    const val bodyArea = "Beden"
    const val healthArea = "Sağlık"
    const val mindArea = "Zihin"
    const val characterArea = "Karakter"
    const val peopleArea = "İnsanlar"
    const val lifeArea = "Hayat"

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
