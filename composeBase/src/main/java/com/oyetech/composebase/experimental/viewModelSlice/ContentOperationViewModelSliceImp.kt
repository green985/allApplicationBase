package com.oyetech.composebase.experimental.viewModelSlice

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent.LikeContent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationUiState
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.models.newPackages.helpers.dataOrNull
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-13.02.2025-
-23:48-
 **/

class ContentOperationViewModelSliceImp(private val firebaseContentLikeOperationRepository: FirebaseContentLikeOperationRepository) :
    ContentOperationViewModelSlice {

    private lateinit var contentOperationUiState: MutableStateFlow<ContentOperationUiState>

    override fun getContentOperationUiState(contentId: String): MutableStateFlow<ContentOperationUiState> {
        if (!::contentOperationUiState.isInitialized) {
            contentOperationUiState =
                MutableStateFlow(ContentOperationUiState(contentId = contentId))
        }
        return contentOperationUiState
    }

    context(BaseViewModel)
    override fun onContentEvent(event: ContentOperationEvent) {
        Timber.d("onContentEvent: $event")
        when (event) {
            is LikeContent -> {
                viewModelScope.launch(getDispatcherIo()) {
                    firebaseContentLikeOperationRepository.likeOperation(event.contentId).asResult()
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = {
                                    contentOperationUiState.value =
                                        contentOperationUiState.value.copy(
                                            isLiked = it.dataOrNull()?.isLiked ?: false
                                        )
                                },
                                onFailure = {
                                    contentOperationUiState.value =
                                        contentOperationUiState.value.copy(
                                            errorText = ErrorHelper.getErrorMessage(
                                                it
                                            )
                                        )
                                }
                            )
                        }
                }
            }

        }
    }

}