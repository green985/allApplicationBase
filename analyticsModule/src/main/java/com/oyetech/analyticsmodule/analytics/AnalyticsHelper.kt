package com.oyetech.analyticsmodule.analytics

import com.oyetech.analyticsmodule.analytics.FirebaseAnalyticsHelper.Companion.analyticsHelper
import timber.log.Timber

object AnalyticsHelper {

    fun logEventWithKey(analyticsKey: String) {
        Timber.d("logggg")

        analyticsHelper?.logEventWithKey(analyticsKey)
    }

    fun logEventWithKeyAndData(analyticsKey: String, subKey: String) {
        Timber.d("logggg")

        analyticsHelper?.logEventWithKeyAndData(analyticsKey, subKey)
    }
}
