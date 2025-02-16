package com.oyetech.composebase.sharedScreens.messaging;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageSend
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageTextChange
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
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(MessageDetailUiState())

    val receiverSenderId = "1234567890" // will be change if group chat or something.

    init {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseMessagingRepository.conversationStateFlow.asResult().collectLatest {
                it.fold(
                    onSuccess = { conversationData ->
                        Timber.d("Conversation data: $conversationData")
                    },
                    onFailure = {
                        Timber.e(it)
                    }
                )
            }
        }
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
        viewModelScope.launch(getDispatcherIo()) {
            firebaseMessagingRepository.getConversationDetailOrCreate("asjdnaksjdnakjsdn")
            firebaseMessagingRepository.getConversationDetailOrCreate("asjdnaksjdnakjsdn123123")
            firebaseMessagingRepository.getConversationDetailOrCreate("asjdnaksjdnakjsdn123123aasgasraw")
            firebaseMessagingRepository.getConversationDetailOrCreate("asldnalkdnlaknsdagasgasg")
            firebaseMessagingRepository.getConversationDetailOrCreate("asldnalkdnlaknsdagasgasgasd")
            firebaseMessagingRepository.getConversationDetailOrCreate("asldnalkdnlaknsdagasgasgasd1")
            firebaseMessagingRepository.getConversationDetailOrCreate("asldnalkdnlaknsdagasgasgasd13")
            firebaseMessagingRepository.getConversationDetailOrCreate("asdklansdljkansjkldnasd")
            firebaseMessagingRepository.getConversationDetailOrCreate("asdjnaskjdbnakjsdbad")
        }

        return



        Timber.d("Message sent: ${uiState.value.messageText}")
        uiState.updateState {
            copy(messageText = "")
        }
    }
}