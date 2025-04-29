package com.oyetech.composebase.sharedScreens.messaging.conversationList;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import com.oyetech.composebase.sharedScreens.messaging.mapFromLocalToUiState
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

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
) : BaseListViewModel<MessageConversationUiState>(appDispatchers) {
    val uiState = MutableStateFlow(MessageConversationListUiState())

    override val listViewState: MutableStateFlow<GenericListState<MessageConversationUiState>> =
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
    }

    fun onEvent(event: Any) {
        when (event) {

            else -> {}
        }
    }

}