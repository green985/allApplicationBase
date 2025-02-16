package com.oyetech.composebase.projectQuotesFeature.contentOperation

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent.LikeContent
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.models.newPackages.helpers.dataOrNull
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-16.02.2025-
-13:55-
 **/

class ContentOperationVm(
    appDispatchers: AppDispatchers,
    private val firebaseContentLikeOperationRepository: FirebaseContentLikeOperationRepository,
    private val snackbarDelegate: SnackbarDelegate,
) : BaseViewModel(appDispatchers) {

    var contentOperationUiState: MutableStateFlow<ContentOperationUiState> = MutableStateFlow(
        ContentOperationUiState()
    )

    var contentOperationJob: Job? = null

    fun initContentOperationState(
        contentId: String,
    ) {
        contentOperationUiState.updateState {
            copy(
                isInitialed = false
            )
        }
        contentOperationJob?.cancel()
        contentOperationJob = viewModelScope.launch(getDispatcherIo()) {
            firebaseContentLikeOperationRepository.getInitialStateOfContent(contentId).map {
                ContentOperationUiState(
                    contentId = contentId,
                    isLiked = it.like
                )
            }.asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = {
                            contentOperationUiState.value = it
                            contentOperationUiState.updateState {
                                copy(
                                    isInitialed = true
                                )
                            }
                        },
                        onFailure = {
                            // will be handled
                        }
                    )
                }
        }


        Timber.d("init contentttt " + contentId)

    }

    fun onContentEvent(event: ContentOperationEvent) {
        Timber.d("onContentEvent: $event")
        when (event) {
            is LikeContent -> {
                contentOperationUiState.updateState {
                    copy(isLoading = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    firebaseContentLikeOperationRepository.likeOperation(event.contentId)
                        .asResult()
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = {
                                    contentOperationUiState.updateState {
                                        copy(isLiked = it.dataOrNull()?.like ?: false)
                                    }
                                    contentOperationUiState.updateState {
                                        copy(isLoading = false)
                                    }

                                },
                                onFailure = {
                                    snackbarDelegate.triggerSnackbarState(
                                        ErrorHelper.getErrorMessage(
                                            it
                                        )
                                    )
                                    contentOperationUiState.updateState {
                                        copy(
                                            errorText = ErrorHelper.getErrorMessage(
                                                it
                                            )
                                        )
                                    }
                                    contentOperationUiState.updateState {
                                        copy(isLoading = true)
                                    }
                                }
                            )
                        }
                }
            }

        }
    }

}