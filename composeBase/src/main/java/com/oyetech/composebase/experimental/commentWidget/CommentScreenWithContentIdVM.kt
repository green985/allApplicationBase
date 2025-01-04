package com.oyetech.composebase.experimental.commentWidget;

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.AddComment
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.DeleteComment
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.ReportComment
import com.oyetech.composebase.experimental.commentWidget.CommentScreenEvent.OnCommentInputChanged
import com.oyetech.composebase.experimental.commentWidget.CommentScreenEvent.OnCommentSubmit
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.newPackages.helpers.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
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

    val itemTrigger = MutableStateFlow(emptyList<CommentItemUiState>())
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
        ).flow.cachedIn(viewModelScope).combine(itemTriggerSingle) { pagingData, commentList ->
            Timber.d("Paging data: =" + commentList)
            pagingData.map {
                val event = commentList.firstOrNull()
                when (event) {
                    is DeleteComment -> {
                        if (it.commentId == event.commentId) {
                            it.copy(isDeleted = true)
                        } else {
                            it
                        }
                    }

                    is ReportComment -> {
                        it
                    }

                    null -> {
                        it
                    }
                }
            }
        }

    fun refreshCommentSection() {
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
        ).flow.cachedIn(viewModelScope)
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
        }
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