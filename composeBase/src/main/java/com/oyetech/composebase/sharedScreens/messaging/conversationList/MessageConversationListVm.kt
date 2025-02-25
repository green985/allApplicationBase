package com.oyetech.composebase.sharedScreens.messaging.conversationList;

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepository
import com.oyetech.domain.repository.messaging.MessagesSendingOperationRepository
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
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val messagesSendingOperationRepository: MessagesSendingOperationRepository,
    private val firebaseRealtimeHelperRepository: FirebaseRealtimeHelperRepository,
    private val firebaseUserRepository: FirebaseUserRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(MessageConversationListUiState())

    val conversationPaging = Pager(
        config = PagingConfig(
            pageSize = 1
        ),
        pagingSourceFactory = {
            MessagingConversationPagingSource(
                firebaseMessagingRepository,
                firebaseUserRepository = firebaseUserRepository
            )
        }
    ).flow.cachedIn(viewModelScope)

    init {
//        firebaseRealtimeHelperRepository.observeSomething()

        viewModelScope.launch(getDispatcherIo()) {
            firebaseMessagingRepository.getConversationDetailOrCreateFlow("denemeUserIdddddddddddddd")
                .asResult()
                .collectLatest {
                    Timber.d("conversationDetailOrCreateFlow: $it")
                }
        }

    }

    fun onEvent(event: Any) {
//        firebaseRealtimeHelperRepository.sendTestMessage()
        when (event) {

            else -> {}
        }
    }
}