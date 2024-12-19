package com.oyetech.quotes.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.oyetech.dao.BaseDao
import com.oyetech.models.quotes.responseModel.QuoteResponseData

/**
Created by Erdi Özbek
-18.12.2024-
-02:17-
 **/

@Dao
interface QuotesAllListDao : BaseDao<QuoteResponseData> {

    @Query("select * FROM quoteDataModel " + "WHERE quoteId = :quoteId")
    fun findQuoteWithQuoteId(quoteId: String): QuoteResponseData?

    @Query("SELECT * FROM quoteDataModel " + " ")
    fun getQuoteLastList(): List<QuoteResponseData>?

    @Query("delete FROM quoteDataModel " + "WHERE quoteId in (:idList)")
    fun deleteLastList(idList: List<String>): Int

    @Query("delete FROM quoteDataModel ")
    fun deleteAllList()

    @Query("select * FROM quoteDataModel " + "WHERE quoteId in (:quoteIdList)")
    fun findQuoteWithQuotesIdList(quoteIdList: List<String>): List<QuoteResponseData>

    @Query("select * FROM quoteDataModel " + "WHERE isSeen = 0 ORDER BY RANDOM() LIMIT 20")
    fun getQuoteUnseenFlow(): List<QuoteResponseData>

    @Transaction
    fun insertLastList(list: List<QuoteResponseData>) {
        insert(list)
    }

    @Query("UPDATE quoteDataModel SET isSeen = 1 WHERE quoteId = :quoteId")
    fun setSeenQuote(quoteId: String)

}