package com.oyetech.composebase.sharedScreens.messaging.conversationList;

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.sharedScreens.messaging.MessagingConversationPagingSource
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-17.02.2025-
-21:40-
 **/

class MessageConversationListVm(
    appDispatchers: AppDispatchers,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val firebaseRealtimeHelperRepository: FirebaseRealtimeHelperRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(MessageConversationListUiState())

    val conversationPaging = Pager(
        config = PagingConfig(
            pageSize = 1
        ),
        pagingSourceFactory = {
            MessagingConversationPagingSource(firebaseMessagingRepository)
        }
    ).flow.cachedIn(viewModelScope)

    init {
        firebaseRealtimeHelperRepository.observeSomething()
    }

    fun onEvent(event: Any) {
        firebaseRealtimeHelperRepository.sendTestMessage()
        when (event) {

            else -> {}
        }
    }
}