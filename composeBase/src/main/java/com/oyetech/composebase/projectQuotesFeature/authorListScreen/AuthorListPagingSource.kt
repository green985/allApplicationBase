package com.oyetech.composebase.projectQuotesFeature.authorListScreen

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oyetech.composebase.base.baseGenericList.ComplexItemListState
import com.oyetech.composebase.base.updateState
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
Created by Erdi Özbek
-11.01.2025-
-15:52-
 **/

class AuthorListPagingSource(
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
    private val complexItemViewState: MutableStateFlow<ComplexItemListState<QuoteAuthorUiState>>,
) :
    PagingSource<Int, QuoteAuthorUiState>() {
    override fun getRefreshKey(state: PagingState<Int, QuoteAuthorUiState>): Int? {
        val keyy = state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
        Timber.d("getRefreshKey == " + keyy)
        return keyy

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuoteAuthorUiState> {
        return try {
            withContext(Dispatchers.IO) {
                val result =
                    quoteDataOperationRepository.getAuthorList().mapToUiState().map {
                        complexItemViewState.updateState {
                            copy(items = it.toImmutableList())
                        }
                        it
                    }.firstOrNull() ?: emptyList()

                LoadResult.Page(
                    data = result,
                    prevKey = null,
                    nextKey = null
                )
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }


}
