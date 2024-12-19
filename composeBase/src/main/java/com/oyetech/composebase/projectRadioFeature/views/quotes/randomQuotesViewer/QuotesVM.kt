package com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer

import androidx.core.text.parseAsHtml
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseList.BaseListViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.getItemSize
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.helpers.viewProperties.toAnnotatedString
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent.LoadMore
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent.QuoteSeen
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuotesUiState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-16.12.2024-
-23:10-
 **/

class QuotesVM(
    appDispatchers: AppDispatchers,
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
) :
    BaseListViewModel<QuotesUiState>(appDispatchers) {

    override val complexItemViewState: MutableStateFlow<ComplexItemListState<QuotesUiState>> =
        MutableStateFlow(ComplexItemListState())

    val currentPage = MutableStateFlow(0)

    init {
        getList()
    }

    fun onEvent(event: QuoteListUiEvent) {
        when (event) {
            is LoadMore -> {
                val i = (complexItemViewState.value.getItemSize() - event.currentItem) == 2
                if (i) {
                    getList()
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

    private fun getList(isFromRefresh: Boolean = false, isFromLoadMore: Boolean = false): Job {
        Timber.d(" getRandomRemoteQuote  ==" + isFromRefresh + " " + isFromLoadMore)
        if (!isFromRefresh && !isFromLoadMore) {
            complexItemViewState.updateState { copy(isLoadingInitial = true) }
        }
        if (isFromLoadMore) {
            complexItemViewState.updateState { copy(isLoadingMore = true) }
        }

        return viewModelScope.launch(getDispatcherIo()) {
            delay(1000)
            quoteDataOperationRepository.getQuoteUnseenFlow().mapToUiState().asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = {
                            Timber.d(" getRandomRemoteQuote  " + it.size)
                            if (it.isEmpty()) {
                                complexItemViewState.value = ComplexItemListState(
                                    isEmptyList = true
                                )
                            } else {
                                val list = complexItemViewState.value.items
                                val mergedList =
                                    list.toPersistentList().addAll(it).distinctBy { model ->
                                        model.quoteId
                                    }.toImmutableList()
                                Timber.d(" getRandomRemoteQuote  mergedList " + mergedList.size)

                                complexItemViewState.value = ComplexItemListState(mergedList)
                            }
                        },
                        onFailure = {
                            if (isFromLoadMore) {
                                complexItemViewState.value = ComplexItemListState(
                                    isErrorOnMore = true,
                                    errorMessage = errorMessage
                                )
                            } else {
                                complexItemViewState.value = ComplexItemListState(
                                    isErrorInitial = true,
                                    errorMessage = errorMessage
                                )
                            }
                            // handle error
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    var loadMoreJob: Job? = null
    override fun loadMoreItem() {
        loadMoreJob?.cancel()
        loadMoreJob = getList(isFromLoadMore = true)
    }

    override fun refreshList() {
        complexItemViewState.updateState {
            copy(isRefreshing = true)
        }
        getList(isFromRefresh = true)
    }

    override fun retry() {
        complexItemViewState.updateState {
            ComplexItemListState(
                isLoadingInitial = true
            )
        }
        getList()
    }

    private var isVisibleControl = false
    override fun itemVisible(index: Int) {
        Timber.d(" itemVisible index = $index")
        viewModelScope.launch(getDispatcherIo()) {
            if (index == 0) {
                isVisibleControl = true
            }
            if (isVisibleControl) {
                isVisibleControl = false
                val quoteIdMap = complexItemViewState.value.items.subList(0, index).map {
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

