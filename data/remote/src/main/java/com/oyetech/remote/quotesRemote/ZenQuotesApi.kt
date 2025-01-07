package com.oyetech.remote.quotesRemote

import com.oyetech.models.quotes.responseModel.QuoteAuthorResponseData
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

    // Günün alıntısı
    @GET("authors")
    suspend fun getAuthors(): Response<List<QuoteAuthorResponseData>>

    // Yazarın alıntıları
    @GET("quotes/author/{author}")
    suspend fun getQuotesByAuthor(
        @Path("author") author: String,
    ): Response<List<QuoteResponseData>>

    @GET("quotes")
    suspend fun getQuotes(
    ): Response<List<QuoteResponseData>>

    // Anahtar kelimeye göre alıntılar
    @GET("quotes")
    suspend fun getQuotesByKeyword(
        @Query("keyword") keyword: String,
    ): Response<List<QuoteResponseData>>
}