package com.oyetech.domain.useCases.helpers

import com.oyetech.domain.repository.helpers.AppReviewControllerRepository
import com.oyetech.domain.repository.helpers.AppReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-5.10.2022-
-17:30-
 **/

class AppReviewOperationUseCase(
    private var reviewRepository: AppReviewRepository,
    private var reviewControllerRepository: AppReviewControllerRepository,
) {

    fun getReviewOperationStateFlow() = reviewRepository.getReviewStatusState()

    fun startAppReviewOperation() {
        reviewRepository.startAppReviewOperation()
    }

    fun fakeStartAppReviewOperation() {
        reviewRepository.fakeStartAppReviewOperation()
    }

    fun controlReviewCanShow() {
        reviewControllerRepository.controlReviewCanShow()
    }

    fun getReviewCanShowState(): MutableStateFlow<Boolean> {
        return reviewControllerRepository.getReviewCanShowState()
    }
}
