package com.oyetech.models.mappers

import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.postBody.messages.MessageRequestBody

/**
Created by Erdi Özbek
-23.03.2022-
-00:00-
 **/

fun MessageRequestBody.mapToMessageDetailResponse(
    status: Int = 0,
): MessageDetailDataResponse {
    var messageRequestBody = this
    var conversationID = 0L

    if (messageRequestBody.conversationId == 0L) {
        conversationID = generateConversationId(0, messageRequestBody.toUserId)
    } else {
        conversationID = conversationId
    }

    var messageDetailDataResponse =
        MessageDetailDataResponse(
            tempId = messageRequestBody.tempId,
            content = messageRequestBody.content,
            contentType = messageRequestBody.contentType,
            conversationId = conversationID,
            date = "",
            deliverDate = "",
            deliveryUrl = "",
            fromUserId = messageRequestBody.toUserId,
            messageId = messageRequestBody.id,
            isDelivered = false,
            isSeen = false,
            seenDate = "",
            status = status,
            toUserName = this.toUserName,
            profileImageUrl = this.profileImageUrl,
        )
    messageDetailDataResponse.imagePropertyData = messageRequestBody.imageUploadResponseData
    messageDetailDataResponse.audioPropertyData = messageRequestBody.audioUploadResponseData
    messageDetailDataResponse.audioDurationModel.totalDuration =
        messageDetailDataResponse.audioPropertyData?.totalDuration ?: 0L
    return messageDetailDataResponse
}

private fun generateConversationId(x: Long, y: Long): Long {
    return -(((x + y) * (x + y + 1)) / 2 + y)
}
