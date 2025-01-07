package com.oyetech.repository.quotesImp.quotesData

import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.models.quotes.responseModel.QuoteAuthorResponseData
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import com.oyetech.remote.helper.interceptTrueForm
import com.oyetech.remote.quotesRemote.ZenQuotesApi
import kotlinx.coroutines.flow.Flow

class QuotesRepositoryImp(private val api: ZenQuotesApi) : QuotesRepository {

    override suspend fun getQuotesByKeyword(
        keyword: String,
    ): Flow<List<QuoteResponseData>> {
        // list size max 50
        val quotes = interceptTrueForm {
            api.getQuotesByKeyword(keyword)
        }
        return quotes
    }

    override suspend fun getQuotesByAuthor(
        author: String,
    ): Flow<List<QuoteResponseData>> {
        // list size max 50
        val quotes = interceptTrueForm {
            api.getQuotesByAuthor(author)
        }
        return quotes
    }

    override suspend fun getAuthors(): Flow<List<QuoteAuthorResponseData>> {
        val quotes = interceptTrueForm {
            api.getAuthors()
        }
        return quotes
    }

    override fun getQuoteOfTheDay(): Flow<List<QuoteResponseData>> {
        val quotes = interceptTrueForm {
            api.getQuoteOfTheDay()
        }
        return quotes
    }

    override fun getQuotes(): Flow<List<QuoteResponseData>> {
        val quotes = interceptTrueForm {
            api.getQuotes()
        }
        return quotes
    }

}
