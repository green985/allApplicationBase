package com.oyetech.composebase.sharedScreens.quotes.randomQuotesViewer

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.mappers.mapToUi.QuotesMappers.mapToQuoteUiState
import com.oyetech.composebase.sharedScreens.quotes.listScreen.QuotePagingSource
import com.oyetech.composebase.sharedScreens.quotes.uiState.QuoteListUiEvent
import com.oyetech.composebase.sharedScreens.quotes.uiState.QuoteListUiEvent.QuoteSeen
import com.oyetech.composebase.sharedScreens.quotes.uiState.QuoteUiState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-16.12.2024-
-23:10-
 **/

class QuotesVM(
    private val appDispatchers: AppDispatchers,
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
) : BaseViewModel(appDispatchers) {

    val quotesPage =
        Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                QuotePagingSource(
                    quoteDataOperationRepository,
                    complexItemViewState = complexItemViewState
                )
            }
        ).flow.cachedIn(viewModelScope)

    val complexItemViewState: MutableStateFlow<ComplexItemListState<QuoteUiState>> =
        MutableStateFlow(ComplexItemListState())

    val currentPage = MutableStateFlow(0)

    init {
        Timber.d("QuotesVM init")
    }

    fun onEvent(event: QuoteListUiEvent) {
        when (event) {
            is QuoteSeen -> {
                itemVisible(event.index)
            }
        }
    }

    private var isVisibleControl = false
    fun itemVisible(index: Int) {
        Timber.d("itemVisible  $index")
        viewModelScope.launch(getDispatcherIo()) {
            if (index == 0) {
                isVisibleControl = true
            }
            if (isVisibleControl) {
                isVisibleControl = false
                val quoteIdMap =
                    complexItemViewState.value.items.subList(0, index).map {
                        quoteDataOperationRepository.setSeenQuote(it.quoteId)
                            .collect()
                    }
                return@launch
            }
            val quoteId = complexItemViewState.value.items.getOrNull(index)?.quoteId
            if (quoteId != null) {
                quoteDataOperationRepository.setSeenQuote(quoteId)
                    .collect()
            }
        }
    }
}

fun Flow<List<QuoteResponseData>>.mapToUiState(): Flow<List<QuoteUiState>> {
    return this.map {
        it.map {
            mapToQuoteUiState(it)
        }
    }
}

