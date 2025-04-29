package com.oyetech.composebase.base.baseGenericList

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseListViewModel<T>(
    private val dispatchers: AppDispatchers,
    private val autoStartLoading: Boolean = true,
) : BaseViewModel(dispatchers) {

    abstract val listViewState: MutableStateFlow<GenericListState<T>>

    var loadJob: Job? = null
    var loadMoreJob: Job? = null

    init {
        if (autoStartLoading) {
            viewModelScope.launch(getDispatcherIo()) {
                delay(60)
                loadList()
            }
        }
    }

    fun loadList(isFromRefresh: Boolean = false) {
        if (!isFromRefresh) {
            listViewState.updateState { copy(isLoadingInitial = true) }
        }

        loadJob?.cancel()
        loadJob = viewModelScope.launch(dispatchers.io) {
            listViewState.value.dataFlow?.asResult()?.collectLatest { result ->
                Timber.d("ListResulttt== : ${result.isSuccess}")
                result.fold({ list ->
                    if (list.isEmpty()) {
                        listViewState.makeEmptyListState()
                    } else {
                        listViewState.setList(list)
                    }
                }, {
                    listViewState.updateErrorInitial(it)
                })
            }
        }
    }

    fun refreshList() {
        listViewState.updateState { copy(isRefreshing = true) }
        loadList(isFromRefresh = true)
    }

    fun loadMore() {
        listViewState.updateState { copy(isLoadingMore = true) }
        Timber.d("loadMore")
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch(dispatchers.io) {
            listViewState.value.loadMoreDataFlow
                .asResult()
                .collectLatest { result ->
                    result.fold({ list ->
                        if (list.isEmpty()) {
                            // do nothing now
                        } else {
                            listViewState.updateListItem(list)
                        }
                    }, {
                        listViewState.updateErrorMore(it)
                    })
                }
        }
    }

    fun retry() {
        // todo analytics
        loadList()
    }

}