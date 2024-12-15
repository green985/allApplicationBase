package com.oyetech.domain.useCases.helpers

import com.oyetech.core.utils.SingleLiveEvent
import com.oyetech.domain.repository.helpers.AppReviewControllerRepository
import com.oyetech.domain.repository.helpers.AppReviewRepository
import com.oyetech.domain.repository.helpers.AppReviewResultRepository

/**
Created by Erdi Özbek
-5.10.2022-
-17:30-
 **/

class AppReviewOperationUseCase(
    private var reviewRepository: AppReviewRepository,
    private var reviewControllerRepository: AppReviewControllerRepository
) {

    fun startAppReviewOperation(repository: AppReviewResultRepository) {
        reviewRepository.startAppReviewOperation(repository)
    }

    fun fakeStartAppReviewOperation(repository: AppReviewResultRepository) {
        reviewRepository.fakeStartAppReviewOperation(repository)
    }

    fun controlReviewCanShow() {
        reviewControllerRepository.controlReviewCanShow()
    }

    fun getReviewCanShowSingleLiveEvent(): SingleLiveEvent<Boolean> {
        return reviewControllerRepository.getReviewCanShowSingleLiveEvent()
    }
}
