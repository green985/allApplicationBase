package com.oyetech.dao.firebaseMessaging

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.oyetech.dao.BaseDao
import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesSendingDao : BaseDao<FirebaseMessagingLocalData> {

    @Query("select * FROM messages " + "WHERE messageId = :messageId")
    fun findRadioStationWithStationId(messageId: String): FirebaseMessagingLocalData?

    @Query("SELECT * FROM messages " + " " + "ORDER BY createdAt DESC LIMIT 1")
    fun getFirstInMessage(): Flow<FirebaseMessagingLocalData>

    @Query("SELECT * FROM messages " + " ")
    fun getMessageList(): List<FirebaseMessagingLocalData>?

    @Query("SELECT * FROM messages " + "where messageId = :messageId")
    fun getMessageWithId(messageId: String): FirebaseMessagingLocalData?

    @Query("SELECT * FROM messages " + " ")
    fun getMessageListFlow(): Flow<List<FirebaseMessagingLocalData>?>

    @Query("delete FROM messages " + "WHERE messageId in (:idList)")
    fun deleteLastList(idList: List<String>): Int

    @Query("delete FROM messages ")
    fun deleteAllList()

    @Query("select * FROM messages " + "WHERE messageId in (:messageIdList)")
    fun findRadioStationWithStationIdList(messageIdList: List<String>): List<FirebaseMessagingLocalData>

    fun clearLastListTable() {
        val list = getMessageList()
        if (list.isNullOrEmpty()) {
            return
        }
        deleteLastList(list.map { it.messageId })
    }

    @Transaction
    fun insertLastList(list: List<FirebaseMessagingLocalData>) {
//        deleteAllList()
        insert(list)
    }


}