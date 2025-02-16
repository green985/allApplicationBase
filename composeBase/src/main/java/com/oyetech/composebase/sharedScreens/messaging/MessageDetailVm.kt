package com.oyetech.composebase.sharedScreens.messaging;

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageSend
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageTextChange
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

/**
Created by Erdi Özbek
-16.02.2025-
-19:02-
 **/

class MessageDetailVm(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(MessageDetailUiState())

    init {
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
        Timber.d("Message sent: ${uiState.value.messageText}")
        uiState.updateState {
            copy(messageText = "")
        }
    }
}