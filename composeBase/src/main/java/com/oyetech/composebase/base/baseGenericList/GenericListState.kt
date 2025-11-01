package com.oyetech.composebase.base.baseGenericList

import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

data class GenericListState<T>(
    val dataFlow: Flow<List<T>>? = null,
    val items: ImmutableList<T> = emptyList<T>().toImmutableList(),
    val isRefreshEnable: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingInitial: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isErrorInitial: Boolean = false,
    val isErrorMore: Boolean = false,
    val isEmptyList: Boolean = false,
    val endOfList: Boolean = false,
    val errorMessage: String = "",
    val emptyErrorMessage: String = "",
    val errorMessageInitial: String? = "",
    val errorMessageMore: String? = "",
    val skipInitialLoading: Boolean = false,
    val endFlowOperation: Boolean = true,

    val refreshDataFlow: Flow<List<T>> = flowOf(emptyList()),
    val loadMoreFlow: Flow<List<T>> = flowOf(emptyList()),

    val onRefresh: (() -> Unit)? = null,
    val onLoadMore: (() -> Unit)? = null,
    val onRetryInitial: (() -> Unit)? = null,
    val onRetryMore: (() -> Unit)? = null,

    val triggerScrollToPosition: ((Int) -> Unit)? = null,
)

fun <T> MutableStateFlow<GenericListState<T>>.makeEmptyListState(): GenericListState<T> {
    this.updateState {
        copy(
            items = emptyList<T>().toImmutableList(),
            isEmptyList = true,
            errorMessage = "",
            isRefreshing = false,
            isLoadingInitial = false,
            isLoadingMore = false,
            isErrorInitial = false,
            isErrorMore = false,
        )
    }
    return this.value
}

fun <T> MutableStateFlow<GenericListState<T>>.updateListItem(list: List<T>): GenericListState<T> {
    val mergedList = this.value.items.toMutableList()
    mergedList.addAll(list)
    mergedList.distinct()
    this.updateState {
        copy(
            items = mergedList.toImmutableList(),
            isEmptyList = false,
            errorMessage = "",
            isRefreshing = false,
            isLoadingInitial = false,
            isLoadingMore = false,
            isErrorInitial = false,
            isErrorMore = false,
        )
    }
    return this.value
}

fun <T> MutableStateFlow<GenericListState<T>>.setList(list: List<T>): GenericListState<T> {
    this.updateState {
        copy(
            items = list.toImmutableList(),
            isEmptyList = false,
            errorMessage = "",
            isRefreshing = false,
            isLoadingInitial = false,
            isLoadingMore = false,
            isErrorInitial = false,
            isErrorMore = false,
        )
    }
    return this.value
}

fun <T> MutableStateFlow<GenericListState<T>>.updateErrorInitial(errorMessage: Throwable): GenericListState<T> {
    this.updateState {
        copy(
            errorMessage = ErrorHelper.getErrorMessage(errorMessage),
            isRefreshing = false,
            isLoadingInitial = false,
            isLoadingMore = false,
            isErrorInitial = true,
            isErrorMore = false,
        )
    }
    return this.value
}

fun <T> MutableStateFlow<GenericListState<T>>.updateErrorInitial(errorMessage: String): GenericListState<T> {
    this.updateState {
        copy(
            errorMessage = errorMessage,
            isRefreshing = false,
            isLoadingInitial = false,
            isLoadingMore = false,
            isErrorInitial = true,
            isErrorMore = false,
        )
    }
    return this.value
}

fun <T> MutableStateFlow<GenericListState<T>>.updateErrorMore(errorMessage: Throwable): GenericListState<T> {
    this.updateState {
        copy(
            errorMessage = ErrorHelper.getErrorMessage(errorMessage),
            isRefreshing = false,
            isLoadingInitial = false,
            isLoadingMore = false,
            isErrorInitial = false,
            isErrorMore = true,
        )
    }
    return this.value
}

fun <T> MutableStateFlow<GenericListState<T>>.updateSingleItem(
    predicate: (T) -> Boolean,
    transform: (T) -> T,
): GenericListState<T> {
    val currentList = this.value.items.toMutableList()
    val index = currentList.indexOfFirst(predicate)

    if (index != -1) {
        currentList[index] = transform(currentList[index])
        this.updateState {
            copy(items = currentList.toImmutableList())
        }
    }

    return this.value
}
