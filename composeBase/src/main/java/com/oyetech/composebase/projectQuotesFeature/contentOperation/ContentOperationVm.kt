package com.oyetech.composebase.projectQuotesFeature.contentOperation

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent.LikeContent
import com.oyetech.domain.repository.contentOperation.ContentOperationLocalRepository
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val contentOperationLocalRepository: ContentOperationLocalRepository,
) : BaseViewModel(appDispatchers) {

    private val contentOperationStateMap =
        HashMap<String, MutableStateFlow<ContentOperationUiState>>()
    private val contentOperationJobs = HashMap<String, Job>()

    private var contentIdListInitJob: Job? = null

    fun getContentStateFlow(contentId: String): MutableStateFlow<ContentOperationUiState> {
        return contentOperationStateMap.getOrPut(contentId) {
            MutableStateFlow(ContentOperationUiState(contentId = contentId))
        }
    }

    fun initContentOperationState(contentIdList: List<String>, inList: Boolean = false) {
        val clearedIdList = contentIdList.filter { it.isNotEmpty() }
        if (clearedIdList.isEmpty()) return

        if (inList) {
            initContentOperationStateWithLocal(clearedIdList)
        } else {
            initContentOperationStateWithLocal(clearedIdList)
            clearedIdList.forEach { initContentOperationSingle(it) }
        }
    }

    private fun initContentOperationSingle(contentId: String) {
        val stateFlow = getContentStateFlow(contentId)

//        stateFlow.updateState { copy(isInitialed = false) }

        contentOperationJobs[contentId]?.cancel()
        contentOperationJobs[contentId] = viewModelScope.launch(getDispatcherIo()) {
            firebaseContentLikeOperationRepository.getInitialStateOfContent(contentId)
                .map { ContentOperationUiState(contentId = contentId, isLiked = it.like) }
                .asResult()
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            stateFlow.value = it.copy(isInitialed = true)
                        },
                        onFailure = {
                            snackbarDelegate.triggerSnackbarState(ErrorHelper.getErrorMessage(it))
                            // optional...
                            stateFlow.updateState { copy(errorText = ErrorHelper.getErrorMessage(it)) }
                        }
                    )
                }
        }

        Timber.d("Initialized content operation for: $contentId")
    }

    private fun initContentOperationStateWithLocal(contentIdList: List<String>) {
        contentIdListInitJob?.cancel()
        contentIdListInitJob = viewModelScope.launch(getDispatcherIo()) {
            contentOperationLocalRepository.getContentLikeListFlow(contentIdList).asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = { list ->
                            updateContentStates(list)
                        },
                        onFailure = {
                            snackbarDelegate.triggerSnackbarState(ErrorHelper.getErrorMessage(it))
                        }
                    )
                }
        }
        Timber.d("Initialized content operation for: ${contentIdList.size} items")

    }

    private fun updateContentStates(list: List<LikeOperationModel>) {
        viewModelScope.launch(getDispatcherIo()) {
            delay(50)
            list.map {
                getContentStateFlow(it.contentId).updateState { copy(isLiked = it.like) }
            }
        }
    }

    fun onContentEvent(event: ContentOperationEvent) {
        Timber.d("onContentEvent: $event")
        when (event) {
            is LikeContent -> {
                val stateFlow = getContentStateFlow(event.contentId)

                stateFlow.updateState { copy(isLoading = true) }
                viewModelScope.launch(getDispatcherIo()) {
                    firebaseContentLikeOperationRepository.likeOperation(event.contentId)
                        .asResult()
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = {
                                    contentOperationLocalRepository.addToLikeList(it)
                                    stateFlow.updateState {
                                        copy(
                                            isLiked = it.like,
                                            isLoading = false
                                        )
                                    }
                                },
                                onFailure = {
                                    snackbarDelegate.triggerSnackbarState(
                                        ErrorHelper.getErrorMessage(
                                            it
                                        )
                                    )
                                    stateFlow.updateState {
                                        copy(
                                            errorText = ErrorHelper.getErrorMessage(it),
                                            isLoading = false
                                        )
                                    }
                                }
                            )
                        }
                }
            }
        }
    }

}