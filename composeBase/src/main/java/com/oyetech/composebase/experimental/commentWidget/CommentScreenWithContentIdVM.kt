package com.oyetech.composebase.experimental.commentWidget;

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.AddComment
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.AddCommentSuccess
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.DeleteComment
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.ReportComment
import com.oyetech.composebase.experimental.commentWidget.CommentScreenEvent.CommentOperationClicked
import com.oyetech.composebase.experimental.commentWidget.CommentScreenEvent.OnCommentInputChanged
import com.oyetech.composebase.experimental.commentWidget.CommentScreenEvent.OnCommentSubmit
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.newPackages.helpers.isSuccess
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-29.12.2024-
-22:12-
 **/

class CommentScreenWithContentIdVM(
    appDispatchers: AppDispatchers,
    private val contentId: String,
    private val userRepository: FirebaseUserRepository,
    private val commentOperationRepository: FirebaseCommentOperationRepository,
) :
    BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(CommentScreenUiState(contentId = contentId))

    val itemTriggerSingle = MutableStateFlow<List<CommentOptionsEvent>>(emptyList())

    var commentPageState =
        Pager(
            config = PagingConfig(
                pageSize = 1,
                initialLoadSize = 1,
                enablePlaceholders = false

            ),
            pagingSourceFactory = {
                CommentPagingSource(
                    commentOperationRepository = commentOperationRepository,
                    userRepository = userRepository,
                    commentScreenUiState = uiState
                )
            }
        ).flow.cachedIn(viewModelScope).combineWithUiState(uiState, itemTriggerSingle)

    fun Flow<PagingData<CommentItemUiState>>.combineWithUiState(
        uiState: StateFlow<CommentScreenUiState>,
        itemTrigger: Flow<List<CommentOptionsEvent>>,
    ): Flow<PagingData<CommentItemUiState>> {
        return this.combine(itemTrigger) { pagingData, commentList ->
            if (uiState.value.commentList.isEmpty()) {
                pagingData
            } else {
                PagingData.from(uiState.value.commentList.toList())
            }
        }
    }

    fun refreshCommentSection() {
        uiState.updateState {
            copy(commentList = persistentListOf())
        }
        commentPageState = Pager(
            config = PagingConfig(
                pageSize = 1,
                initialLoadSize = 1,
                enablePlaceholders = false

            ),
            pagingSourceFactory = {
                CommentPagingSource(
                    commentOperationRepository = commentOperationRepository,
                    userRepository = userRepository,
                    commentScreenUiState = uiState
                )
            }
        ).flow.cachedIn(viewModelScope).combineWithUiState(uiState, itemTriggerSingle)
    }

    fun onEvent(event: CommentScreenEvent) {
        when (event) {
            is AddComment -> {
                val commentInput = event.commentInput
                if (commentInput.isBlank()) return
                viewModelScope.launch(getDispatcherIo()) {
                    commentOperationRepository.addCommentFlow(contentId, event.commentInput)
                        .asResult()
                        .collectLatest {
                            it.fold(
                                onSuccess = {
                                    uiState.updateState {
                                        if (it.isSuccess()) {
                                            copy(
                                                addCommentState = it,
                                                commentInput = "",
                                                errorMessage = ""
                                            )
                                        } else {
                                            copy(addCommentState = it)
                                        }
                                    }
                                    if (it.isSuccess()) {
                                        Timber.d("Comment added")
//                                        itemTriggerSingle.value = listOf(AddCommentSuccess)
                                        successCommentAddFunctions()
                                    }
                                },
                                onFailure = {
                                    uiState.updateState {
                                        copy(errorMessage = ErrorHelper.getErrorMessage(it))
                                    }
                                }
                            )
                        }
                }
            }

            is DeleteComment -> {
                deleteComment(event.commentId)
            }

            is ReportComment -> TODO()

            is OnCommentInputChanged -> {
                uiState.updateState {
                    copy(commentInput = event.commentInput)
                }
            }

            is OnCommentSubmit -> {
                onCommentSubmitOperation()
            }

            AddCommentSuccess -> {
                Timber.d("AddCommentSuccess")
            }

            is CommentOperationClicked -> TODO()
        }
    }

    @Suppress("ReturnCount")
    private fun onCommentSubmitOperation() {
        val commentContent = uiState.value.commentInput
        if (commentContent.isBlank()) {
            uiState.updateState {
                copy(errorMessage = LanguageKey.commentCannotBeEmpty)
            }
            return
        }
        if (commentContent.length < 5) {
            uiState.updateState {
                copy(errorMessage = LanguageKey.commentCannotBeTooShort)
            }
            return
        } else if (commentContent.length > 500) {
            uiState.updateState {
                copy(errorMessage = LanguageKey.commentCannotBeTooLong)
            }
            return
        }

        uiState.updateState {
            copy(errorMessage = "")
        }




        onEvent(AddComment(commentContent))
    }

    private fun successCommentAddFunctions() {
        refreshCommentSection()
    }

    private fun deleteComment(commentId: String) {

        viewModelScope.launch(getDispatcherIo()) {
            commentOperationRepository.deleteComment(contentId, commentId).asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = {
                            uiState.updateState {
                                copy(commentList = commentList.map {
                                    if (it.commentId != commentId) {
                                        it
                                    } else {
                                        it.copy(isDeleted = true)
                                    }
                                }.toImmutableList())
                            }
                            itemTriggerSingle.value = listOf(DeleteComment(commentId))
                        },
                        onFailure = {
                            uiState.updateState {
                                copy(errorMessage = ErrorHelper.getErrorMessage(it))
                            }
                        }
                    )
                }
        }
    }


}