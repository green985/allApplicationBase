package com.oyetech.helper.appReviewHelper

import android.app.Activity
import com.oyetech.base.BaseFragment
import com.oyetech.cripto.analyticsKeys.AnalyticsKeys
import com.oyetech.domain.repository.helpers.AppReviewResultRepository
import timber.log.Timber

/**
Created by Erdi Özbek
-5.10.2022-
-18:49-
 **/

fun BaseFragment<*, *>.getAppReviewActivityRepository(): AppReviewResultRepository {
    var repository = object : AppReviewResultRepository {

        override fun onCompleteAppReviewOperation() {
            // Timber.d("appREviewwww onCompleteAppReviewOperation== ")
            viewModel.sharedPrefRepositoryHelper.setReviewAlreadyShown(true)
            viewModel.logEventWithKey(AnalyticsKeys.reviewCompleteSection)
        }

        override fun onErrorAppReviewOperation(exception: Exception?) {
            Timber.d("appREviewwww onErrorAppReviewOperation== " + exception.toString())
            exception?.printStackTrace()
            viewModel.logEventWithKey(AnalyticsKeys.reviewErrorSection)
        }

        override fun onStartAppReviewOperation() {
            Timber.d("appREviewwww == onStartAppReviewOperation")
            viewModel.logEventWithKey(AnalyticsKeys.reviewOnStartSection)
        }

        override fun getActivityy(): Activity? {
            return requireActivity()
        }
    }
    return repository
}
