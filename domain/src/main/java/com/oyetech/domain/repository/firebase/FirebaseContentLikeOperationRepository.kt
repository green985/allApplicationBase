package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import com.oyetech.models.newPackages.helpers.OperationState
import kotlinx.coroutines.flow.Flow

interface FirebaseContentLikeOperationRepository {
    fun getLikeListWithContentId(contentId: String): Flow<List<LikeOperationModel>>

    @Suppress("TooGenericExceptionThrown")
    fun likeOperation(contentId: String): Flow<OperationState<LikeOperationModel>>
    fun getLikeCount(contentId: String): Flow<Int>
}
