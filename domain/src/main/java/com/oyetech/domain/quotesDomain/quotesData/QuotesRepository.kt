package com.oyetech.domain.quotesDomain.quotesData

import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-16.12.2024-
-22:51-
 **/

interface QuotesRepository {
    fun fetchRandomQuotes(): Flow<List<QuoteResponseData>>
    fun fetchQuotes(): Flow<List<QuoteResponseData>>
}