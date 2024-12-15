package com.oyetech.domain.repository.helpers

import android.app.Activity
import com.oyetech.models.enums.ReviewStatus
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-5.10.2022-
-17:24-
 **/

interface AppReviewRepository {

    fun startAppReviewOperation()
    fun fakeStartAppReviewOperation()

    fun getReviewStatusState(): MutableStateFlow<ReviewStatus>
}

interface AppReviewControllerRepository {

    fun controlReviewCanShow()
    fun getReviewCanShowState(): MutableStateFlow<Boolean>
}

interface AppReviewResultRepository {
    fun onCompleteAppReviewOperation()
    fun onErrorAppReviewOperation(exception: Exception?)
    fun onStartAppReviewOperation()

    fun getActivityy(): Activity?
}
