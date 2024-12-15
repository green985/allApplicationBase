package com.oyetech.domain.helper

import android.content.Context
import android.content.pm.ApplicationInfo

/**
Created by Erdi Özbek
-7.08.2023-
-17:29-
 **/

fun Context.isDebug(): Boolean {
    return this.getApplicationInfo().flags and
            ApplicationInfo.FLAG_DEBUGGABLE !== 0
}
