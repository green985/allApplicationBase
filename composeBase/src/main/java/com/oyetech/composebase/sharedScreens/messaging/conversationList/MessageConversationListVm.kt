package com.oyetech.composebase.sharedScreens.messaging.conversationList;

import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import com.oyetech.composebase.sharedScreens.messaging.mapFromLocalToUiState
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
    private val messagesAllOperationRepository: MessagesAllOperationRepository,
) : BaseListViewModel<MessageConversationUiState>(appDispatchers) {
    val uiState = MutableStateFlow(MessageConversationListUiState())

    override val listViewState: MutableStateFlow<GenericListState<MessageConversationUiState>> =
        MutableStateFlow(
            GenericListState(
//                dataFlow = flowOf(emptyList())
                dataFlow = messagesAllOperationRepository.getConversationList()
                    .mapFromLocalToUiState(clientUserId = firebaseUserRepository.getUserId())
            )
        )

    init {
//        firebaseRealtimeHelperRepository.observeSomething()
//
//        viewModelScope.launch(getDispatcherIo()) {
//            firebaseMessagingRepository.getConversationDetailOrCreateFlow("denemeUserIdddddddddddddd")
//                .asResult()
//                .collectLatest {
//                    Timber.d("conversationDetailOrCreateFlow: $it")
//                }
//
//        }

    }

    fun onEvent(event: Any) {
//        firebaseRealtimeHelperRepository.sendTestMessage()
        when (event) {

            else -> {}
        }
    }

}