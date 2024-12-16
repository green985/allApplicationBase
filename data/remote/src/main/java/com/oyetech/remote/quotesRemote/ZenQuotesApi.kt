package com.oyetech.remote.quotesRemote

import com.oyetech.models.quotes.responseModel.QuoteResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ZenQuotesApi {

    // Rastgele bir alıntı
    @GET("random")
    suspend fun getRandomQuote(): Response<List<QuoteResponseData>>

    // Günün alıntısı
    @GET("today")
    suspend fun getQuoteOfTheDay(): Response<List<QuoteResponseData>>

    // Yazarın alıntıları
    @GET("quotes/author/{author}")
    suspend fun getQuotesByAuthor(
        @Path("author") author: String,
    ): Response<List<QuoteResponseData>>

    // Anahtar kelimeye göre alıntılar
    @GET("quotes")
    suspend fun getQuotes(
    ): Response<List<QuoteResponseData>>

    // Anahtar kelimeye göre alıntılar
    @GET("quotes")
    suspend fun getQuotesByKeyword(
        @Query("author") author: String,
    ): Response<List<QuoteResponseData>>

    // Toplu alıntılar
    @GET("quotes")
    suspend fun getBatchQuotes(): Response<List<QuoteResponseData>>
}