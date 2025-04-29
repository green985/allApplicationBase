package com.oyetech.composebase.projectQuotesFeature.authorListScreen;

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.ComplexItemListState
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-11.01.2025-
-15:51-
 **/

class AuthorListVM(
    appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
) : BaseViewModel(appDispatchers) {

    fun onEvent(event: QuoteAuthorEvent) {


    }

    private val complexItemViewState: MutableStateFlow<ComplexItemListState<QuoteAuthorUiState>> =
        MutableStateFlow(ComplexItemListState())

    val authorPage =
        Pager(
            config = PagingConfig(
                pageSize = 1
            ),
            pagingSourceFactory = {
                AuthorListPagingSource(
                    quoteDataOperationRepository,
                    complexItemViewState = complexItemViewState
                )
            }
        ).flow.cachedIn(viewModelScope)

}