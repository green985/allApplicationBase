package com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.sharedScreens.messaging.MessageOperationVM
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserListOperationRepository
import com.oyetech.domain.useCases.helpers.AppReviewOperationUseCase
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-15.12.2024-
-14:05-
 **/

class GeneralOperationVM(
    appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    private val appReviewOperationUseCase: AppReviewOperationUseCase,
    private val sharedHelperRepository: SharedOperationRepository,
    private val firebaseUserListOperationRepository: FirebaseUserListOperationRepository,
    private val messageOperationVM: MessageOperationVM,
) : BaseViewModel(appDispatchers) {

    fun getReviewCanShowState() = appReviewOperationUseCase.getReviewCanShowState()

    fun getReviewOperationStatus() = appReviewOperationUseCase.getReviewOperationStateFlow()

    fun dismissReviewState() {
        getReviewCanShowState().value = false
        sharedHelperRepository.setReviewAlreadyShown(true)
    }

    fun startReviewOperation() {
        getReviewCanShowState().value = false
        appReviewOperationUseCase.startAppReviewOperation()
    }

    fun dismissDialog() {
        getReviewCanShowState().value = false
        sharedHelperRepository.setReviewAlreadyShown(true)
    }

    fun observeRealtimeMessages() {}

    private fun signToUserFeedList() {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseUserListOperationRepository.addUserToUserList().asResult()
                .collectLatest {
                    Timber.d("User added to user list")
                }
        }
    }

    init {
        messageOperationVM.initFun()
        sharedHelperRepository.increaseAppOpenCount()
        viewModelScope.launch(getDispatcherIo()) {
            delay(1000)
            appReviewOperationUseCase.controlReviewCanShow()
        }
        signToUserFeedList()
    }


}