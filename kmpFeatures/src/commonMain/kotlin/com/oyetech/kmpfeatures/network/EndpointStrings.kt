package com.oyetech.kmpfeatures.network

object EndpointStrings {
    private const val baseUrl = "https://uduwhuvgdcacvdhzheyi.supabase.co/functions"

    const val getUserWithToken = "$baseUrl/v1/getUserWithToken"
    const val registerGoogleUser = "$baseUrl/v1/registerGoogleUser"
    const val diaryEntries = "$baseUrl/v1/kmpFunctions/diary/entries"

    fun diaryEntriesForDate(date: String): String =
        "$diaryEntries?date=$date"

    fun diaryDayQuote(date: String): String =
        "$baseUrl/v1/kmpFunctions/diary/days/$date/quote"

    fun diaryEntry(entryId: String): String =
        "$diaryEntries/$entryId"
}
