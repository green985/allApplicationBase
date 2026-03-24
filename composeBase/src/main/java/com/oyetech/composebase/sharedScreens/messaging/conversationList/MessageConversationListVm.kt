package com.oyetech.composebase.sharedScreens.messaging.conversationList

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.makeEmptyListState
import com.oyetech.composebase.base.baseGenericList.setList
import com.oyetech.composebase.base.baseGenericList.updateErrorInitial
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.moonOperation.MoonOperationVm
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent.OnConversationClick
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent.OnConversationClickWithPosition
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent.OnConversationScreenOpen
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent.Retry
import com.oyetech.composebase.sharedScreens.messaging.mapFromLocalToUiState
import com.oyetech.composebase.sharedScreens.messaging.mapToUiState
import com.oyetech.composebase.sharedScreens.navigation.ScreenKey
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-17.02.2025-
-21:40-
 **/

class MessageConversationListVm(
    appDispatchers: AppDispatchers,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val navigationUseCase: NavigationUseCase,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val messagesAllOperationRepository: MessagesAllOperationRepository,
    private val MoonOperationVm: MoonOperationVm,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(MessageConversationListUiState())

    val listViewState: MutableStateFlow<GenericListState<MessageConversationUiState>> =
        MutableStateFlow(
            GenericListState(
                dataFlow = messagesAllOperationRepository.getConversationListUpdated()
                    .mapFromLocalToUiState(clientUserIdAction = { firebaseUserRepository.getUserId() }),
                refreshDataFlow = messagesAllOperationRepository.getConversationList()
                    .mapFromLocalToUiState(clientUserIdAction = { firebaseUserRepository.getUserId() })
            )
        )

    init {
        firebaseMessagingRepository.initLocalMessageSendOperation(viewModelScope)
        controlUserStatus()
    }

    private fun controlUserStatus() {
        listViewState.updateState { copy(isLoadingInitial = true, isRefreshing = false) }
        viewModelScope.launch(getDispatcherIo()) {
            firebaseUserRepository.userProfileDataStateFlow.asResult().collectLatest { result ->
                result.fold({
                    if (!it.isProfileCompletedForAuth()) {
                        Timber.e("User ID is null or empty")
                        listViewState.updateErrorInitial(errorMessage = LanguageKey.messageListErrorUserNotFound)
                    } else {
                        getConversationListData()
                        observeUserReceivedMessages()
                    }
                }, {
                    Timber.e("User ID is null or empty")
                    listViewState.updateErrorInitial(it)
                })
            }
        }
    }

    private suspend fun getConversationListData() {
        Timber.d("Fetching conversation list data")
        listViewState.value.dataFlow?.asResult()?.collect { result ->
            result.fold({ list ->
                if (list.isEmpty()) {
                    listViewState.makeEmptyListState()
                } else {
                    Timber.d("Conversation list received, size: ${list.size}")
                    listViewState.setList(list)
                    refreshLastMessages()
                }
            }, {
                listViewState.updateErrorInitial(it)
            })
        }
    }

    private suspend fun refreshLastMessages() {
        Timber.d("Refreshing last messages")
        try {
            val itemList = listViewState.value.items
            if (itemList.isEmpty()) {
                return
            }
            val lastMessageList = itemList.map { it.lastMessageId }
            val messageList =
                messagesAllOperationRepository.getMessageListWithMessageIdListFromLocal(
                    lastMessageList
                )

            val updatedList = itemList.map { conversation ->
                val lastMessage = messageList.find { it.messageId == conversation.lastMessageId }
                conversation.copy(lastMessageUiState = lastMessage?.mapToUiState())
            }

            listViewState.updateState {
                copy(
                    items = updatedList.toImmutableList(),
                    isLoadingInitial = false,
                    isRefreshing = false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observeUserReceivedMessages() {
        viewModelScope.launch(getDispatcherIo()) {
            val userId = firebaseUserRepository.getUserId()
            messagesAllOperationRepository.getMessageListWithReceiverId(userId)
                .collectLatest {
                    refreshLastMessages()
//
//                    listViewState.value.dataFlow?.asResult()?.collect { result ->
//                        result.fold({ list ->
//                            if (list.isNotEmpty()) {
//                                Timber.d("Received new messages, updating conversation list")
//                                listViewState.setList(list)
//                            }
//                        }, {
//                        })
//
//                    }
                }
        }
    }

    override fun onEvent(event: Any) {
        if (event is MessageConversationListEvent) {
            when (event) {
                is OnConversationClick -> {
                    Timber.d("Conversation Clicked: ${event.conversationId}")
                    messagesAllOperationRepository.currentUsername =
                        listViewState.value.items.find { it.conversationId == event.conversationId }?.username
                            ?: ""

                    navigationUseCase.navigateTo(
                        QuestionAppProjectRoutes.MessageDetail.withArgs(
                            ScreenKey.conversationId to event.conversationId,
                            ScreenKey.receiverUserId to event.userId,
                        )
                    )
                }

                is OnConversationClickWithPosition -> {
                    onConversationClickWithPosition(event)
                }

                OnConversationScreenOpen -> {
                    viewModelScope.launch(getDispatcherIo()) {
                        refreshLastMessages()
                    }
                }

                Retry -> {
                    controlUserStatus()
                }
            }
        }
    }

    private fun onConversationClickWithPosition(event: OnConversationClickWithPosition) {
        val conversationId =
            listViewState.value.items.getOrNull(event.conversationPosition)?.conversationId
                ?: return
        val userId =
            listViewState.value.items.getOrNull(event.conversationPosition)?.userId
                ?: return

        Timber.d("Conversation Clicked: $conversationId")
        messagesAllOperationRepository.currentUsername =
            listViewState.value.items.find { it.conversationId == conversationId }?.username
                ?: ""

        navigationUseCase.navigateTo(
            QuestionAppProjectRoutes.MessageDetail.withArgs(
                ScreenKey.conversationId to conversationId,
                ScreenKey.receiverUserId to userId,
            )
        )
    }
}
