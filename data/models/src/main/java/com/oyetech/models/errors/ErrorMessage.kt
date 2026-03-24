package com.oyetech.models.errors

import timber.log.Timber

/**
Created by Erdi Özbek
-2.03.2022-
-01:08-
 **/

@Suppress("ReturnCount", "ComplexCondition")
object ErrorMessage {
    var unknownErrorMessage = "NO_INTERNET_CONNECTION"
    var JsonParseError = "Expected BEGIN_OBJECT but was false"
    var PROFILE_NOT_FOUND_ERROR_STRING = "USER_PROFILE_DOES_NOT_EXISTS"

    // Google Identity / Credential Manager error codes
    var ID_TOKEN_RETRIEVAL_FAILED = "[28404] Failed to retrieve an ID token"

    fun fetchErrorMessage(message: String?): String {
        if (message.isNullOrEmpty()) {
            return unknownErrorMessage
        }
        if (message.contains("28404", ignoreCase = true) ||
            message.contains(ID_TOKEN_RETRIEVAL_FAILED, ignoreCase = true)
        ) {
            return unknownErrorMessage
        }
        if (message.contains("Certificate pinning failure!", ignoreCase = true) || message.contains(
                "500", ignoreCase = true
            ) || message.contains(
                "Too many follow-up requests",
                ignoreCase = true
            ) || message.contains(
                "timed out",
                ignoreCase = true
            ) || message.contains(
                "failed to connect to",
                ignoreCase = true
            ) || message.contains(
                "timeout",
                ignoreCase = true
            ) || message.contains("Unable to resolve", ignoreCase = true)
        ) {
            return unknownErrorMessage
        }
        return message
    }

    fun controlJsonError(errorMessage: String?): Boolean {
        return errorMessage?.contains(JsonParseError) == true
    }

    fun fetchException(e: Exception, writePrintStack: Boolean = true) {
        if (writePrintStack) {
            e.printStackTrace()
        }
        Timber.d("exception == " + e.message)
    }

    fun throwError(errorMessage: String?): String {
        return fetchErrorMessage(errorMessage)
    }
}
