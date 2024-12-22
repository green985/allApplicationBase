package com.oyetech.composebase.experimental.commentScreen

import androidx.compose.runtime.mutableStateOf
import com.oyetech.composebase.base.baseList.BaseListViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.experimental.commentScreen.CommentScreenEvent.AddComment
import com.oyetech.composebase.experimental.commentScreen.CommentScreenEvent.UpdateContent
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-20.12.2024-
-21:50-
 **/

class CommentScreenVM(
    appDispatchers: AppDispatchers,
    private val commentOperationRepository: FirebaseCommentOperationRepository,
) :
    BaseListViewModel<CommentScreenUiState>(appDispatchers) {
    override val complexItemViewState: MutableStateFlow<ComplexItemListState<CommentScreenUiState>> =
        MutableStateFlow(ComplexItemListState(errorMessage = errorMessage))

    val toolbarState = mutableStateOf(
        RadioToolbarState(
            title = "Comment Screen"
        )
    )

    val commentScreenUiState = MutableStateFlow(CommentScreenUiState())

    init {

    }

    fun onEvent(event: CommentScreenEvent) {
        when (event) {
            is AddComment -> addComment()
            is UpdateContent -> {
                commentScreenUiState.value =
                    commentScreenUiState.value.copy(inputContent = event.content)
            }
        }
    }

    fun addComment() {
        val content = commentScreenUiState.value.inputContent
        commentOperationRepository.addComment("firstComment", content)
    }

    fun popilateCommentScreenList() {
        val commentScreenUiState = CommentScreenUiState("1")
        complexItemViewState.value =
            ComplexItemListState(listOf(commentScreenUiState).toImmutableList())
    }


}