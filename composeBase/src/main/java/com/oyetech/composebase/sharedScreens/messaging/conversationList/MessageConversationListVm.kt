package com.oyetech.composebase.sharedScreens.messaging.conversationList;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.makeEmptyListState
import com.oyetech.composebase.base.baseGenericList.setList
import com.oyetech.composebase.base.baseGenericList.updateErrorInitial
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import com.oyetech.composebase.sharedScreens.messaging.mapFromLocalToUiState
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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
        viewModelScope.launch(getDispatcherIo()) {
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
    }

    fun onEvent(event: Any) {
        when (event) {

            else -> {}
        }
    }

}