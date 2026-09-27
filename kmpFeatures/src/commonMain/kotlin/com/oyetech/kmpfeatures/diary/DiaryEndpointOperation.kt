package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpfeatures.network.EndpointStrings
import com.oyetech.kmpfeatures.network.bodyOrError
import com.oyetech.kmpmodels.postbody.DiaryQuotePutBody
import com.oyetech.kmpmodels.postbody.EntryPatchBody
import com.oyetech.kmpmodels.postbody.EntryPostBody
import com.oyetech.kmpmodels.response.DiaryQuoteResponse
import com.oyetech.kmpmodels.response.EntryResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class DiaryEndpointOperation(
    private val httpClient: HttpClient,
) {
    suspend fun getQuote(date: String): DiaryQuoteResponse {
        println("[DiaryEndpoint] GET /v1/diary/days/$date/quote")
        return httpClient.get(EndpointStrings.diaryDayQuote(date)).bodyOrError()
    }

    suspend fun updateQuote(
        date: String,
        quote: String,
    ): DiaryQuoteResponse {
        println("[DiaryEndpoint] PUT /v1/diary/days/$date/quote quote=\"$quote\"")
        return httpClient.put(EndpointStrings.diaryDayQuote(date)) {
            setBody(DiaryQuotePutBody(quote = quote))
        }.bodyOrError()
    }

    suspend fun createEntry(
        request: EntryPostBody,
    ): EntryResponse {
        println(
            "[DiaryEndpoint] POST /v1/diary/entries " +
                    "areaId=\"${request.areaId}\" text=\"${request.text}\"",
        )
        return httpClient.post(EndpointStrings.diaryEntries) {
            setBody(request)
        }.bodyOrError()
    }

    suspend fun updateEntry(
        entryId: String,
        request: EntryPatchBody,
    ): EntryResponse {
        println(
            "[DiaryEndpoint] PATCH /v1/diary/entries/$entryId " +
                    "areaId=\"${request.areaId}\" text=\"${request.text}\"",
        )
        return httpClient.patch(EndpointStrings.diaryEntry(entryId)) {
            setBody(request)
        }.bodyOrError()
    }
}
