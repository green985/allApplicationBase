package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import kotlinx.coroutines.flow.Flow

interface FirebaseContentLikeOperationRepository {
    fun getLikeListWithContentId(contentId: String): Flow<List<LikeOperationModel>>

    @Suppress("TooGenericExceptionThrown")
    suspend fun likeOperation(contentId: String): Flow<LikeOperationModel>
    fun getLikeCount(contentId: String): Flow<Int>

    suspend fun getInitialStateOfContent(contentId: String): Flow<LikeOperationModel>
}
