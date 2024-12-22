package com.oyetech.composebase.experimental.commentScreen

/**
Created by Erdi Özbek
-20.12.2024-
-21:51-
 **/

data class CommentScreenUiState(
    val id: String = "",
    val inputContent: String = "",
)

sealed class CommentScreenEvent {
    data class UpdateContent(val content: String) : CommentScreenEvent()
    object AddComment : CommentScreenEvent()
}