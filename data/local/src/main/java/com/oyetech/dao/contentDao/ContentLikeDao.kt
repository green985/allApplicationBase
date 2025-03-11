package com.oyetech.dao.contentDao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.oyetech.dao.BaseDao
import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-11.03.2025-
-20:37-
 **/

@Dao
interface ContentLikeDao : BaseDao<LikeOperationModel> {

    @Query("SELECT * FROM contentLike " + " ")
    fun getContentLikeList(): List<LikeOperationModel>?

    @Query("SELECT * FROM contentLike " + " WHERE contentId in (:contentIdList)")
    fun getContentLikeListFlow(contentIdList: List<String>): Flow<List<LikeOperationModel>>

    @Query("delete FROM contentLike " + "WHERE contentId in (:idList)")
    fun deleteLastList(idList: List<String>): Int

    @Query("delete FROM contentLike ")
    fun deleteAllList()

    @Query("delete FROM contentLike " + "WHERE contentId = :contentId")
    fun removeLike(contentId: String)

    @Query("SELECT * FROM contentLike " + "WHERE contentId = :contentId")
    fun getContentSingleLike(contentId: String): LikeOperationModel?

    fun addToLikeList(contentLike: LikeOperationModel) {
        insert(contentLike)
    }

    @Transaction
    fun clearLastListTable() {
        var list = getContentLikeList()
        if (list.isNullOrEmpty()) {
            return
        }
        deleteLastList(list.map { it.contentId })
    }

    @Transaction
    fun insertLastList(list: List<LikeOperationModel>) {
        deleteAllList()
        insert(list)
    }
}