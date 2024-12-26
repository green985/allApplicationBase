package com.oyetech.languageModule.keyset

import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper

/**
Created by Erdi Özbek
-26.12.2024-
-10:53-
 **/

object ExceptionKeys {

    var emptyList set(value) {} get() = LocalLanguageHelper.getStringWithKey("emptyList")

}