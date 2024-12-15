package com.oyetech.analyticsmodule.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import timber.log.Timber

/**
Created by Erdi Özbek
-4.10.2022-
-15:16-
 **/

class FirebaseAnalyticsHelper(private var firebaseAnalytics: FirebaseAnalytics) {

    companion object {
        var analyticsHelper: FirebaseAnalyticsHelper? = null
    }

    init {
        Timber.d("FirebaseAnalyticsHelper intittttt")
    }

    fun logEventWithKey(analyticsKey: String) {
        Timber.d("logggg")

        firebaseAnalytics.logEvent(analyticsKey, null)
    }

    fun logEventWithKeyAndData(analyticsKey: String, subKey: String) {
        Timber.d("logggg")

        firebaseAnalytics.logEvent(analyticsKey) {
            this.param(FirebaseAnalytics.Param.CONTENT_TYPE, subKey)
        }
    }
}
