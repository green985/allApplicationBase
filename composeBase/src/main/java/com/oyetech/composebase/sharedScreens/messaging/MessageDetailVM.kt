package com.oyetech.composebase.sharedScreens.messaging;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageSend
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageTextChange
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnRefresh
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnRetry
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.toImmutableList
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
    var conversationId: String = "",
    var receiverUserId: String = "",
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val messagingAllOperationRepository: MessagesAllOperationRepository,
    private val firebaseUserRepository: FirebaseUserRepository,
) : BaseListViewModel<MessageDetailUiState>(appDispatchers) {

    override val listViewState: MutableStateFlow<GenericListState<MessageDetailUiState>> =
        MutableStateFlow(
            GenericListState(
                dataFlow = messagingAllOperationRepository.getMessageListFlow(conversationId)
                    .mapFromLocalToUiState(),
            )
        )

    val uiState = MutableStateFlow(
        MessageDetailScreenUiState(
            isLoading = false,
            errorText = "",
            createdAt = null,
            createdAtString = "",
            senderId = firebaseUserRepository.getUserId(),
            receiverId = receiverUserId,
            conversationId = conversationId
        )
    )

    init {
        Timber.d("MessageDetailVm created == " + uiState.value.toString())
        viewModelScope.launch(getDispatcherIo()) {
            firebaseMessagingRepository.getConversationDetailOrCreateFlow(receiverUserId)
                .asResult().collectLatest {
                    it.fold(
                        onSuccess = { conversationData ->
                            conversationId = conversationData.conversationId
                            Timber.d("Conversation data: $conversationData")
                            observeMessageList()
                        },
                        onFailure = {
                            Timber.d(it)
                        }
                    )
                }
        }

        firebaseMessagingRepository.initLocalMessageSendOperation(viewModelScope)
    }

    private fun observeMessageList() {
        viewModelScope.launch(getDispatcherIo()) {
            messagingAllOperationRepository.getMessageListFlow(conversationId)
                .mapFromLocalToUiState().asResult().collectLatest {
                    it.fold(
                        onSuccess = { messageList ->
                            Timber.d("Message list: $messageList")
                            listViewState.updateState {
                                copy(
                                    isLoadingInitial = false,
                                    items = messageList.toImmutableList()
                                )
                            }
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

            OnRefresh -> {
                Timber.d("OnRefresh")
                refreshList()
            }

            OnRetry -> {
                Timber.d("OnRetry")
//                retry()
            }
        }
    }

    private fun sendMessage() {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseMessagingRepository.sendMessage(
                messageText = uiState.value.messageText,
                conversationId = conversationId,
                receiverUserId = receiverUserId
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