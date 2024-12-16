package com.oyetech.repository.quotesImp.quotesData

import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import com.oyetech.remote.helper.interceptTrueForm
import com.oyetech.remote.quotesRemote.ZenQuotesApi
import kotlinx.coroutines.flow.Flow

class QuotesRepositoryImp(private val api: ZenQuotesApi) : QuotesRepository {

    override fun fetchRandomQuotes(): Flow<List<QuoteResponseData>> {
        val quotes = interceptTrueForm {

            api.getRandomQuote()
        }
        return quotes
    }

    override fun fetchQuotes(): Flow<List<QuoteResponseData>> {
        val quotes = interceptTrueForm {
            api.getQuotes()
        }
        return quotes
    }

}
