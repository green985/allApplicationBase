package com.oyetech.composebase.projectRadioFeature.views.quotes.randomQuotesViewer

import androidx.core.text.parseAsHtml
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oyetech.composebase.base.baseList.BaseListViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.getItemSize
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.helpers.viewProperties.toAnnotatedString
import com.oyetech.composebase.projectRadioFeature.views.quotes.listScreen.QuotePagingSource
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent.LoadMore
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuoteListUiEvent.QuoteSeen
import com.oyetech.composebase.projectRadioFeature.views.quotes.uiState.QuotesUiState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.models.quotes.responseModel.QuoteResponseData
import com.oyetech.models.utils.helper.TimeFunctions
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
    private val appDispatchers: AppDispatchers,
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
) :
    BaseListViewModel<QuotesUiState>(appDispatchers) {

    fun getQuotesPager() =

        Pager(
            config = PagingConfig(
                pageSize = 20,
//                enablePlaceholders = false,
//                prefetchDistance = 1,

            ),
            pagingSourceFactory = {
                QuotePagingSource(
                    quoteDataOperationRepository,
                    getOldDataList = {
                        getOldDataList()
                    }
                )
            }
        ).flow.cachedIn(viewModelScope)

    override val complexItemViewState: MutableStateFlow<ComplexItemListState<QuotesUiState>> =
        MutableStateFlow(ComplexItemListState())

    val currentPage = MutableStateFlow(0)

    fun onEvent(event: QuoteListUiEvent) {
        when (event) {
            is LoadMore -> {
                val i =
                    (complexItemViewState.value.getItemSize() - event.currentItem) == 2
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
                QuotesUiState(
                    quoteId = it.quoteId,
                    text = it.text,
                    author = it.author,
                    createdAtString = TimeFunctions.getDateFromLongWithoutHour(it.createdAt),
                    authorImage = it.authorImage,
                    htmlFormatted = it.htmlFormatted,
                    annotatedStringText = it.htmlFormatted.parseAsHtml()
                        .toAnnotatedString()
                )
            }
        }
    }

    fun getOldDataList(): Array<String> {
        val oldList = complexItemViewState.value.items.map { it.quoteId }.toTypedArray()
        return oldList
    }

    private fun getList(
        isFromRefresh: Boolean = false,
        isFromLoadMore: Boolean = false,
    ): Job {
        return viewModelScope.launch(getDispatcherIo()) {
            delay(250)
//            Timber.d(" getRandomRemoteQuote  ==" + isFromRefresh + " " + isFromLoadMore)
            if (!isFromRefresh && !isFromLoadMore) {
                complexItemViewState.updateState { copy(isLoadingInitial = true) }
            }
            if (isFromLoadMore) {
                complexItemViewState.updateState { copy(isLoadingMore = true) }
            }

            val oldList =
                complexItemViewState.value.items.map { it.quoteId }.toTypedArray()
            quoteDataOperationRepository.getQuoteUnseenFlow(oldList).mapToUiState()
                .asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = {
//                            Timber.d(" getRandomRemoteQuote  " + it.size)
                            if (it.isEmpty()) {
                                complexItemViewState.value = ComplexItemListState(
                                    isEmptyList = true
                                )
                            } else {
                                val list = complexItemViewState.value.items
                                val mergedList =
                                    list.toPersistentList().addAll(it)
                                        .distinctBy { model ->
                                            model.quoteId
                                        }.toImmutableList()
//                                Timber.d(" getRandomRemoteQuote  mergedList " + mergedList.size)

                                complexItemViewState.value =
                                    ComplexItemListState(mergedList)
                            }
                        },
                        onFailure = { exception ->
                            Timber.d(" getRandomRemoteQuote  " + exception.message)
                            if (isFromLoadMore) {
                                complexItemViewState.updateState {
                                    copy(
                                        isLoadingMore = false,
                                        isErrorMore = true,
                                        errorMessage = exception.message ?: ""
                                    )
                                }
                            } else {
                                complexItemViewState.value = ComplexItemListState(
                                    isErrorInitial = true,
                                    errorMessage = errorMessage
                                )
                            }
                            // handle error
                            exception.printStackTrace()
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
        val items = complexItemViewState.value.items

        if (items.size > 0) {
            getList(isFromLoadMore = true)
        } else {
            getList()
        }

    }

    private var isVisibleControl = false
    override fun itemVisible(index: Int) {
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

fun Flow<List<QuoteResponseData>>.mapToUiState(): Flow<List<QuotesUiState>> {
    return this.map {
        it.map {
            QuotesUiState(
                quoteId = it.quoteId,
                text = it.text,
                author = it.author,
                createdAtString = TimeFunctions.getDateFromLongWithoutHour(it.createdAt),
                authorImage = it.authorImage,
                htmlFormatted = it.htmlFormatted,
                annotatedStringText = it.htmlFormatted.parseAsHtml().toAnnotatedString()
            )
        }
    }
}
