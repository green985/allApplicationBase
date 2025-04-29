package com.oyetech.composebase.projectQuotesFeature.searchScreen

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuotesFeature.quotes.randomQuotesViewer.mapToUiState
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteUiState
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.models.utils.const.HelperConstant
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber

class QuoteSearchPagingSource(
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
    private val uiState: MutableStateFlow<QuoteSearchUiState>,
) : PagingSource<Int, QuoteUiState>() {
    override fun getRefreshKey(state: PagingState<Int, QuoteUiState>): Int? {
        val keyy = state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
        Timber.d("getRefreshKey == " + keyy)
        return keyy
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuoteUiState> {

        return try {
            withContext(Dispatchers.IO) {
                val searchQuery = uiState.value.searchQuery
                if (searchQuery.isBlank()) {
                    LoadResult.Page(
                        data = emptyList(),
                        null, null
                    )
                } else if (searchQuery.length > 2) {
                    delay(HelperConstant.DEBOUNCE_TIME_LONG)
                    val response =
                        quoteDataOperationRepository.searchQuote(searchQuery)
                            .mapToUiState().firstOrNull() ?: emptyList()

                    uiState.updateState {
                        copy(searchResults = response.toImmutableList())
                    }

                    LoadResult.Page(
                        data = response,
                        null, null
                    )
                } else {
                    LoadResult.Page(
                        data = uiState.value.searchResults,
                        null, null
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


}