package com.oyetech.domain.repository.socketOperation

import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.postBody.messages.MessageRequestBody

/**
Created by Erdi Özbek
-10.03.2022-
-17:00-
 **/
@Suppress("TooManyFunctions")
interface SignalrOperationRepository {

    fun socketErrorTrigger(eventString: String)

    // have to add for signalRUseCase call
    fun sendClientReadMessage(eventString: String)
    fun sendTypingEvent(userId: Long)
    fun sendRecordingVoiceEvent(userId: Long)

    fun onConnectionConnect()
    fun onConnectionClosed(exception: Exception?)
    fun onConnectionError(errorString: String)

    fun closeConnectionWithActivityLifecycle()
    fun startConnection()
    fun deathConnection()

    var isSocketConnect: Boolean
}

interface SignalrReceivedEventRepository {

    fun onMessageSentEventTrigger(eventString: String)
    fun onMessageReceivedEventTrigger(eventString: String)
    fun onMessageDeliveredTrigger(eventString: String)
    fun onMessageSeenTrigger(eventString: String)
    fun onMessageDeletedTrigger(eventString: String)
    fun onConversationDeletedTrigger(eventString: String)

    fun onWaitingMessageSentAcknowledge(eventString: String)
    fun onWaitingMessageDeliveredAcknowledge(eventString: String)
    fun onWaitingMessageSeenAcknowledge(eventString: String)
    fun onWaitingMessageReceivedAcknowledge(eventString: String)
    fun onWaitingMessageReadAcknowledge(eventString: String)

    fun onUserOnline(eventString: String)
    fun onUserOffline(eventString: String)

    fun onTypingEventReceived(eventString: String)
    fun onRecordingEventReceived(eventString: String)
    fun onSendAudioEventReceived(eventString: String)
    fun onSendImageEventReceived(eventString: String)
    fun onMessagingLimitExceeded(eventString: String)
    fun onConversationLimitExceeded(eventString: String)
    fun onTranslateLimitExceeded(eventString: String)
}

interface SignalrSendEventRepository {

    fun sendSocketMessage(body: MessageRequestBody)
    fun sendClientReadMessage(eventBody: String)
    fun sendClientReceivedMessage(eventBody: MessageDetailDataResponse?)

    fun sendAcknowledgeMessageSent(messageId: Long)
    fun sendAcknowledgeMessageRead(messageId: Long)
    fun sendAcknowledgeMessageDelivered(messageId: Long)
    fun sendAcknowledgeMessageSeen(messageId: Long)
    fun sendAcknowledgeMessageReceived(messageId: Long)

    fun sendUserOnlineStatus(isOnline: Boolean)

    fun sendTypingEvent(userId: Long)
    fun sendImageUploadEvent(userId: Long)
    fun sendAudioUploadEvent(userId: Long)
    fun sendRecordVoiceEvent(userId: Long)
}
