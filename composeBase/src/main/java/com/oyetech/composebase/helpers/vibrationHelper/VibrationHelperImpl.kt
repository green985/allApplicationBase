package com.oyetech.composebase.helpers.vibrationHelper

import android.annotation.SuppressLint
import android.content.Context

/**
Created by Erdi Özbek
-2.11.2024-
-17:24-
 **/

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission", "NewApi")
class VibrationHelperImpl(private val context: Context) : IVibrationHelper {

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private var vibrationJob: Job? = null

    var vibrationIntensity = 50

    override var currentVibrationType: VibrationType? = null

    override fun vibrate(vibrationType: VibrationType): Boolean {
        if (!vibrator.hasVibrator()) {
            currentVibrationType = null
            return false
        }

        val vibrationEffect = VibrationEffect.createWaveform(
            vibrationType.pattern,
            getVibrationAmplitudesPatternWithIntensity(vibrationType),
            0,
        ) // 0 means repeat indefinitely

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrator.vibrate(500L)
        } else {
            vibrator.vibrate(vibrationEffect)
        }

        // Start a coroutine to stop vibration after 60 seconds
        vibrationJob?.cancel() // Cancel any existing job to avoid overlap
        vibrationJob = CoroutineScope(Dispatchers.Main).launch {
            delay(60 * 1000L) // 60 seconds
            stopVibration()
        }
        currentVibrationType = vibrationType

        return true
    }

    override fun stopVibration() {
//        vibrator.cancel()
        currentVibrationType = null
        vibrationJob?.cancel() // Cancel the 60-second coroutine if stopped manually
    }

    override fun setVibrationIntensityy(value: Int) {
        vibrationIntensity = value
    }

    private fun getVibrationAmplitudesPatternWithIntensity(vibrationType: VibrationType): IntArray? {
        val intensity = if (vibrationIntensity == 0) 1 else vibrationIntensity
        val amplitudes = vibrationType.pattern.map {
            (intensity * 2.5).toInt()
        }

        return amplitudes.toIntArray()

    }
}
