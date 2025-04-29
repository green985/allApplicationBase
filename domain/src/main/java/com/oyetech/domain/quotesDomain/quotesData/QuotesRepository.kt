package com.oyetech.domain.quotesDomain.quotesData

import com.oyetech.models.quotes.responseModel.QuoteAuthorResponseData
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-16.12.2024-
-22:51-
 **/

interface QuotesRepository {
    fun getQuotes(): Flow<List<QuoteResponseData>>
    fun getQuoteOfTheDay(): Flow<List<QuoteResponseData>>
    suspend fun getQuotesByKeyword(keyword: String): Flow<List<QuoteResponseData>>
    suspend fun getQuotesByAuthor(author: String): Flow<List<QuoteResponseData>>

    suspend fun getAuthors(): Flow<List<QuoteAuthorResponseData>>
}