package com.oyetech.composebase.experimental.commentScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseList.BaseListViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.experimental.commentScreen.CommentScreenEvent.AddComment
import com.oyetech.composebase.experimental.commentScreen.CommentScreenEvent.UpdateContent
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.commentModel.CommentResponseData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        popilateCommentScreenList()
    }

    fun onEvent(event: CommentScreenEvent) {
        when (event) {
            is AddComment -> addComment()
            is UpdateContent -> {
                commentScreenUiState.value =
                    commentScreenUiState.value.copy(commentContent = event.content)
            }
        }
    }

    fun addComment() {
        val content = commentScreenUiState.value.commentContent
        commentOperationRepository.addComment("firstComment", content)
    }

    fun popilateCommentScreenList() {
        viewModelScope.launch(getDispatcherIo()) {
            commentOperationRepository.getCommentsWithId("firstComment").asResult().collectLatest {
                it.fold(
                    onSuccess = {
                        complexItemViewState.value = ComplexItemListState(
                            items = it.mapToResponse(),
                            errorMessage = errorMessage
                        )
                    },
                    onFailure = {
                        complexItemViewState.value = ComplexItemListState(
                            errorMessage = it.message ?: LanguageKey.generalErrorText
                        )
                    }
                )
            }
        }
    }


}

private fun List<CommentResponseData>.mapToResponse(): ImmutableList<CommentScreenUiState> {
    return this.map {
        CommentScreenUiState(
            commentContent = it.content, createdAt = it.createdAt,
        )
    }.toImmutableList()
}
