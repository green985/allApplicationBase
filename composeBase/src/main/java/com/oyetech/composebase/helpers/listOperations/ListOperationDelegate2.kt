package com.oyetech.composebase.helpers.listOperations

import com.oyetech.composebase.base.baseGenericList.GenericListState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListOperationDelegate2<T>(
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    initialDataFlow: Flow<List<T>>,
    private val keySelector: (T) -> Any,
    loadMoreFlow: Flow<List<T>>? = null,
) {
    val listUiState = MutableStateFlow(
        GenericListState(
            dataFlow = initialDataFlow,
            loadMoreFlow = loadMoreFlow,
            onLoadMore = ::loadMore,
            onRetryInitial = ::loadData,
            onRetryMore = { loadMore(isRetry = true) },
        )
    )

    private var loadDataJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        loadDataJob?.cancel()
        listUiState.update {
            it.copy(
                isLoadingInitial = true,
                isErrorInitial = false,
                isEmptyList = false,
                items = persistentListOf(),
            )
        }

        loadDataJob = scope.launch(dispatcher) {
            listUiState.value.dataFlow
                ?.catch { error ->
                    listUiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            isErrorInitial = true,
                            errorMessage = error.message.orEmpty(),
                            items = persistentListOf(),
                        )
                    }
                }
                ?.collectLatest { items ->
                    listUiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            isErrorInitial = false,
                            isEmptyList = items.isEmpty(),
                            items = items.toImmutableList(),
                        )
                    }
                }
        }
    }

    fun loadMore(isRetry: Boolean = false) {
        val state = listUiState.value
        if (state.isLoadingMore || state.endOfList || state.loadMoreFlow == null) return

        if (isRetry) {
            listUiState.update { it.copy(isErrorMore = false, errorMessage = "") }
        }

        listUiState.update {
            it.copy(
                isLoadingMore = true,
                isErrorMore = false,
                errorMessage = "",
            )
        }

        loadMoreJob?.cancel()
        loadMoreJob = scope.launch(dispatcher) {
            state.loadMoreFlow
                .catch { error ->
                    listUiState.update {
                        it.copy(
                            isLoadingMore = false,
                            isErrorMore = true,
                            errorMessage = error.message.orEmpty(),
                        )
                    }
                }
                .collectLatest { newItems ->
                    if (newItems.isEmpty()) {
                        listUiState.update {
                            it.copy(
                                isLoadingMore = false,
                                isErrorMore = false,
                                endOfList = true,
                            )
                        }
                    } else {
                        val items = (listUiState.value.items + newItems)
                            .distinctBy(keySelector)
                            .toImmutableList()
                        listUiState.update {
                            it.copy(
                                isLoadingMore = false,
                                isErrorMore = false,
                                items = items,
                            )
                        }
                    }
                }
        }
    }

    fun updateList(items: List<T>) {
        listUiState.update { it.copy(items = items.toImmutableList()) }
    }

    fun cancelJobs() {
        loadDataJob?.cancel()
        loadMoreJob?.cancel()
    }
}
