package com.oyetech.models.firebaseModels.commentModel

import java.util.Date

data class CommentWrapper(
    val contentId: String,
    val createdAt: Date = Date(),
    val userId: String = "green985",
    val comments: List<CommentData>,
)

data class CommentData(
    val contentId: String = "contentId",
    val connectionId: String = "connectionId",
    val content: String,
    val userId: String = "green985",
    val createdAt: Date = Date(),
    val createdAtFieldValue: Any? = null,
//    val likes: Int,
//    val replies: List<Reply> = listOf()
)

data class Reply(
    val id: String,
    val content: String,
    val userId: String,
    val createdAt: Date,
)