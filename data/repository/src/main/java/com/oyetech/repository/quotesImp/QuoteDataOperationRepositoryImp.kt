package com.oyetech.repository.quotesImp

import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.quotes.dao.QuotesAllListDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

    override fun getQuoteUnseenFlow(oldList: Array<String>): Flow<List<QuoteResponseData>> {
        return flowOf(quotesAllListDao.getQuoteUnseenFlow(oldList)).map {
            val leftCount = quotesAllListDao.getListCount(oldList)
            if (leftCount < HelperConstant.QUOTES_PAGER_LIMIT * 3) {
                val remoteQuotesList =
                    getRandomRemoteQuote().firstOrNull()
                remoteQuotesList?.subList(0, 10) ?: emptyList()
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