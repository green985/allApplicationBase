package com.oyetech.domain.useCases

import android.os.CountDownTimer
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.radioModels.PauseReason.USER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

/**
Created by Erdi Özbek
-29.12.2022-
-23:10-
 **/

class TimerOperationUseCase(private var radioOperationUseCase: RadioOperationUseCase) {

    private var timer: CountDownTimer? = null

    var timerCountStateFlow =
        MutableStateFlow(0L)

    fun setTimerMin(timerMin: Int) {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }

        var seconds = (timerMin * 60).toLong()

        timer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                seconds = millisUntilFinished / 1000

                Timber.d("secondssss" + seconds)
                timerCountStateFlow.value = seconds
            }

            override fun onFinish() {
                stop()
                timer = null
            }
        }.start()
    }

    fun cancelTimer() {
        timer?.cancel()
        timerCountStateFlow.value = 0L
    }

    fun getTimerCountFlow(): StateFlow<Long> {
        return timerCountStateFlow.asStateFlow()
    }

    private fun stop() {
        timerCountStateFlow.value = 0L
        radioOperationUseCase.pausePlayer(USER)
    }

}