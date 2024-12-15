package com.oyetech.audioPlayerHelper.services

import android.content.Intent
import android.content.Intent.ACTION_MEDIA_BUTTON
import android.os.CountDownTimer
import androidx.media.session.MediaButtonReceiver
import com.oyetech.audioPlayerHelper.serviceUtils.PlayerServiceUtils
import com.oyetech.audioPlayerHelper.serviceUtils.ServiceConst
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.scope.serviceScope
import org.koin.core.scope.Scope
import timber.log.Timber

/**
Created by Erdi Özbek
-17.11.2022-
-22:14-
 **/

@OptIn(InternalCoroutinesApi::class)
class PlayerService : PlayerServiceHelper() {

    override val scope: Scope by serviceScope()

    private var timer: CountDownTimer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        PlayerServiceUtils.bindService()

        Timber.d("service onStartCommand calledd.")
        if (intent != null) {
            var action = intent.action
            if (action != null) {
                var returnCode = handleOnStartCommandIntent(intent)
                if (returnCode != ServiceConst.SERVICE_RETURN_NOT_IMPORTANT) {
                    return returnCode
                }
            }

            MediaButtonReceiver.handleIntent(mediaSessionHelper.getMediaSession(), intent)

        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleOnStartCommandIntent(intent: Intent): Int {
        val action = intent.action
        when (action) {
            ServiceConst.ACTION_SKIP_TO_PREVIOUS -> {
                previous()
            }

            ServiceConst.ACTION_SKIP_TO_NEXT -> {
                next()
            }

            ServiceConst.ACTION_PAUSE -> {
                pause()
            }

            ServiceConst.ACTION_RESUME -> {
                resume()
            }

            ServiceConst.ACTION_STOP -> {
                Timber.d("action stopppp")
                stopService()
                return START_NOT_STICKY
            }

            ACTION_MEDIA_BUTTON -> {
                actionMediaButton(intent)
            }
        }

        return ServiceConst.SERVICE_RETURN_NOT_IMPORTANT
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("service onCreate calledd.")

        prepareServiceInit()

        observeRadioStateFlowData()
        observeRadioPauseReasonMutableStateFlow()
    }

    private fun observeRadioStateFlowData() {
        radioOperationJobs?.cancel()
        radioOperationJobs = corutinesScope.launch {
            if (!currentCoroutineContext().isActive) {
                Timber.d("jobs cannecled")
                return@launch
            }

            var subsCount =
                contentOperationUseCase.contentStateViewMutableStateFlow.subscriptionCount.value
            Timber.d("subssss == " + subsCount)

            contentOperationUseCase.observeContentViewState()
                .collect(getRadioOperationServiceCollector())

        }
    }

    private fun observeRadioPauseReasonMutableStateFlow() {
        radioPauseReasonOperationJobs?.cancel()
        radioPauseReasonOperationJobs = GlobalScope.launch {
            if (!currentCoroutineContext().isActive) {
                Timber.d("jobs cannecled")
                return@launch
            }
            contentOperationUseCase.contentPauseReasonMutableStateFlow.asStateFlow()
                .collect(getRadioPauseReasonServiceCollector())
        }
    }


}