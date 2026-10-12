package com.oyetech.kmpfeatures.network

object EndpointStrings {
    private const val baseUrl = "https://uduwhuvgdcacvdhzheyi.supabase.co/functions"

    const val getUserWithToken = "$baseUrl/v1/getUserWithToken"
    const val registerGoogleUser = "$baseUrl/v1/registerGoogleUser"
    private const val kmpFunctionsBaseUrl = "$baseUrl/v1/kmpFunctions"
    const val diaryEntries = "$kmpFunctionsBaseUrl/diary/entries"

    fun diaryEntriesForDate(date: String): String =
        diaryDayEntries(date)

    fun diaryDayEntries(date: String): String =
        "$kmpFunctionsBaseUrl/diary/days/$date/entries"

    fun diaryDayQuote(date: String): String =
        "$kmpFunctionsBaseUrl/diary/days/$date/quote"

    fun diaryEntry(entryId: String): String =
        "$diaryEntries/$entryId"

    fun diaryEntryTimerStart(entryId: String): String =
        "$diaryEntries/$entryId/timer/start"

    fun diaryEntryTimerCancel(entryId: String): String =
        "$diaryEntries/$entryId/timer/cancel"
}
