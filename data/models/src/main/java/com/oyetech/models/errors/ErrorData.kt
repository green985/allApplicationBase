package com.oyetech.models.errors

import androidx.annotation.Keep

/**
Created by Erdi Özbek
-25.11.2023-
-16:42-
 **/

@Keep
data class ErrorData(
    var message: String,
    var exception: Exception? = null,
)
