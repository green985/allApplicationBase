package com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.domain.useCases.helpers.AppReviewOperationUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-15.12.2024-
-14:05-
 **/

class GeneralOperationVM(
    appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    private val appReviewOperationUseCase: AppReviewOperationUseCase,
    private val sharedHelperRepository: SharedOperationRepository,
    private val googleLoginRepository: GoogleLoginRepository,
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

    fun signInWithGoogleAnonymous() {
        viewModelScope.launch(getDispatcherIo()) {
            googleLoginRepository.signInWithGoogleAnonymous()
        }
    }


    init {
        sharedHelperRepository.increaseAppOpenCount()
        viewModelScope.launch(getDispatcherIo()) {
            delay(1000)
            appReviewOperationUseCase.controlReviewCanShow()
        }
    }


}