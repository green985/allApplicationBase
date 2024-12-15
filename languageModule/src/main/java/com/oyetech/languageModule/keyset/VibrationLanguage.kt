package com.oyetech.languageModule.keyset

import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper

/**
Created by Erdi Özbek
-23.10.2024-
-23:55-
 **/

object VibrationLanguage {

    var GOOGLE_SIGN_IN_ERROR
        set(value) {}
        get() = LocalLanguageHelper.getStringWithKey(
            "GOOGLE_SIGN_IN_ERROR",
            defaultString = " Google Sign In Error"
        )
}