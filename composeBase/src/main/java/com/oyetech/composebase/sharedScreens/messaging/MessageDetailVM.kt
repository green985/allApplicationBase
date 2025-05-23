package com.oyetech.composebase.sharedScreens.messaging

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.updateErrorInitial
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageSend
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnMessageTextChange
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnRefresh
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnRetry
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailEvent.OnScreenOut
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-16.02.2025-
-19:02-
 **/

@Suppress("MatchingDeclarationName", "LongParameterList")
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
                dataFlow = messagingAllOperationRepository.getMessagesFromRemoteAndInsertToLocal(
                    conversationId
                ).mapFromLocalToUiState(),
                loadMoreDataFlow = messagingAllOperationRepository.getMessageListWithConversationIdWithMessageId(
                    conversationId
                ).mapFromLocalToUiState(),
                triggerLoadMore = { loadMore() },
                skipInitialLoading = true
            )
        )

    var messagesJob: Job? = null

    val uiEvent = MutableSharedFlow<MessageDetailUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        BufferOverflow.DROP_OLDEST
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
            initConversationOperation()
        }
    }

    private fun initConversationOperation() {
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

    private fun initMessageDetailOperation() {
        uiEvent.tryEmit(MessageDetailUiEvent.OnConversationCreated)
        messagingAllOperationRepository.currentConversationId.value = conversationId
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
                            messageList.mergeMessages(onSizeChanged = {
                                viewModelScope.launch(getDispatcherIo()) {
                                    uiEvent.emit(MessageDetailUiEvent.OnNewMessage)
                                }
                            }, listViewState = listViewState)
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
                if (!event.triggered) {
                    uiState.updateState {
                        copy(onMessageSendTriggered = false)
                    }
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

            OnScreenOut -> {
                setCurrentConversation(true)
            }
        }
    }

    private fun sendMessage() {
        val messageText = uiState.value.messageText
        if (messageText.isBlank()) {
            Timber.d("Message text is blank")
            return
        }
        uiState.updateState {
            copy(messageText = "")
        }
        viewModelScope.launch(getDispatcherIo()) {
            firebaseMessagingRepository.sendMessage(
                messageText = messageText,
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

    }

    fun setCurrentConversation(isExit: Boolean) {
        if (isExit) {
            messagingAllOperationRepository.currentConversationId.value = ""
        } else {

            messagingAllOperationRepository.currentConversationId.value = conversationId
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}