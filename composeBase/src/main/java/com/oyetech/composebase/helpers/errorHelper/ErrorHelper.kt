package com.oyetech.composebase.helpers.errorHelper

import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Erdi Özbek
-28.12.2024-
-14:41-
 **/

// todo TimeoutCancellationException operasyonunu yonet.

@Suppress("ReturnCount")
fun String.toErrorMessage(): String {
    if (this.contains("unable to resolve host", true)) {
        return LanguageKey.internetConnectionErrorText
    }

    if (this.isEmpty()) {
        return LanguageKey.generalErrorText
    }


    return this
}