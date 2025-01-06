package com.oyetech.composebase.experimental.commentWidget

import com.oyetech.models.newPackages.helpers.OperationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.Date

/**
Created by Erdi Özbek
-20.12.2024-
-21:51-
 **/

data class CommentScreenUiState(
    val contentId: String = "",
    val commentInput: String = "",
    val addCommentState: OperationState<Boolean> = OperationState.Idle,
    val isListEmpty: Boolean = false,

    val commentList: ImmutableList<CommentItemUiState> = persistentListOf(),
    val isOptionsPopupShow: Boolean = false,

    val errorMessage: String = "",
)

data class CommentItemUiState(
    val commentId: String = "",
    val commentContent: String = "",
    val createdAt: Date = Date(),
    val createdAtString: String = "",
    val username: String = "",
    val isMine: Boolean = false,
    val isDeleted: Boolean = false,
)

sealed class CommentScreenEvent {
    data class OnCommentInputChanged(val commentInput: String) : CommentScreenEvent()
    data class CommentOperationClicked(val commentId: String) : CommentScreenEvent()

    object OnCommentSubmit : CommentScreenEvent()

}

sealed class CommentOptionsEvent : CommentScreenEvent() {
    data class AddComment(val commentInput: String) : CommentScreenEvent()
    object AddCommentSuccess : CommentOptionsEvent()
    data class DeleteComment(val commentId: String) : CommentOptionsEvent()
    data class ReportComment(val commentId: String) : CommentOptionsEvent()
}