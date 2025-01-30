package com.oyetech.repository.quotesImp

import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.domain.repository.firebase.FirebaseQuotesOperationRepository
import com.oyetech.models.quotes.responseModel.QuoteAuthorResponseData
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.quotes.dao.QuotesAllListDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    private val errorQuotesString = "Too many requests. Obtain an auth key for unlimited access."
    var isError = false
    override fun getRandomRemoteQuote(): Flow<List<QuoteResponseData>> {
        return quotesRepository.getQuotes().map {
            var quoteResponseList = it
            quoteResponseList = it.map {
                if (it.text != errorQuotesString) {
                    it
                } else {
                    null
                }
            }.filterNotNull()

//            if (isError) {
//                return@map emptyList<QuoteResponseData>()
//            } else {
//                isError = true
//            }
            quoteResponseList
        }.map {
            val quoteResponseList = it
            if (it.isEmpty()) {
                it
            } else {
                Timber.d(" getRandomRemoteQuote  " + it.size)
                quotesAllListDao.insert(quoteResponseList)
                firebaseQuotesOperationRepository.saveListWithNoTag(quoteResponseList)
                quoteResponseList
            }

        }
    }

    override suspend fun getAuthorList(): Flow<List<QuoteAuthorResponseData>> {
        return quotesRepository.getAuthors()
    }

    override suspend fun getSingleQuote(quoteId: String): Flow<QuoteResponseData> {
        return flow {
            if (quoteId == "randomSingle") {
                val list = getQuoteUnseenFlow(emptyArray()).first()
                emit(list.first())
            } else {
                emit(quotesAllListDao.findQuoteWithQuotesIdList(listOf(quoteId)).first())
            }
        }
    }

    override fun submitQuote(
        quoteText: String,
        authorText: String,
        tags: List<String>,
        noteToInspector: String,
        isCheckedTruthForm: Boolean,
    ): Flow<Boolean> {

        return flow {
//            val quoteResponseData = QuoteResponseData(
//                id = "",
//                text = quoteText,
//                author = authorText,
//                tags = tags,
//                unseen = true,
//                noteToInspector = noteToInspector,
//                isCheckedTruthForm = isCheckedTruthForm
//            )
//            quotesAllListDao.insert(quoteResponseData)
//            firebaseQuotesOperationRepository.saveListWithNoTag(listOf(quoteResponseData))
            emit(true)
        }
    }

    override suspend fun getQuoteUnseenFlow(oldList: Array<String>): Flow<List<QuoteResponseData>> {
        return quotesAllListDao.getQuoteUnseenFlow(oldList)
            .let {
                var list = emptyList<QuoteResponseData>()
                val leftCount = quotesAllListDao.getListCount(oldList)
                if (leftCount < HelperConstant.QUOTES_PAGER_LIMIT * 3) {
                    val remoteQuotesList =
                        getRandomRemoteQuote().firstOrNull()

                    list = remoteQuotesList?.subList(0, 20) ?: emptyList<QuoteResponseData>()

                } else {
                    list = it
                }

                flowOf(list)
            }
    }

    override fun setSeenQuote(quoteId: String): Flow<Unit> {
        return flow {
            quotesAllListDao.setSeenQuote(quoteId)
        }
    }
}