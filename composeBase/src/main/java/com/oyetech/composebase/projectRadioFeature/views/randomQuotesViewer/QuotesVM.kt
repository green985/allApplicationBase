package com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer

import androidx.core.text.parseAsHtml
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.helpers.viewProperties.toAnnotatedString
import com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer.uiState.QuotesUiState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-16.12.2024-
-23:10-
 **/

class QuotesVM(appDispatchers: AppDispatchers, private val quotesRepository: QuotesRepository) :
    BaseViewModel(appDispatchers) {

    val complexItemListState = MutableStateFlow(ComplexItemListState<QuotesUiState>())

    init {
        fetchRandomQuotes()
    }

    fun fetchRandomQuotes() {
        viewModelScope.launch(getDispatcherIo()) {
            delay(100)
            quotesRepository.fetchQuotes().mapToUiState().asResult().collectLatest {
                it.fold(
                    onSuccess = {
                        complexItemListState.value =
                            ComplexItemListState(items = it.toImmutableList())
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
            Timber.d(" it.size =    ${it.size}")
            it
        }


    }
}

