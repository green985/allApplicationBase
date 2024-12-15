package com.oyetech.models.postBody.feedback

import androidx.annotation.Keep
import com.squareup.moshi.Json

/**
Created by Erdi Özbek
-26.12.2023-
-15:34-
 **/

@Keep
data class FeedbackOperationRequestBody(
    @Json(name = "email") var email: String = "",
    @Json(name = "name") var name: String = "",
    @Json(name = "description") var message: String = "",
    @Json(name = "isReadByAdmin") var isReadByAdmin: Boolean = false,
)