package com.oyetech.analyticsmodule.analytics

import android.content.Context
import android.content.ContextWrapper
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.oyetech.domain.helper.isDebug
import com.oyetech.domain.repository.AnalyticsRepository
import timber.log.Timber

/**
Created by Erdi Özbek
-5.10.2022-
-16:11-
 **/

class AnalyticsRepositoryImp(private var context: Context) : AnalyticsRepository {

    override fun makeThrowableWithMessageDetail(messageDetailString: String) {
        if (context.isDebug()) {
            return
        }
        FirebaseCrashlytics.getInstance().apply {
            recordException(Exception(messageDetailString))
        }
    }

    override fun makeThrowableWithString(purchaseString: String) {
        if (context.isDebug()) {
            return
        }
        FirebaseCrashlytics.getInstance().apply {
            recordException(Exception(purchaseString))
        }
    }

    override fun logEventWithKey(analyticsKey: String) {
        Timber.d("logEventWithKey == " + analyticsKey)

        if (context.isDebug()) {
            return
        }

        FirebaseAnalyticsHelper.analyticsHelper?.logEventWithKey(analyticsKey)
    }
}
