package com.oyetech.composebase.experimental.commentScreen

import java.util.Date

/**
Created by Erdi Özbek
-20.12.2024-
-21:51-
 **/

data class CommentScreenUiState(
    val id: String = "",
    val commentContent: String = "",
    val createdAt: Date = Date(),
    val createdAtString: String = "",
)

sealed class CommentScreenEvent {
    data class UpdateContent(val content: String) : CommentScreenEvent()
    object AddComment : CommentScreenEvent()
}