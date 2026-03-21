package com.oyetech.models.errors.exceptionHelper

/**
Created by Erdi Özbek
-23.02.2025-
-20:53-
 **/

class GeneralException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
