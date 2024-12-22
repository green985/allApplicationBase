package com.oyetech.domain.repository.firebase

interface FirebaseCommentOperationRepository {

    fun getCommentsWithId(commentId: String)
    fun addComment(contentId: String, comment: String)
}
