package com.oyetech.composebase.base.baseGenericList

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseListViewModel<T>(
    private val dispatchers: AppDispatchers,
) : BaseViewModel(dispatchers) {

    abstract val listViewState: MutableStateFlow<GenericListState<T>>

    init {
        viewModelScope.launch(getDispatcherIo()) {
            delay(60)
            loadList()
        }
    }

    private fun loadList(isFromRefresh: Boolean = false) {
        if (!isFromRefresh) {
            listViewState.updateState { copy(isLoadingInitial = true) }
        }

        viewModelScope.launch(dispatchers.io) {
            listViewState.value.dataFlow
                .asResult()
                .collectLatest { result ->
                    Timber.d("ListResulttt== : $result")
                    result.fold({ list ->
                        if (list.isEmpty()) {
                            listViewState.makeEmptyListState()
                        } else {
                            listViewState.updateListItem(list)
                        }
                    }, {
                        listViewState.updateState {
                            copy(
                                errorMessage = it.message ?: "",
                                isErrorInitial = true
                            )
                        }
                    })
                }
        }
    }

    fun refreshList() {
        listViewState.updateState { copy(isRefreshing = true) }
        loadList(isFromRefresh = true)
    }

    fun loadMore() {
        viewModelScope.launch(dispatchers.io) {
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
                        listViewState.updateState {
                            copy(
                                errorMessage = it.message ?: "",
                                isErrorMore = true
                            )
                        }
                    })
                }
        }
    }
//
//    override fun retry() {
//        complexItemViewState.updateState {
//            copy(isLoadingInitial = true)
//        }
//        loadMore()
//    }

}