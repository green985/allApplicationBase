package com.oyetech.composebase.sharedScreens.messaging;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageSend
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageTextChange
import com.oyetech.domain.repository.firebase.FirebaseMessagingLocalRepository
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-16.02.2025-
-19:02-
 **/

class MessageDetailVm(
    appDispatchers: AppDispatchers,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val firebaseMessagingLocalRepository: FirebaseMessagingLocalRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(MessageDetailUiState())

    val receiverSenderId = "1234567890" // will be change if group chat or something.
    var conversationId = "1234567890" // will be change if group chat or something.

    init {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseMessagingRepository.getConversationDetailOrCreateFlow(receiverSenderId)
                .asResult().collectLatest {
                    it.fold(
                        onSuccess = { conversationData ->
                            conversationId = conversationData.conversationId
                            Timber.d("Conversation data: $conversationData")
                        },
                        onFailure = {
                            Timber.e(it)
                        }
                    )
                }
        }

        firebaseMessagingRepository.initLocalMessageSendOperation(viewModelScope)
    }

    fun onEvent(event: MessageDetailEvent) {
        when (event) {
            OnMessageSend -> {
                sendMessage()
            }

            is OnMessageTextChange -> {
                uiState.value = uiState.value.copy(messageText = event.messageText)
            }
        }
    }

    private fun sendMessage() {
//        viewModelScope.launch(getDispatcherIo()) {
//            firebaseMessagingRepository.getConversationList()
//                .asResult().collectLatest {
//                    it.fold(
//                        onSuccess = { conversationList ->
//                            Timber.d("Conversation list: $conversationList")
//                        },
//                        onFailure = {
//                            Timber.e(it)
//                        }
//                    )
//                }
//        }
        viewModelScope.launch(getDispatcherIo()) {
            firebaseMessagingRepository.sendMessage(
                messageText = uiState.value.messageText,
                conversationId = conversationId,
                receiverUserId = receiverSenderId
            ).asResult().collectLatest {
                it.fold(
                    onSuccess = { messageDetail ->
                        Timber.d("Message sent: $messageDetail")
                    },
                    onFailure = {
                        Timber.e(it)
                    }
                )
            }
        }
        Timber.d("Message sent: ${uiState.value.messageText}")
        uiState.updateState {
            copy(messageText = "")
        }
    }
}