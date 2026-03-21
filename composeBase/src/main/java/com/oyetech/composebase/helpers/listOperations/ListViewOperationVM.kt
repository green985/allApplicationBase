package com.oyetech.composebase.helpers.listOperations

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class ListViewOperationVM constructor(
    private val dispatchers: AppDispatchers,
    private val exampleEndlessDataFlowUseCase: ExampleEndlessDataFlowUseCase,
) : BaseViewModel(dispatchers) {

    private val listOperationDelegate = ListOperationDelegate(
        scope = viewModelScope,
        dispatcher = getDispatcherIo(),
        initialDataFlow = exampleEndlessDataFlowUseCase.getExampleDataFlow(isInitial = true),
        loadMoreFlow = exampleEndlessDataFlowUseCase.getExampleDataFlow(isInitial = false),
        keySelector = { it.id }
    )

    val listUiState: StateFlow<GenericListState<ExampleObject>> = listOperationDelegate.listUiState

    init {
        Timber.d("ListViewOperationVM created")
    }

    override fun onCleared() {
        super.onCleared()
        listOperationDelegate.cancelJobs()
        Timber.d("ListViewOperationVM cleared")
    }
}

data class ExampleObject(val id: Long, val name: String)
