package com.oyetech.composebase.base.baseList

import com.oyetech.composebase.R
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseList.ListUIEvent.ItemVisible
import com.oyetech.composebase.base.baseList.ListUIEvent.LoadMore
import com.oyetech.composebase.base.baseList.ListUIEvent.Refresh
import com.oyetech.composebase.base.baseList.ListUIEvent.Retry
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-9.11.2024-
-18:43-
 **/

abstract class BaseListViewModel<T>(dispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers) :
    BaseViewModel(dispatchers) {
    abstract val complexItemViewState: MutableStateFlow<ComplexItemListState<T>>
    val errorMessage = context.getString(R.string.searchpreference_no_results)

    open fun loadMoreItem() {}
    open fun refreshList() {}
    open fun retry() {}
    open fun itemVisible(index: Int) {}

    fun handleListEvent(listUiEvent: ListUIEvent) {
        when (listUiEvent) {
            LoadMore -> {
                loadMoreItem()
            }

            Refresh -> {
                refreshList()
            }

            Retry -> {
                retry()
                // Tekrar dene işlemi burada yapılabilir
            }

            is ItemVisible -> {
                itemVisible(listUiEvent.index)
            }
        }

    }


}
//
//
//    private fun getLanguageList(isFromRefresh: Boolean = false) {
//        if (!isFromRefresh) {
//            complexItemViewState.updateState { copy(isLoadingInitial = true) }
//        }
//        complexItemViewState.updateState { ComplexItemListState(isLoadingInitial = true) }
//        viewModelScope.launch(appDispatchers.io) {
//            radioCountryTagOperationUseCase.getLanguagesList().map {
//                lastList = it.toImmutableList()
//                it
//            }.asResult().collectLatest { list1 ->
//                list1.fold(
//                    onSuccess = { list ->
//                        if (list.isEmpty()) {
//                            complexItemViewState.value = ComplexItemListState(
//                                isEmptyList = true
//                            )
//                        } else {
//                            complexItemViewState.value =
//                                ComplexItemListState(
//                                    items = list.mapToUiState(),
//                                )
//                        }
//
//                    },
//                    onFailure = {
//                        complexItemViewState.value = ComplexItemListState(
//                            isErrorInitial = true,
//                            errorMessage = errorMessage
//                        )
//                    }
//                )
//            }
//
//        }
//    }

//