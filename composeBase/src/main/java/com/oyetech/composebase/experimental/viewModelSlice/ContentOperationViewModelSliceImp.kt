package com.oyetech.composebase.experimental.viewModelSlice

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent.LikeContent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationUiState
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteUiState
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.models.newPackages.helpers.dataOrNull
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.Dispatchers
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

    override fun initContentOperationState(
        quoteId: String,
        contentOperationUiState: MutableStateFlow<ContentOperationUiState>,
        uiState: MutableStateFlow<QuoteUiState>,
    ) {
        this.contentOperationUiState = contentOperationUiState
        this.contentOperationUiState.updateState {
            ContentOperationUiState(contentId = quoteId)
        }
        updateLoading = { isLoading ->
            uiState.updateState {
                copy(isLoading = isLoading)
            }
        }
        updateErrorText = { errorText ->
            uiState.updateState {
                copy(errorMessage = errorText)
            }
        }
    }

    context(BaseViewModel)
    override fun onContentEvent(event: ContentOperationEvent) {
        Timber.d("onContentEvent: $event")
        when (event) {
            is LikeContent -> {
                updateLoading(true)
                viewModelScope.launch(Dispatchers.IO) {
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