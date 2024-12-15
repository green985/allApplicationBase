package com.oyetech.domain.useCases

import com.oyetech.domain.repository.AnalyticsRepository
import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.utils.moshi.serialize
import timber.log.Timber

/**
Created by Erdi Özbek
-5.10.2022-
-15:54-
 **/

class AnalyticsOperationUseCase(private var analyticsRepository: AnalyticsRepository) {

    fun makeThrowableWithMessageDetail(messageDetailString: MessageDetailDataResponse?) {
        Timber.d("makeThrowableWithMessageDetail added...")
        var stringg = messageDetailString.serialize()
        analyticsRepository.makeThrowableWithMessageDetail(stringg)
    }

    fun makeThrowableWithExceptions(exception: Exception?) {
        Timber.d("makeThrowableWithMessageDetail added...")
        var stringg = exception.toString()
        analyticsRepository.makeThrowableWithMessageDetail(stringg)
    }

    fun makeThrowableWithExceptions(exception: String?) {
        Timber.d("makeThrowableWithMessageDetail added...")
        var stringg = exception.toString()
        analyticsRepository.makeThrowableWithString(stringg)
    }

    fun logEventWithKey(key: String) {
        analyticsRepository.logEventWithKey(key)
    }
}
