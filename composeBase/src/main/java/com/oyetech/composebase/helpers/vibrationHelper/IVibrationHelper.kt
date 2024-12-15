package com.oyetech.composebase.helpers.vibrationHelper

/**
Created by Erdi Özbek
-2.11.2024-
-17:42-
 **/

interface IVibrationHelper {
    fun vibrate(vibrationType: VibrationType): Boolean
    var currentVibrationType: VibrationType?
    fun stopVibration()
    fun setVibrationIntensityy(value: Int)
}