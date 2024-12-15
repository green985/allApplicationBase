package com.oyetech.exoplayermodule.errorHandling

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidContentTypeException
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy.LoadErrorInfo
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.radioModels.PlayState.Paused
import com.oyetech.models.utils.const.HelperConstant
import timber.log.Timber

class CustomLoadErrorHandlingPolicy(private var radioOperationUseCase: RadioOperationUseCase) :
    DefaultLoadErrorHandlingPolicy(HelperConstant.EXOPLAYER_ERROR_RETRY_COUNT) {
    val sanitizedRetryDelaySettingsMs = 500

    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorInfo): Long {
        var exception = loadErrorInfo.exception
        var count = loadErrorInfo.errorCount


        Timber.d("counttt === " + count)

        if (exception is InvalidContentTypeException) {
            radioOperationUseCase.changeRadioErrorState("error_play_stream")
            return C.TIME_UNSET // Immediately surface error if we cannot play content type
        }

        if (radioOperationUseCase.getPlayerState() == Paused) {
            // paused when try to reconnection
            // abort retry
            Timber.d("aborttt retryyyy")
            return C.TIME_UNSET
        }

        var retryDelay = HelperConstant.EXOPLAYER_ERROR_RETRY_FIRST_TRY_LONG

        if (count > 5) {
            retryDelay = sanitizedRetryDelaySettingsMs * (count - 2)
        }

        Timber.d("retry delay ==== " + retryDelay)
        return retryDelay.toLong()
    }

}