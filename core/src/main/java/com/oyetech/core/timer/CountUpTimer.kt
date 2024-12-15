package com.oyetech.core.timer

import android.os.CountDownTimer

abstract class CountUpTimer(private val secondsInFuture: Int, countUpIntervalMilis: Long) :
    CountDownTimer(secondsInFuture.toLong() * 1000, countUpIntervalMilis) {

    abstract fun onCount(count: Long)

    override fun onTick(msUntilFinished: Long) {
        onCount(((secondsInFuture.toLong() * 1000 - msUntilFinished)))
    }
}
