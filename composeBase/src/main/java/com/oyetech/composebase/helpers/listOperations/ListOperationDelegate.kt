package com.oyetech.composebase.helpers.listOperations

import com.oyetech.composebase.base.baseGenericList.GenericListState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Delegate class to handle list operations like loading initial data, loading more, and error handling.
 */
class ListOperationDelegate<T>(
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    initialDataFlow: Flow<List<T>>,
    private val keySelector: (T) -> Any,
    loadMoreFlow: Flow<List<T>>? = null,
) {

    val listUiState = MutableStateFlow(
        GenericListState<T>(
            dataFlow = initialDataFlow,
            loadMoreFlow = loadMoreFlow,
            onLoadMore = ::loadMore,
            onRetryInitial = ::loadData,
            onRetryMore = { loadMore(isRetry = true) }
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
                items = persistentListOf()
            )
        }

        loadDataJob = scope.launch(dispatcher) {
            listUiState.value.dataFlow?.catch { error ->
                Timber.e("Error in initial data flow: ${error.localizedMessage}")
                listUiState.update {
                    it.copy(
                        isLoadingInitial = false,
                        isErrorInitial = true,
                        errorMessageInitial = error.message,
                        items = persistentListOf(),
                    )
                }
            }?.collectLatest { list ->
                Timber.d("Initial data loaded, size: ${list.size}")
                if (list.isEmpty()) {
                    Timber.d("Initial data is empty")
                    listUiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            isErrorInitial = false,
                            isEmptyList = true,
                            items = list.toImmutableList()
                        )
                    }
                } else {
                    listUiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            isErrorInitial = false,
                            items = list.toImmutableList()
                        )
                    }
                }

                if (listUiState.value.endFlowOperation) {
                    loadDataJob?.cancel()
                }
            }
        }
    }

    fun loadMore(isRetry: Boolean = false) {
        Timber.d("loadMore called, isRetry: $isRetry")
        if (listUiState.value.isLoadingMore) {
            return
        }
        if (isRetry) {
            Timber.d("Retrying load more after error")
            listUiState.update {
                it.copy(
                    endOfList = false,
                    isErrorMore = false
                )
            }
        }

        if (listUiState.value.endOfList) {
            Timber.d("End of list already reached, not loading more")
            return
        }

        if (listUiState.value.isErrorMore) {
            Timber.d("Error enable for load more, not loading more")
            return
        }

        listUiState.update {
            it.copy(
                isLoadingMore = true,
                isErrorMore = false,
                errorMessageMore = null
            )
        }

        loadMoreJob?.cancel()
        loadMoreJob = scope.launch(dispatcher) {
            listUiState.value.loadMoreFlow?.catch { error ->
                Timber.e("Error in load more flow: ${error.localizedMessage}")
                Timber.e("Error in load more flow: ${error.printStackTrace()}")
                listUiState.update {
                    it.copy(
                        isLoadingMore = false,
                        isErrorMore = true,
                        endOfList = true,
                        errorMessageMore = error.message ?: "Error loading more"
                    )
                }
            }?.collectLatest { result ->
                if (result.isNotEmpty()) {
                    val oldList = listUiState.value.items
                    val combined = (oldList + result)

                    // 🔹 keySelector ile duplicate filtreleme
                    val distinctList = combined
                        .distinctBy { keySelector(it) }
                        .toPersistentList()
                    Timber.d("Load more completed, old size: ${oldList.size}, new size: ${distinctList.size}")
                    listUiState.update {
                        it.copy(
                            isLoadingMore = false,
                            items = distinctList,
                            isErrorMore = false
                        )
                    }
                } else {
                    // Empty result means end of list reached
                    Timber.d("End of list reached")
                    listUiState.update {
                        it.copy(
                            isLoadingMore = false,
                            isErrorMore = false,
                            endOfList = true
                        )
                    }
                }

                if (listUiState.value.endFlowOperation) {
                    loadMoreJob?.cancel()
                }
            }
        }
    }

    fun updateList(newList: List<T>) {
        listUiState.update {
            it.copy(
                items = newList.toImmutableList()
            )
        }
    }

    fun updateDataFlows(
        initialDataFlow: Flow<List<T>>,
        loadMoreFlow: Flow<List<T>>,
    ) {
        listUiState.update {
            it.copy(
                dataFlow = initialDataFlow,
                loadMoreFlow = loadMoreFlow
            )
        }
        loadData()
    }

    fun cancelJobs() {
        loadDataJob?.cancel()
        loadMoreJob?.cancel()
    }
}
