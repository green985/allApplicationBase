package com.oyetech.composebase.sharedScreens.messaging.conversationList

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import com.oyetech.composebase.sharedScreens.messaging.mapFromLocalToUiState
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
Created by Erdi Özbek
-16.02.2025-
-22:40-
 **/

class MessagingConversationPagingSource(
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val firebaseUserRepository: FirebaseUserRepository,
) :
    PagingSource<Int, MessageConversationUiState>() {
    override fun getRefreshKey(state: PagingState<Int, MessageConversationUiState>): Int? {
        val keyy = state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
        Timber.d("getRefreshKey == " + keyy)
        return keyy
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageConversationUiState> {
        val userId = firebaseUserRepository.getUserId()
        return try {
            withContext(Dispatchers.IO) {
                val response =
                    firebaseMessagingRepository.getConversationList()
                        .mapFromLocalToUiState(clientUserId = userId)
                        .firstOrNull() ?: emptyList()

                LoadResult.Page(
                    data = response,
                    null, null
                )

            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


}