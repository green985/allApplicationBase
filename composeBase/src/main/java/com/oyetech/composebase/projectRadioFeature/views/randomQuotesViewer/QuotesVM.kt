package com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer

import androidx.core.text.parseAsHtml
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.getItemSize
import com.oyetech.composebase.helpers.viewProperties.toAnnotatedString
import com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer.uiState.QuoteListUiEvent
import com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer.uiState.QuoteListUiEvent.LoadMore
import com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer.uiState.QuoteListUiEvent.QuoteSeen
import com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer.uiState.QuotesUiState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-16.12.2024-
-23:10-
 **/

class QuotesVM(
    appDispatchers: AppDispatchers,
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
) :
    BaseViewModel(appDispatchers) {

    val complexItemListState = MutableStateFlow(ComplexItemListState<QuotesUiState>())

    val currentPage = MutableStateFlow(0)

    init {
        fetchRandomQuotes()
    }

    fun onEvent(event: QuoteListUiEvent) {
        when (event) {
            is LoadMore -> {
                val i = (complexItemListState.value.getItemSize() - event.currentItem) == 2
                if (i) {
                    fetchRandomQuotes()
                }
            }

            is QuoteSeen -> {
                viewModelScope.launch(getDispatcherIo()) {
                    quoteDataOperationRepository.setSeenQuote(
                        event.quotesUiState.quoteId
                    ).collect()
                }
            }
        }
    }

    fun fetchRandomQuotes() {
        viewModelScope.launch(getDispatcherIo()) {
            quoteDataOperationRepository.getQuoteUnseenFlow().mapToUiState().asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = {
                            val list = complexItemListState.value.items
                            val mergedList = list.toPersistentList().addAll(it)
                            complexItemListState.value = ComplexItemListState(mergedList)
                        },
                        onFailure = {
                            // handle error
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    private fun Flow<List<QuoteResponseData>>.mapToUiState(): Flow<List<QuotesUiState>> {
        return this.map {
            it.map {
                if (it.charCount < 200) {
                    QuotesUiState(
                        quoteId = it.quoteId,
                        text = it.text,
                        author = it.author,
                        authorImage = it.authorImage,
                        htmlFormatted = it.htmlFormatted,
                        annotatedStringText = it.htmlFormatted.parseAsHtml().toAnnotatedString()
                    )
                } else {
                    null
                }
            }.filterNotNull()
        }.map {
            it
        }


    }
}

