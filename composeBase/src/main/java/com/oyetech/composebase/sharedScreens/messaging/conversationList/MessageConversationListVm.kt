package com.oyetech.composebase.sharedScreens.messaging.conversationList;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.makeEmptyListState
import com.oyetech.composebase.base.baseGenericList.setList
import com.oyetech.composebase.base.baseGenericList.updateErrorInitial
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent.OnConversationClick
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent.Retry
import com.oyetech.composebase.sharedScreens.messaging.mapFromLocalToUiState
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
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
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val messagesAllOperationRepository: MessagesAllOperationRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(MessageConversationListUiState())

    val listViewState: MutableStateFlow<GenericListState<MessageConversationUiState>> =
        MutableStateFlow(
            GenericListState(
                dataFlow = messagesAllOperationRepository.getConversationList()
                    .mapFromLocalToUiState(clientUserId = firebaseUserRepository.getUserId()),
                refreshDataFlow = messagesAllOperationRepository.getConversationList()
                    .mapFromLocalToUiState(clientUserId = firebaseUserRepository.getUserId())
            )
        )

    init {
        firebaseMessagingRepository.initLocalMessageSendOperation(viewModelScope)
        getConversationList()
    }

    private fun getConversationList() {
        listViewState.updateState { copy(isLoadingInitial = true, isRefreshing = false) }
        viewModelScope.launch(getDispatcherIo()) {
            firebaseUserRepository.userDataStateFlow.asResult().collectLatest { result ->
                result.fold({
                    if (!it.isProfileComplete()) {
                        Timber.e("User ID is null or empty")
                        listViewState.updateErrorInitial(errorMessage = LanguageKey.messageListErrorUserNotFound)
                    } else {
                        listViewState.value.dataFlow?.asResult()?.collect { result ->
                            result.fold({ list ->
                                if (list.isEmpty()) {
                                    listViewState.makeEmptyListState()
                                } else {
                                    listViewState.setList(list)
                                }
                            }, {
                                listViewState.updateErrorInitial(it)
                            })

                        }
                    }
                }, {
                    listViewState.updateErrorInitial(it)
                })
            }

        }
    }

    fun onEvent(event: MessageConversationListEvent) {
        when (event) {
            is OnConversationClick -> {
                Timber.d("Conversation Clicked: ${event.conversationId}")
            }

            Retry -> {
                getConversationList()
            }
        }
    }

}