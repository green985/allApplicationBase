package com.oyetech.domain.repository.helpers

import android.app.Activity
import com.oyetech.core.utils.SingleLiveEvent

/**
Created by Erdi Özbek
-5.10.2022-
-17:24-
 **/

interface AppReviewRepository {

    fun startAppReviewOperation(resultRepository: AppReviewResultRepository)
    fun fakeStartAppReviewOperation(resultRepository: AppReviewResultRepository)
}

interface AppReviewControllerRepository {

    fun controlReviewCanShow()
    fun getReviewCanShowSingleLiveEvent(): SingleLiveEvent<Boolean>
}

interface AppReviewResultRepository {
    fun onCompleteAppReviewOperation()
    fun onErrorAppReviewOperation(exception: Exception?)
    fun onStartAppReviewOperation()

    fun getActivityy(): Activity?
}
