package com.oyetech.composebase.projectQuotesFeature.searchScreen;

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuotesFeature.searchScreen.QuoteSearchEvent.ExpandedChange
import com.oyetech.composebase.projectQuotesFeature.searchScreen.QuoteSearchEvent.SearchQueryChanged
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-15.02.2025-
-22:30-
 **/

class QuoteSearchVM(
    appDispatchers: AppDispatchers,
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(QuoteSearchUiState(expanded = true))
    private var searchString = ""
    var quoteSearchPage =
        Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                QuoteSearchPagingSource(
                    quoteDataOperationRepository,
                    uiState
                )
            }
        ).flow.cachedIn(viewModelScope)

    fun onEvent(event: QuoteSearchEvent) {
        when (event) {
            is ExpandedChange -> {}
            is SearchQueryChanged -> {
                uiState.updateState {
                    copy(searchQuery = event.query)
                }
                searchString = event.query.ifBlank {
                    ""
                }
                quoteSearchPage =
                    Pager(
                        config = PagingConfig(
                            pageSize = 20,
                        ),
                        pagingSourceFactory = {
                            QuoteSearchPagingSource(
                                quoteDataOperationRepository,
                                uiState
                            )
                        }
                    ).flow.cachedIn(viewModelScope)
            }
        }
    }
}
