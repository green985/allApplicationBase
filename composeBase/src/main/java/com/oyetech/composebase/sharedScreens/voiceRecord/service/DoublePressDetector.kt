package com.oyetech.composebase.sharedScreens.voiceRecord.service

import android.os.SystemClock

class DoublePressDetector(private val intervalMs: Long = 500L) {
    private var lastUp = 0L
    private var lastDown = 0L

    fun onKey(type: VolumeKeyType, eventTime: Long = SystemClock.uptimeMillis()): Boolean {
        return when (type) {
            VolumeKeyType.Up -> {
                val isDouble = eventTime - lastUp in 1..intervalMs
                lastUp = eventTime
                isDouble
            }

            VolumeKeyType.Down -> {
                val isDouble = eventTime - lastDown in 1..intervalMs
                lastDown = eventTime
                isDouble
            }
        }
    }
}