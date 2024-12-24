package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.commentModel.CommentResponseData
import kotlinx.coroutines.flow.Flow

interface FirebaseCommentOperationRepository {

    suspend fun getCommentsWithId(commentId: String): Flow<List<CommentResponseData>>
    fun addComment(contentId: String, comment: String)
}
