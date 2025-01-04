package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.commentModel.CommentResponseData
import com.oyetech.models.newPackages.helpers.OperationState
import kotlinx.coroutines.flow.Flow

interface FirebaseCommentOperationRepository {

    suspend fun getCommentsWithId(commentId: String): Flow<List<CommentResponseData>>
    fun addCommentFlow(contentId: String, content: String): Flow<OperationState<Boolean>>

    fun reportComment(commentId: String): Flow<OperationState<Unit>>
    fun deleteComment(contentId: String, commentId: String): Flow<OperationState<Unit>>
}
