package com.oyetech.domain.quotesDomain.quotesData

import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-18.12.2024-
-02:42-
 **/

interface QuoteDataOperationRepository {
    fun getRandomRemoteQuote(): Flow<List<QuoteResponseData>>
    fun setSeenQuote(quoteId: String): Flow<Unit>
    fun getQuoteUnseenFlow(oldList: Array<String>): Flow<List<QuoteResponseData>>
}