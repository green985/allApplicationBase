package com.oyetech.tools.ext

import timber.log.Timber

/**
Created by Erdi Özbek
-16.04.2022-
-18:07-
 **/

inline fun doInTryCatch(action: (() -> Unit)) {
    try {
        action.invoke()
    } catch (e: Exception) {
        Timber.d("e == " + e.printStackTrace())
        e.printStackTrace()
    }
}

inline fun doInTryCatchWithoutStack(action: (() -> Unit)) {
    try {
        action.invoke()
    } catch (e: Exception) {
        Timber.d("e == " + e.printStackTrace())
        e.printStackTrace()
    }
}
