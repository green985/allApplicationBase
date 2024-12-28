package com.oyetech.composebase.projectRadioFeature.views.quotes.listScreen

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer.mapToUiState
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuotesUiState
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

class QuotePagingSource(
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
    private val complexItemViewState: MutableStateFlow<ComplexItemListState<QuotesUiState>>,
) : PagingSource<Int, QuotesUiState>() {
    override fun getRefreshKey(state: PagingState<Int, QuotesUiState>): Int? {
        val keyy = state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
        Timber.d("getRefreshKey == " + keyy)
        return keyy
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuotesUiState> {

        return try {
            withContext(Dispatchers.IO) {
                val page = params.key ?: 1
                Timber.d("getRefreshKey load == " + page)
                val oldList = complexItemViewState.value.items.map { it.quoteId }.toTypedArray()
                delay(2000)
                val response =
                    quoteDataOperationRepository.getQuoteUnseenFlow(oldList).map {
                        var listt = it
                        listt = listt.filterNot {
                            val result = oldList.contains(it.quoteId)
                            result
                        }
                        listt
                    }.mapToUiState().map {
                        complexItemViewState.value = complexItemViewState.value.copy(
                            items = complexItemViewState.value.items.toMutableList().apply {
                                addAll(it)
                            }.toPersistentList()
                        )
                        it
                    }.firstOrNull()
                        ?: emptyList()


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