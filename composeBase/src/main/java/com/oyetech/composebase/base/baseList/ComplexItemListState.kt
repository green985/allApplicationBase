package com.oyetech.composebase.base.baseList

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class ComplexItemListState<T>(
    val items: ImmutableList<T> = emptyList<T>().toImmutableList(),
    val isRefreshing: Boolean = false,
    val isLoadingInitial: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isErrorInitial: Boolean = false,
    val isErrorOnMore: Boolean = false,
    val isEmptyList: Boolean = false,
    val errorMessage: String = "",

    val itemSortType: ItemSortType = ItemSortType.DefaultSortType,
) {

    fun makeEmptyListState(): ComplexItemListState<T> {
        return copy(
            items = emptyList<T>().toImmutableList(),
            isEmptyList = true,
            isRefreshing = false,
            isLoadingInitial = false,
            isLoadingMore = false,
            isErrorInitial = false,
            isErrorOnMore = false,
        )
    }

    fun ComplexItemListState<T>.makeSuccessState(
        items: ImmutableList<T>,
    ): ComplexItemListState<T> {
        return copy(
            items = items,
            isRefreshing = false,
            isLoadingInitial = false,
            isLoadingMore = false,
            isErrorInitial = false,
            isErrorOnMore = false,
        )
    }

    fun ComplexItemListState<T>.makeErrorState(errorMessage: String? = null): ComplexItemListState<T> {
        return copy(
            isLoadingInitial = false,
            isRefreshing = false,
            isErrorInitial = true,
            errorMessage = errorMessage ?: this.errorMessage,
        )
    }

    fun ComplexItemListState<T>.makeLoadingState(): ComplexItemListState<T> {
        return copy(
            isLoadingInitial = true,
            isRefreshing = false,
            isErrorInitial = false,
        )
    }

    fun ComplexItemListState<T>.makeLoadingMoreState(): ComplexItemListState<T> {
        return copy(
            isLoadingMore = true,
            isErrorOnMore = false,
        )
    }

    fun ComplexItemListState<T>.makeErrorOnMoreState(errorMessage: String? = null): ComplexItemListState<T> {
        return copy(
            isLoadingMore = false,
            isErrorOnMore = true,
            errorMessage = errorMessage ?: this.errorMessage,
        )
    }

}

data class LoadMoreState(
    val loadMoreRemainCountThreshold: Int,
    val onLoadMore: (() -> Unit),
)

data class LoadableLazyColumnState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val lazyListState: LazyListState,
    val pullRefreshState: PullToRefreshState,
    val loadMoreState: LoadMoreState,
)

sealed class ListUIEvent {
    object LoadMore : ListUIEvent()
    object Refresh : ListUIEvent()
    object Retry : ListUIEvent()
}

sealed class ItemSortType {
    object DefaultSortType : ItemSortType()
    object NameSortType : ItemSortType()
}

fun ItemSortType.changeSortType(): ItemSortType {
    return when (this) {
        ItemSortType.DefaultSortType -> ItemSortType.NameSortType
        ItemSortType.NameSortType -> ItemSortType.DefaultSortType
    }
}
