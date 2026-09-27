package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpfeatures.network.EndpointStrings
import com.oyetech.kmpmodels.postbody.DiaryQuotePutBody
import com.oyetech.kmpmodels.postbody.EntryPatchBody
import com.oyetech.kmpmodels.postbody.EntryPostBody
import com.oyetech.kmpmodels.response.DiaryQuoteResponse
import com.oyetech.kmpmodels.response.EntryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class DiaryEndpointOperation(
    private val httpClient: HttpClient,
) {
    suspend fun updateQuote(
        date: String,
        quote: String,
    ): Result<DiaryQuoteResponse> = runCatching {
        println("[DiaryEndpoint] PUT /v1/diary/days/$date/quote quote=\"$quote\"")
        httpClient.put(EndpointStrings.diaryDayQuote(date)) {
            setBody(DiaryQuotePutBody(quote = quote))
        }.body()
    }

    suspend fun createEntry(
        request: EntryPostBody,
    ): Result<EntryResponse> = runCatching {
        println(
            "[DiaryEndpoint] POST /v1/diary/entries " +
                    "areaId=\"${request.areaId}\" text=\"${request.text}\"",
        )
        httpClient.post(EndpointStrings.diaryEntries) {
            setBody(request)
        }.body()
    }

    suspend fun updateEntry(
        entryId: String,
        request: EntryPatchBody,
    ): Result<EntryResponse> = runCatching {
        println(
            "[DiaryEndpoint] PATCH /v1/diary/entries/$entryId " +
                    "areaId=\"${request.areaId}\" text=\"${request.text}\"",
        )
        httpClient.patch(EndpointStrings.diaryEntry(entryId)) {
            setBody(request)
        }.body()
    }
}
