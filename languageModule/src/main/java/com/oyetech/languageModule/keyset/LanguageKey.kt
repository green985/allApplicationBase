package com.oyetech.languageModule.keyset

import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper

/**
Created by Erdi Özbek
-22.12.2024-
-20:08-
 **/

object LanguageKey {
    var generalErrorText set(value) {} get() = LocalLanguageHelper.getStringWithKey("Error")
    var login set(value) {} get() = LocalLanguageHelper.getStringWithKey("Login")
    var denemeKey set(value) {} get() = LocalLanguageHelper.getStringWithKey("denemeKey")

}