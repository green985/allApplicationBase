package com.oyetech.helper.strings.entitySerialize

import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.entity.messages.MessageDetailSubDataResponse
import com.oyetech.models.entity.messages.mapToNormalize
import com.oyetech.models.utils.moshi.deserialize

/**
Created by Erdi Özbek
-28.03.2022-
-20:14-
 **/

fun getMessageDetailResponse(eventString: String): MessageDetailDataResponse? {
    var responseData = eventString.deserialize<MessageDetailSubDataResponse>()?.mapToNormalize()
    if (responseData == null) {
        return null
    }
    return responseData
}
