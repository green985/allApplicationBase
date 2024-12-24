package com.oyetech.models.firebaseModels.commentModel

import java.util.Date

data class CommentWrapper(
    val comments: List<CommentResponseData>,
)

data class CommentResponseData(
    val contentId: String = "contentId",
    val connectionId: String = "connectionId",
    val content: String = "",
    val userId: String = "green985",
    val createdAt: Date = Date(),
//    val likes: Int,
//    val replies: List<Reply> = listOf()
)

data class Reply(
    val id: String,
    val content: String,
    val userId: String,
    val createdAt: Date,
)