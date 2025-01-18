package com.oyetech.composebase.helpers.general

import com.oyetech.composebase.BuildConfig

/**
Created by Erdi Özbek
-18.01.2025-
-11:19-
 **/

object GeneralSettings {

    fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }


}