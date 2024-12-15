package com.oyetech.domain.repository

/**
Created by Erdi Özbek
-5.10.2022-
-16:01-
 **/

interface AnalyticsRepository {

    fun makeThrowableWithMessageDetail(messageDetailString: String)
    fun makeThrowableWithString(purchaseString: String)

    fun logEventWithKey(key: String)
}
