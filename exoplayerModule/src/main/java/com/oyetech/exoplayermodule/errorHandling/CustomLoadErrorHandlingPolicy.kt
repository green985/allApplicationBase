package com.oyetech.exoplayermodule.errorHandling

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidContentTypeException
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy.LoadErrorInfo
import com.oyetech.models.utils.const.HelperConstant
import timber.log.Timber

// TODO: Refactor — RadioOperationUseCase removed; re-implement pause/error state checks via new signal
class CustomLoadErrorHandlingPolicy :
    DefaultLoadErrorHandlingPolicy(HelperConstant.EXOPLAYER_ERROR_RETRY_COUNT) {
    val sanitizedRetryDelaySettingsMs = 500

    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorInfo): Long {
        val exception = loadErrorInfo.exception
        val count = loadErrorInfo.errorCount

        Timber.d("counttt === $count")

        if (exception is InvalidContentTypeException) {
            return C.TIME_UNSET
        }

        var retryDelay = HelperConstant.EXOPLAYER_ERROR_RETRY_FIRST_TRY_LONG

        if (count > 5) {
            retryDelay = sanitizedRetryDelaySettingsMs * (count - 2)
        }

        Timber.d("retry delay ==== $retryDelay")
        return retryDelay.toLong()
    }
}
