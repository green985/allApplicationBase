package com.oyetech.domain.repository.contentOperation

import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import kotlinx.coroutines.flow.Flow

interface ContentOperationLocalRepository {
    fun getContentLikeList(): List<LikeOperationModel>?
    fun getContentLikeListFlow(contentIdList: List<String>): Flow<List<LikeOperationModel>>
    suspend fun removeLike(contentId: String)
    suspend fun deleteAllList()
    suspend fun deleteLastList(idList: List<String>)
    suspend fun getContentSingleLike(contentId: String): LikeOperationModel?
    suspend fun addToLikeList(contentLike: LikeOperationModel)
}