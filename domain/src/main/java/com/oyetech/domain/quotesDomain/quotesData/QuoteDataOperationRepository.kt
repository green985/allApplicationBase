package com.oyetech.domain.quotesDomain.quotesData

import com.oyetech.models.quotes.responseModel.QuoteAuthorResponseData
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
    suspend fun getQuoteUnseenFlow(oldList: Array<String>): Flow<List<QuoteResponseData>>
    suspend fun getAuthorList(): Flow<List<QuoteAuthorResponseData>>
    suspend fun getSingleQuote(quoteId: String): Flow<QuoteResponseData>
    fun submitQuote(
        quoteText: String,
        authorText: String,
        tags: List<String>,
        noteToInspector: String,
        isCheckedTruthForm: Boolean,
    ): Flow<Unit>

    suspend fun searchQuote(searchQuery: String): Flow<List<QuoteResponseData>>
}