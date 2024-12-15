package com.oyetech.core.shakeDetector

import android.content.Context
import com.oyetech.core.serviceUtil.ServiceUtil
import timber.log.Timber

/**
Created by Erdi Özbek
-7.05.2022-
-16:17-
 **/

/**
 * context : ApplicationContext
 */
private fun startShakeDetector(context: Context) {
    var shakeDetector = ShakeDetector(object : ShakeDetector.Listener {
        override fun onShakeDetected() {
            Timber.d("shake calledddddd.")
        }
    })

    shakeDetector.start(ServiceUtil.getSensorManager(context))
}

private fun stopShakeDetector(shakeDetector: ShakeDetector) {
    shakeDetector.stop()
}
