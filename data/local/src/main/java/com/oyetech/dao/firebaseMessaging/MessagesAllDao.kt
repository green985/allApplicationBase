package com.oyetech.dao.firebaseMessaging

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.oyetech.dao.BaseDao
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-25.02.2025-
-23:17-
 **/
@Dao
interface MessagesAllDao : BaseDao<FirebaseMessagingLocalData> {

    @Query("SELECT * FROM messages " + " " + "ORDER BY createdAt DESC LIMIT 1")
    fun getFirstInMessage(): Flow<FirebaseMessagingLocalData>

    @Query("SELECT * FROM messages " + "where conversationId = :conversationId ORDER BY createdAt  " + "limit 100 ")
    fun getMessageListFlow(conversationId: String): Flow<List<FirebaseMessagingLocalData>>

    @Query(
        """
        SELECT * FROM messages
        WHERE conversationId = :conversationId 
        AND createdAt < (SELECT createdAt FROM messages WHERE messageId = :messageId)
        ORDER BY createdAt DESC
        LIMIT 30
    """
    )
    fun getMessageListWithLastMessageId(
        conversationId: String,
        messageId: String,
    ): List<FirebaseMessagingLocalData>

    @Query("SELECT * FROM messages " + "where messageId = :messageId")
    fun getMessageWithId(messageId: String): FirebaseMessagingLocalData?

    @Query("delete FROM messages " + "WHERE messageId in (:idList)")
    fun deleteLastList(idList: List<String>): Int

    @Query("delete FROM messages ")
    fun deleteAllList()

    @Query("select * FROM messages " + "WHERE messageId in (:messageIdList)")
    fun findMessageListWithMessageIdList(messageIdList: List<String>): List<FirebaseMessagingLocalData>

    @Transaction
    fun insertLastList(list: List<FirebaseMessagingLocalData>) {
//        deleteAllList()
        insert(list)
    }


}