package com.oyetech.repository.quotesImp

import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import com.oyetech.quotes.dao.QuotesAllListDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
Created by Erdi Özbek
-18.12.2024-
-02:30-
 **/

class QuoteDataOperationRepositoryImp(
    private val quotesRepository: QuotesRepository,
    private val quotesAllListDao: QuotesAllListDao,
) : QuoteDataOperationRepository {

    override fun getRandomRemoteQuote(): Flow<List<QuoteResponseData>> {
        return quotesRepository.fetchQuotes().map {
            val quoteResponseList = it
            Timber.d(" getRandomRemoteQuote  " + it.size)
            quotesAllListDao.insert(quoteResponseList)
            quoteResponseList
        }
    }

    //will be return quote list but not visible quotes

    override fun getQuoteUnseenFlow(): Flow<List<QuoteResponseData>> {
        return quotesAllListDao.getQuoteUnseenFlow().map {
            if (it.size < 5) {
                val remoteQuotesList =
                    getRandomRemoteQuote().firstOrNull()
                remoteQuotesList ?: emptyList()
            } else {
                it
            }
        }
    }

    override fun setSeenQuote(quoteId: String): Flow<Unit> {
        return flow {
            Timber.d(" setSeenQuote  $quoteId")
            quotesAllListDao.setSeenQuote(quoteId)
        }
    }
}