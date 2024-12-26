package com.oyetech.repository.quotesImp

import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.domain.repository.firebase.FirebaseQuotesOperationRepository
import com.oyetech.languageModule.keyset.ExceptionKeys
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
    private val firebaseQuotesOperationRepository: FirebaseQuotesOperationRepository,
) : QuoteDataOperationRepository {

    var flaggg = false
    private val errorQuotesString = "Too many requests. Obtain an auth key for unlimited access."

    override fun getRandomRemoteQuote(): Flow<List<QuoteResponseData>> {
        return quotesRepository.fetchQuotes().map {
            val quoteResponseList = it
            val isError = quoteResponseList.firstOrNull()?.let {
                it.text == errorQuotesString
            } ?: false
            Timber.d(" isError  " + isError)

            if (flaggg) {
                Timber.d(" isError  " + isError)
                return@map emptyList<QuoteResponseData>()
            } else {
                flaggg = true
            }

            if (isError) {
                emptyList<QuoteResponseData>()
//                delay(5000)
//                quotesRepository.fetchQuotes().firstOrNull() ?: emptyList()
            } else {
                quoteResponseList
            }
            quoteResponseList
        }.map {
            val quoteResponseList = it
            if (it.isEmpty()) {

                throw Exception(ExceptionKeys.emptyList)
            } else {
                Timber.d(" getRandomRemoteQuote  " + it.size)
                quotesAllListDao.insert(quoteResponseList)
                firebaseQuotesOperationRepository.saveListWithNoTag(quoteResponseList)
                quoteResponseList
            }

        }
    }

    override fun getQuoteUnseenFlow(oldList: Array<String>): Flow<List<QuoteResponseData>> {
        return flowOf(quotesAllListDao.getQuoteUnseenFlow(oldList)).map {
            val leftCount = quotesAllListDao.getListCount(oldList)
            if (leftCount < HelperConstant.QUOTES_PAGER_LIMIT * 3) {
                val remoteQuotesList =
                    getRandomRemoteQuote().firstOrNull()
                remoteQuotesList?.subList(0, 20) ?: emptyList()
            } else {
                it
            }
        }
    }

    override fun setSeenQuote(quoteId: String): Flow<Unit> {
        return flow {
            quotesAllListDao.setSeenQuote(quoteId)
        }
    }
}