package com.oyetech.composebase.experimental.viewModelSlice

import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent.LikeContent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationUiState
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.models.newPackages.helpers.dataOrNull
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-13.02.2025-
-23:48-
 **/

class ContentOperationViewModelSliceImp(
    private val firebaseContentLikeOperationRepository: FirebaseContentLikeOperationRepository,
    private val snackbarDelegate: SnackbarDelegate,
) :
    ContentOperationViewModelSlice {

    private lateinit var updateErrorText: (String) -> Unit
    private lateinit var updateLoading: (Boolean) -> Unit
    private lateinit var contentOperationUiState: MutableStateFlow<ContentOperationUiState>

    override fun getContentOperationUiState(
        contentId: String,
        updateLoading: ((Boolean) -> Unit),
        updateErrorText: (String) -> Unit,
    ): MutableStateFlow<ContentOperationUiState> {
        this.updateLoading = updateLoading
        this.updateErrorText = updateErrorText
        if (!::contentOperationUiState.isInitialized) {
            contentOperationUiState =
                MutableStateFlow(ContentOperationUiState(contentId = contentId))
        }
        return contentOperationUiState
    }

    override fun onContentEvent(event: ContentOperationEvent) {
        Timber.d("onContentEvent: $event")
        when (event) {
            is LikeContent -> {
                updateLoading(true)
                GlobalScope.launch() {
                    firebaseContentLikeOperationRepository.likeOperation(event.contentId)
                        .asResult()
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = {
                                    contentOperationUiState.updateState {
                                        copy(isLiked = it.dataOrNull()?.like ?: false)
                                    }
                                    updateLoading(false)

                                },
                                onFailure = {
                                    snackbarDelegate.triggerSnackbarState(
                                        ErrorHelper.getErrorMessage(
                                            it
                                        )
                                    )
                                    updateErrorText(ErrorHelper.getErrorMessage(it))
                                    contentOperationUiState.value =
                                        contentOperationUiState.value.copy(
                                            errorText = ErrorHelper.getErrorMessage(
                                                it
                                            )
                                        )
                                    updateLoading(false)
                                }
                            )
                        }
                }
            }

        }
    }

}