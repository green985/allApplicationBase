package com.oyetech.models.postBody.messages

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

/**
Created by Erdi Özbek
-16.03.2022-
-15:46-
 **/
@Keep
@JsonClass(generateAdapter = true)
data class MessagesConversationRequestBody(
    var page: Int,
)
