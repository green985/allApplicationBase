package com.oyetech.composebase.experimental.commentScreen

import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import com.oyetech.models.newPackages.helpers.OperationState
import java.util.Date

/**
Created by Erdi Özbek
-20.12.2024-
-21:51-
 **/

data class CommentScreenUiState(
    val commentInput: String = "",
    val addCommentState: OperationState<Boolean> = OperationState.Idle,
    val userState: FirebaseUserProfileModel = FirebaseUserProfileModel(),

    )

data class CommentItemUiState(
    val id: String = "",
    val commentContent: String = "",
    val createdAt: Date = Date(),
    val createdAtString: String = "",
    val username: String = "",
    val isMine: Boolean = false,
)

sealed class CommentScreenEvent {
    data class OnCommentChanged(val content: String) : CommentScreenEvent()
    data class OnCommentSubmit(val commentInput: String) : CommentScreenEvent()

}

sealed class CommentOptionsEvent : CommentScreenEvent() {
    data class AddComment(val commentInput: String) : CommentScreenEvent()
    data class DeleteComment(val commentId: String) : CommentOptionsEvent()
    data class RepostComment(val commentId: String) : CommentOptionsEvent()
}