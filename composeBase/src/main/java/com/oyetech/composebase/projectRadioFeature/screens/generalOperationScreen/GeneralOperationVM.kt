package com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.domain.useCases.helpers.AppReviewOperationUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-15.12.2024-
-14:05-
 **/

class GeneralOperationVM(
    appDispatchers: AppDispatchers,
    private val appReviewOperationUseCase: AppReviewOperationUseCase,
    private val sharedHelperRepository: SharedHelperRepository,
) : BaseViewModel(appDispatchers) {

    fun getReviewCanShowState() = appReviewOperationUseCase.getReviewCanShowState()

    fun getReviewOperationStatus() = appReviewOperationUseCase.getReviewOperationStateFlow()
    fun dismissReviewState() {
        getReviewCanShowState().value = false
        sharedHelperRepository.setReviewAlreadyShown(true)
    }

    fun startReviewOperation() {
        getReviewCanShowState().value = false
        appReviewOperationUseCase.fakeStartAppReviewOperation()
    }

    init {
        sharedHelperRepository.increaseAppOpenCount()
        viewModelScope.launch(getDispatcherIo()) {
            delay(1000)
            appReviewOperationUseCase.controlReviewCanShow()
        }
    }


}