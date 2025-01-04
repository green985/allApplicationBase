package com.oyetech.models.firebaseModels.commentModel

import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Keep
data class CommentWrapper(
    val comments: List<CommentResponseData>,
)

@Keep
data class CommentResponseData(
    val commentId: String = "",
    val contentId: String = "",
    val content: String = "",
    val username: String = "",

    @ServerTimestamp
    val createdAt: Date? = null,
//    val likes: Int,
//    val replies: List<Reply> = listOf()
)

@Keep
data class Reply(
    val id: String,
    val content: String,
    val userId: String,
    val createdAt: Date,
)