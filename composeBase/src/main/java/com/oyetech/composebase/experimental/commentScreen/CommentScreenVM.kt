package com.oyetech.composebase.experimental.commentScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseList.BaseListViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.experimental.commentScreen.CommentScreenEvent.AddComment
import com.oyetech.composebase.experimental.commentScreen.CommentScreenEvent.OnCommentChanged
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.commentModel.CommentResponseData
import com.oyetech.models.utils.helper.TimeFunctions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

/**
Created by Erdi Özbek
-20.12.2024-
-21:50-
 **/

class CommentScreenVM(
    appDispatchers: AppDispatchers,
    private val commentOperationRepository: FirebaseCommentOperationRepository,
) :
    BaseListViewModel<CommentItemUiState>(appDispatchers) {
    override val complexItemViewState: MutableStateFlow<ComplexItemListState<CommentItemUiState>> =
        MutableStateFlow(ComplexItemListState(errorMessage = errorMessage))

    val toolbarState = mutableStateOf(
        RadioToolbarState(
            title = "Comment Screen"
        )
    )

    val commentScreenUiState = MutableStateFlow(CommentItemUiState())

    init {
        popilateCommentScreenList()
    }

    fun onEvent(event: CommentScreenEvent) {
        when (event) {
            is AddComment -> addComment()
            is OnCommentChanged -> {
                commentScreenUiState.value =
                    commentScreenUiState.value.copy(commentContent = event.content)
            }
        }
    }

    fun addComment() {
        val content = commentScreenUiState.value.commentContent

        viewModelScope.launch(getDispatcherIo()) {
            commentOperationRepository.addCommentFlow("firstComment", content).asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = {
                            Timber.d("Comment Added ==" + it.toString())
                        },
                        onFailure = {
                            Timber.d("Comment fail ==" + it.toString())

                        }
                    )
                }
        }

        //commentOperationRepository.addComment("firstComment", content)

        popilateCommentScreenList()
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

private fun List<CommentResponseData>.mapToResponse(): ImmutableList<CommentItemUiState> {
    return this.map {
        if (it.createdAt != null) {
            val createdTimeString = TimeFunctions.getDateFromLongWithHour(it.createdAt!!.time)
            CommentItemUiState(
                commentContent = it.content,
                createdAtString = createdTimeString,
                createdAt = it.createdAt ?: Date(),
            )

        } else {
            null
        }
    }.filterNotNull().toImmutableList()


}
