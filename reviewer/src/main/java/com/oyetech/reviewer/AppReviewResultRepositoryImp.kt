package com.oyetech.reviewer

import android.app.Activity
import com.oyetech.cripto.analyticsKeys.AnalyticsKeys
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.AnalyticsRepository
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.helpers.AppReviewResultRepository
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-5.02.2024-
-14:54-
 **/

class AppReviewResultRepositoryImp : AppReviewResultRepository {

    val sharedOperationRepository: SharedOperationRepository by KoinJavaComponent.inject(
        SharedOperationRepository::class.java
    )

    val activityProviderUseCase: ActivityProviderUseCase by KoinJavaComponent.inject(
        ActivityProviderUseCase::class.java
    )

    val analyticsOperationUseCase: AnalyticsRepository by KoinJavaComponent.inject(
        AnalyticsRepository::class.java
    )

    override fun onCompleteAppReviewOperation() {
        Timber.d("appREviewwww onCompleteAppReviewOperation== ")
        sharedOperationRepository.setReviewAlreadyShown(true)
        analyticsOperationUseCase.logEventWithKey(AnalyticsKeys.reviewCompleteSection)
    }

    override fun onErrorAppReviewOperation(exception: Exception?) {
        Timber.d("appREviewwww onErrorAppReviewOperation== " + exception.toString())
        exception?.printStackTrace()
        analyticsOperationUseCase.logEventWithKey(AnalyticsKeys.reviewErrorSection)
    }

    override fun onStartAppReviewOperation() {
        Timber.d("appREviewwww == onStartAppReviewOperation")
        analyticsOperationUseCase.logEventWithKey(AnalyticsKeys.reviewOnStartSection)
    }

    override fun getActivityy(): Activity? {
        return activityProviderUseCase.getCurrentActivity()
    }
}