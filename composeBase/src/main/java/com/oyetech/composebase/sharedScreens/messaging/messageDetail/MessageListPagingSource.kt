package com.oyetech.composebase.sharedScreens.messaging.messageDetail

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailUiState
import com.oyetech.composebase.sharedScreens.messaging.mapToUiState
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
Created by Erdi Özbek
-22.02.2025-
-21:02-
 **/

class MessageListPagingSource(
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val conversationId: String,
) :
    PagingSource<Int, MessageDetailUiState>() {
    override fun getRefreshKey(state: PagingState<Int, MessageDetailUiState>): Int? {
        val keyy = state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
        Timber.d("getRefreshKey == " + keyy)
        return keyy
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageDetailUiState> {

        return try {
            withContext(Dispatchers.IO) {
                val response =
                    firebaseMessagingRepository.getMessageListWithConversationId(conversationId)
                        .mapToUiState().firstOrNull() ?: emptyList()

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