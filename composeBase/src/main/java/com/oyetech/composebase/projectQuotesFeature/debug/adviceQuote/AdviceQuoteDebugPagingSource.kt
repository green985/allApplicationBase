package com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oyetech.domain.repository.firebase.FirebaseQuotesDebugOperationRepository
import com.oyetech.models.utils.helper.updateState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
Created by Erdi Özbek
-3.02.2025-
-23:27-
 **/

class AdviceQuoteDebugPagingSource(
    private val firebaseQuotesDebugOperationRepository: FirebaseQuotesDebugOperationRepository,
    private val adviceQuoteListStateFlow: MutableStateFlow<AdviceQuoteDebugUiState>,
) : PagingSource<Int, ItemAdviceQuoteDebugUiState>() {
    override fun getRefreshKey(state: PagingState<Int, ItemAdviceQuoteDebugUiState>): Int? {
        val keyy = state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
        Timber.d("getRefreshKey == " + keyy)
        return keyy
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemAdviceQuoteDebugUiState> {

        return try {
            withContext(Dispatchers.IO) {
                val page = params.key ?: 1
                val response =
                    firebaseQuotesDebugOperationRepository.getAllAdviceQuoteList()
                        .map { adviceQuoteResponseData ->
                            adviceQuoteResponseData.map {
                                it.mapToUiState()
                            }
                        }.map {
                            adviceQuoteListStateFlow.updateState {
                                copy(list = it.toImmutableList())
                            }
                            it
                        }.firstOrNull() ?: emptyList()


                LoadResult.Page(
                    data = response,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.isEmpty()) null else page + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


}