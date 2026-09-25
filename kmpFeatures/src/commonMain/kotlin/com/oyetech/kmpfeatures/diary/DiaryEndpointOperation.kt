package com.oyetech.kmpfeatures.diary

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
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DiaryEndpointOperation(
    private val httpClient: HttpClient,
) {
    suspend fun updateQuote(
        date: String,
        quote: String,
    ): Result<DiaryQuoteResponse> = runCatching {
        httpClient.put("$baseUrl/v1/diary/days/$date/quote") {
            contentType(ContentType.Application.Json)
            setBody(DiaryQuotePutBody(quote = quote))
        }.body()
    }

    suspend fun createEntry(
        request: EntryPostBody,
    ): Result<EntryResponse> = runCatching {
        httpClient.post("$baseUrl/v1/diary/entries") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateEntry(
        entryId: String,
        request: EntryPatchBody,
    ): Result<EntryResponse> = runCatching {
        httpClient.patch("$baseUrl/v1/diary/entries/$entryId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    private companion object {
        const val baseUrl = "https://uduwhuvgdcacvdhzheyi.supabase.co/functions"
    }
}
