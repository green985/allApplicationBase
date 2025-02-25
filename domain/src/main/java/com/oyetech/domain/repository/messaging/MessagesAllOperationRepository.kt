package com.oyetech.domain.repository.messaging

import com.oyetech.models.firebaseModels.messagingModels.FirebaseMessagingLocalData

interface MessagesAllOperationRepository {
    fun getFirstInMessage()
    fun getMessageListFlow(conversationId: String)
    fun getMessageListWithLastMessageId(conversationId: String, messageId: String)
    fun getMessageWithId(messageId: String)
    fun deleteLastList(idList: List<String>)
    fun deleteAllList()
    fun findRadioStationWithStationIdList(messageIdList: List<String>)
    fun insertLastList(list: List<FirebaseMessagingLocalData>)
}
