package com.oyetech.composebase.helpers.errorHelper

import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Erdi Özbek
-28.12.2024-
-14:41-
 **/

// todo TimeoutCancellationException operasyonunu yonet.

/**
 * Missing or insufficient permissions.
 */

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

object ErrorHelper {

    fun getErrorMessage(exception: Exception, ignoreStack: Boolean = false): String {
        if (!ignoreStack) {
            exception.printStackTrace()
        }
        return exception.message?.toErrorMessage() ?: errorMessage
    }

    fun getErrorMessage(exception: Throwable, ignoreStack: Boolean = false): String {
        if (!ignoreStack) {
            exception.printStackTrace()
        }
        return exception.message?.toErrorMessage() ?: errorMessage
    }

    val errorMessage: String = LanguageKey.generalErrorText
}