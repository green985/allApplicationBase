package com.oyetech.composebase.sharedScreens.messaging;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.updateErrorInitial
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
import kotlinx.coroutines.Job
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

    var messagesJob: Job? = null

    override val listViewState: MutableStateFlow<GenericListState<MessageDetailUiState>> =
        MutableStateFlow(
            GenericListState(
                dataFlow = messagingAllOperationRepository.getMessagesFromRemoteAndInsertToLocal(
                    conversationId
                ).mapFromLocalToUiState(),
            )
        )

    val uiState = MutableStateFlow(
        MessageDetailScreenUiState(
            isLoading = false,
            onMessageSendTriggered = false,
            errorText = "",
            createdAt = null,
            createdAtString = "",
            senderId = firebaseUserRepository.getUserId(),
            receiverId = receiverUserId,
            conversationId = conversationId,
            currentUserId = firebaseUserRepository.getUserId()
        )
    )

    init {
        Timber.d("MessageDetailVm created == " + uiState.value.toString())
        if (conversationId.isNotBlank()) {
            initMessageDetailOperation()
        } else {
            viewModelScope.launch(getDispatcherIo()) {
                firebaseMessagingRepository.getConversationDetailOrCreateFlow(receiverUserId)
                    .asResult().collectLatest {
                        it.fold(
                            onSuccess = { conversationData ->
                                Timber.d("Conversation data: $conversationData")
                                conversationId = conversationData.conversationId

                                initMessageDetailOperation()
                            },
                            onFailure = {
                                listViewState.updateErrorInitial(it)
                            }
                        )
                    }
            }
        }
    }

    private fun initMessageDetailOperation() {
        firebaseMessagingRepository.initLocalMessageSendOperation(viewModelScope)
        loadList()
        observeMessages()
    }

    private fun observeMessages() {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch(getDispatcherIo()) {
            messagingAllOperationRepository.getMessageListFlow(conversationId)
                .asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = { messageList ->
                            Timber.d("Message list: $messageList")
                            messageList.mergeMessages(listViewState)
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
            is OnMessageSend -> {
                uiState.updateState {
                    copy(onMessageSendTriggered = event.triggered)
                }
                if (!event.triggered) {
                    return
                }
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